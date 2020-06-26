package com.oracle.dstram

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author duifsuefi    
  */
object StatefulWordcount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("statefulWordcount")
    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.checkpoint(".")
    val lines = ssc.socketTextStream("CentOS102", 6789)
    lines.flatMap(_.split(" ")).map((_, 1)).updateStateByKey(updateFunction _).print()

    ssc.start()
    ssc.awaitTermination()
  }

  def updateFunction(cru: Seq[Int], pre: Option[Int]): Option[Int] = {
    val crucount = cru.sum
    val precount = pre.getOrElse(0)
    Some(crucount + precount)
  }
}
