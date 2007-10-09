/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.calendar.service;

import org.exoplatform.commons.utils.ISO8601;

/**
 * Created by The eXo Platform SARL
 * Author : Hung Nguyen Quang
 *          hung.nguyen@exoplatform.com
 * Jul 11, 2007  
 */
public class EventQuery {
  private String eventType ;
  private String text = null ;
  private String[] categoryIds = null ;
  private String[] calendarIds = null ;
  private java.util.Calendar fromDate  = null ;
  private java.util.Calendar toDate = null ;
  private String calendarPath ;
  private String priority ;
  private String state ;
  private String[] orderBy ;
  private String orderType ;
  
  public String getEventType() { return eventType ; }
  
  public void setText(String fullTextSearch) { this.text = fullTextSearch ; }
  public String getText() { return text ; }
  
  public void setEventType(String eventType) { this.eventType = eventType ; }
  
  public String[] getCategoryId() { return categoryIds ; }
  public void setCategoryId(String[] categoryIds) { this.categoryIds = categoryIds ; }
  
  public String[] getCalendarId() { return calendarIds ; }
  public void setCalendarId(String[] calendarIds) { this.calendarIds = calendarIds ; }
  
  public java.util.Calendar getFromDate() { return fromDate ; }
  public void setFromDate(java.util.Calendar fromDate) { this.fromDate = fromDate ; }
  
  public java.util.Calendar getToDate() { return toDate ; }
  public void setToDate(java.util.Calendar toDate) { this.toDate = toDate ; }
  
  public String getCalendarPath() { return calendarPath ; }
  public void setCalendarPath(String calendarPath) { this.calendarPath = calendarPath ; }
  
  public String getPriority() { return priority ; }
  public void setPriority(String priority) { this.priority = priority ; }
  
  public String getState() { return state ; }
  public void setState(String st) { this.state = st ; }
  
  public String[] getOrderBy() { return orderBy ; }
  public void setOrderBy(String[] order) { this.orderBy = order ; }
  
  public String getOrderType() { return orderType ; }
  public void setOrderType(String type) { this.orderType = type ; }
  
  public String getQueryStatement() throws Exception {
    StringBuffer queryString = new StringBuffer("/jcr:root" + calendarPath + "//element(*,exo:calendarEvent)") ;
    boolean hasConjuntion = false ;
    StringBuffer stringBuffer = new StringBuffer("[") ;
    //desclared full text query
    if(text != null && text.length() > 0) {
      stringBuffer.append("jcr:contains(., '").append(text).append("')") ;
      hasConjuntion = true ;
    }    
    //desclared event type query
    if(eventType != null && eventType.length() > 0) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;    
      stringBuffer.append("@exo:eventType='" + eventType +"'") ;
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }
    //desclared priority query
    if(priority != null && priority.length() > 0) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;    
      stringBuffer.append("@exo:priority='" + priority +"'") ;
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }
    //desclared state query
    if(state != null && state.length() > 0) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;    
      stringBuffer.append("@exo:eventState='" + state +"'") ;
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }
    //desclared category query
    if(categoryIds != null && categoryIds.length > 0) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;    
      for(int i = 0; i < categoryIds.length; i ++) {
        if(i ==  0) stringBuffer.append("@exo:eventCategoryId='" + categoryIds[i] +"'") ;
        else stringBuffer.append(" or @exo:eventCategoryId='" + categoryIds[i] +"'") ;
      }
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }
    // desclared calendar query
    if(calendarIds != null && calendarIds.length > 0) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;
      for(int i = 0; i < calendarIds.length; i ++) {
        if(i == 0) stringBuffer.append("@exo:calendarId='" + calendarIds[i] +"'") ;
        else stringBuffer.append(" or @exo:calendarId='" + calendarIds[i] +"'") ;
      }
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }
    
    // desclared Date time
    if(fromDate != null && toDate != null){
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;
      stringBuffer.append("(") ;
      stringBuffer.append("@exo:fromDateTime >= xs:dateTime('"+ISO8601.format(fromDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime <= xs:dateTime('"+ISO8601.format(toDate)+"')") ;
      stringBuffer.append(") or (") ;
      stringBuffer.append("@exo:fromDateTime < xs:dateTime('"+ISO8601.format(fromDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime > xs:dateTime('"+ISO8601.format(toDate)+"')") ;
      stringBuffer.append(") or (") ;
      stringBuffer.append("@exo:fromDateTime < xs:dateTime('"+ISO8601.format(fromDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime > xs:dateTime('"+ISO8601.format(fromDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime < xs:dateTime('"+ISO8601.format(toDate)+"')") ;
      stringBuffer.append(") or (") ;
      stringBuffer.append("@exo:fromDateTime > xs:dateTime('"+ISO8601.format(fromDate)+"') and ") ;
      stringBuffer.append("@exo:fromDateTime < xs:dateTime('"+ISO8601.format(toDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime > xs:dateTime('"+ISO8601.format(toDate)+"')") ;
      stringBuffer.append(")") ;
      stringBuffer.append(")") ;  
      hasConjuntion = true ;
    }else if(fromDate != null) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;
      stringBuffer.append("(") ;
      stringBuffer.append("@exo:fromDateTime >= xs:dateTime('"+ISO8601.format(fromDate)+"')") ;
      stringBuffer.append(") or (") ;
      stringBuffer.append("@exo:fromDateTime < xs:dateTime('"+ISO8601.format(fromDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime > xs:dateTime('"+ISO8601.format(fromDate)+"')") ;
      stringBuffer.append(")") ;
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }else if(toDate != null) {
      if(hasConjuntion) stringBuffer.append(" and (") ;
      else stringBuffer.append("(") ;
      stringBuffer.append("(") ;
      stringBuffer.append("@exo:toDateTime <= xs:dateTime('"+ISO8601.format(toDate)+"')") ;
      stringBuffer.append(") or (") ;
      stringBuffer.append("@exo:fromDateTime < xs:dateTime('"+ISO8601.format(toDate)+"') and ") ;
      stringBuffer.append("@exo:toDateTime > xs:dateTime('"+ISO8601.format(toDate)+"')") ;
      stringBuffer.append(")") ;
      stringBuffer.append(")") ;
      hasConjuntion = true ;
    }
    stringBuffer.append("]") ;
    //declared order by
    if(orderBy != null && orderBy.length > 0 && orderType != null && orderType.length() > 0) {
      for(int i = 0; i < orderBy.length; i ++) {
        if(i == 0) stringBuffer.append(" order by " + orderBy[i] + " " + orderType) ;
        else stringBuffer.append(", order by " + orderBy[i] + " " + orderType) ;
      }
      hasConjuntion = true ;
    }
    if(hasConjuntion) queryString.append(stringBuffer.toString()) ;
    return queryString.toString() ;    
  }
}
