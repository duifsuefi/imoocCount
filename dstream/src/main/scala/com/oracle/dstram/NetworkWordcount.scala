package com.oracle.dstram

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author duifsuefi    
  */
object NetworkWordcount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))
    val lines = ssc.socketTextStream("CentOS102", 6789)
    val result=lines.flatMap(x=>x.split(" ")).map((_,1)).print()


    ssc.start()
    ssc.awaitTermination()
  }
}
