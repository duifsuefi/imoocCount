package com.oracle.streamingproject.DAO

import com.oracle.streamingproject.domain.CourseClickCount
import depend.HBaseUtil
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * @author duifsuefi    
  */
object CourseClickCountDAO {
  val tableName = "imooc_course_clickcount"
  val cf = "info"
  val qualifer = "click_count"

  def save(listBuffer: ListBuffer[CourseClickCount]): Unit = {
    val table = HBaseUtil.getInstance().gettable(tableName)
    for (ele <- listBuffer) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count.toLong)
    }
  }

  def count(day_course: String): Long = {
    val table = HBaseUtil.getInstance().gettable(tableName)
    val get = new Get(Bytes.toBytes(day_course))
    val value = table.get(get).getValue(Bytes.toBytes(cf), Bytes.toBytes(qualifer))
    if (value == null) 0L
    else Bytes.toLong(value)
  }

}
