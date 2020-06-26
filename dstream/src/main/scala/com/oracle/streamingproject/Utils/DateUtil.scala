package com.oracle.streamingproject.Utils

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
  * @author duifsuefi    
  */
object  DateUtil {

  val YYYYMMDDHHMMSS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val TARGET_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss")
  def getTime(time:String)={
    YYYYMMDDHHMMSS.parse(time).getTime
  }

  def parseTo(time:String)={
    TARGET_FORMAT.format(new Date(getTime(time)))
  }

}
