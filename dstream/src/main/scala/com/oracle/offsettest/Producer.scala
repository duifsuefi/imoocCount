package com.oracle.offsettest

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * @author duifsuefi    
  */
object Producer {
  def main(args: Array[String]): Unit = {
    val properties = new Properties();
    properties.put("bootstrap.servers", "CentOS102:9092")
    properties.put("acks", "all")
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    val producer = new KafkaProducer[String,String](properties);
   for(i <- 1 to 500) {
      producer.send(new ProducerRecord[String, String]("offset",Integer.toString(i),"hello world-" + i));
      println("OK")
   }
    producer.close()
  }

}
