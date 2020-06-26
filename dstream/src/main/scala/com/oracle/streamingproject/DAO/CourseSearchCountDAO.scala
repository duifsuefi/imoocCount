package com.oracle.streamingproject.DAO

import com.oracle.streamingproject.domain.CourseSearchCount
import depend.HBaseUtil
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * @author duifsuefi    
  */
object CourseSearchCountDAO {
  val tableName = "imooc_course_search_clickcount"
  val cf = "info"
  val qualifer = "click_count"

  def save(listBuffer: ListBuffer[CourseSearchCount]): Unit = {
    val table = HBaseUtil.getInstance().gettable(tableName)
    for (ele <- listBuffer) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_search_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count.toLong)
    }
  }

  def count(day_search_course: String): Long = {
    val table = HBaseUtil.getInstance().gettable(tableName)
    val get = new Get(Bytes.toBytes(day_search_course))
    val value = table.get(get).getValue(Bytes.toBytes(cf), Bytes.toBytes(qualifer))
    if (value == null) 0L
    else Bytes.toLong(value)
  }

  def main(args: Array[String]): Unit = {
    val list=new ListBuffer[CourseSearchCount]
    list.append(CourseSearchCount("20171111_www.baidu.com_85",5000))
    save(list)
    println(count("20171111_www.baidu.com_85"))
  }
}
