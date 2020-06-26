package com.oracle.dstram

import java.sql.{Connection, DriverManager}

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author duifsuefi    
  */
object ForeachRdd2MySQL {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))
    val lines = ssc.socketTextStream("CentOS102", 6789)
    val rdds = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    rdds.foreachRDD(rdd => {
      rdd.foreachPartition(records => {
        Class.forName("com.mysql.jdbc.Driver")
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis", "root", "root")
        records.foreach(redord => {
          val sql = "insert into user(word,wordcount) values ('" + redord._1 + "'," + redord._2 + ")"
          conn.createStatement().execute(sql)
        })
      })
    })
    ssc.start()
    ssc.awaitTermination()
  }

}
