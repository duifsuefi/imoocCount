package com.oracle.dstram

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author duifsuefi    
  */
object TransformApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))
    val lines = ssc.socketTextStream("CentOS102", 6789)
    val blacks=List("zs","ls")
    val blackrdd=ssc.sparkContext.makeRDD(blacks).map((_,true))
    val result=lines.map(x=>{
      (x.split(",")(1),x)
    }).foreachRDD(rdd=>{
      val tmp=rdd.leftOuterJoin(blackrdd).filter(x=>x._2._2.getOrElse(false) != true).map(rdd=>rdd._2._1)
      tmp.foreach(println(_))
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
