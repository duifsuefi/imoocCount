package com.oracle.streamingproject

import com.oracle.streamingproject.DAO.{CourseClickCountDAO, CourseSearchCountDAO}
import com.oracle.streamingproject.Utils.DateUtil
import com.oracle.streamingproject.domain.{ClickLog, CourseClickCount, CourseSearchCount}
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

/**
  * @author duifsuefi    
  */
object StateStreamingApp {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "CentOS102:9092,CentOS103:9092,CentOS104:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test2",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("streaming")
    val fromOffsets = Map[TopicAndPartition, Long]()


    val massage = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))

    massage.foreachRDD(RDD => {
      if (!RDD.isEmpty()) {
        val offsetRanges = RDD.asInstanceOf[HasOffsetRanges].offsetRanges
        val logs = RDD.map(_.value())
        val cleanData = logs.map(line => {
          val infos = line.split("\t")
          val url = infos(2).split(" ")(1)
          var courseId = 0

          if (url.startsWith("/class")) {
            val tmp = url.split("/")(2)
            courseId = tmp.substring(0, tmp.lastIndexOf(".")).toInt
          }

          ClickLog(infos(0), DateUtil.parseTo(infos(1)), courseId, infos(3).toInt, infos(4))
        }).filter(clickLog => clickLog.courseID != 0)

        cleanData.map(log => (log.time.substring(0, 8) + "_" + log.courseID, 1)).reduceByKey(_ + _).foreachPartition(records => {
          val list = new ListBuffer[CourseClickCount]
          records.foreach(pair => {
            list.append(CourseClickCount(pair._1, pair._2.toLong))
          })
          CourseClickCountDAO.save(list)
        })


        cleanData.map(log => {
          val referer = log.referer.replaceAll("//", "/")
          val words = referer.split("/")
          var search = ""
          if (referer.length > 2) search = words(1)
          (log.time, log.courseID, search)
        }).filter(tuple => tuple._3 != "").map(x => (x._1.substring(0, 8) + "_" + x._3 + "_" + x._2, 1)).reduceByKey(_ + _).foreachPartition(records => {
          val list = new ListBuffer[CourseSearchCount]
          records.foreach(pair => {
            list.append(CourseSearchCount(pair._1, pair._2.toLong))
          })
          CourseSearchCountDAO.save(list)
        })


        massage.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }
    })


    ssc.start()
    ssc.awaitTermination()
  }
}