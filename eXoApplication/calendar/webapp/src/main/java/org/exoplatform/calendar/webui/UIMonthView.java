/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 **/
package org.exoplatform.calendar.webui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.calendar.CalendarUtils;
import org.exoplatform.calendar.service.CalendarEvent;
import org.exoplatform.calendar.service.CalendarService;
import org.exoplatform.calendar.service.EventQuery;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormCheckBoxInput;

/**
 * Created by The eXo Platform SARL
 * Author : Hung Nguyen
 *          hung.nguyen@exoplatform.com
 * Aus 01, 2007 2:48:18 PM 
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "app:/templates/calendar/webui/UIMonthView.gtmpl", 
    events = {
      @EventConfig(listeners = UICalendarView.AddEventActionListener.class),      
      @EventConfig(listeners = UICalendarView.DeleteEventActionListener.class, confirm="UICalendarVIew.msg.confirm-delete"),
      @EventConfig(listeners = UICalendarView.ChangeCategoryActionListener.class), 
      @EventConfig(listeners = UICalendarView.EventSelectActionListener.class), 
      @EventConfig(listeners = UICalendarView.AddCategoryActionListener.class),
      @EventConfig(listeners = UICalendarView.ViewActionListener.class),
      @EventConfig(listeners = UICalendarView.EditActionListener.class), 
      @EventConfig(listeners = UICalendarView.DeleteActionListener.class, confirm="UICalendarVIew.msg.confirm-delete"),
      @EventConfig(listeners = UICalendarView.GotoDateActionListener.class), 
      @EventConfig(listeners = UICalendarView.QuickAddActionListener.class), 
      @EventConfig(listeners = UICalendarView.MoveNextActionListener.class), 
      @EventConfig(listeners = UICalendarView.MovePreviousActionListener.class),
      @EventConfig(listeners = UICalendarView.SwitchViewActionListener.class),
      @EventConfig(listeners = UIMonthView.UpdateEventActionListener.class)
    }
)
public class UIMonthView extends UICalendarView {
  private Map<String, String> calendarIds_ = new HashMap<String, String>() ;
  private LinkedHashMap<String, CalendarEvent> dataMap_ = new LinkedHashMap<String, CalendarEvent>() ;
  public UIMonthView() throws Exception{
    super() ;
  }

  protected int getWeeksOfTheMonth(int year, int month, int day) {
    Calendar cal = CalendarUtils.getInstanceTempCalendar() ;
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DATE, day);
    return cal.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH) ;
  }
  protected void refreshEvents() throws Exception {
    CalendarService calendarService = CalendarUtils.getCalendarService() ;
    String username = Util.getPortalRequestContext().getRemoteUser() ;
    EventQuery eventQuery = new EventQuery() ;
    eventQuery.setFromDate(getBeginDateOfMonthView()) ;
    eventQuery.setToDate(getEndDateOfMonthView()) ;
    List<CalendarEvent> allEvents = calendarService.getEvent(getSystemSession(), username, eventQuery, getPublicCalendars()) ;
    Iterator childIter = getChildren().iterator() ;
    while(childIter.hasNext()) {
      UIComponent comp = (UIComponent)childIter.next() ;
      if (comp instanceof UIFormCheckBoxInput ) {
        childIter.remove() ;
      }
    }
    dataMap_.clear() ;
    Iterator<CalendarEvent> eventIter = allEvents.iterator() ;
    while(eventIter.hasNext()) {
      CalendarEvent ce = (CalendarEvent)eventIter.next() ; 
      dataMap_.put(ce.getId(), ce) ;
      UIFormCheckBoxInput<Boolean> input = new UIFormCheckBoxInput<Boolean>(ce.getId(), ce.getId(), false) ;
      input.setBindingField(ce.getCalendarId()) ;
      addChild(input) ;
      eventIter.remove() ;
    }
  }
  /*protected void addCalendarId(String id) {calendarIds_.put(id,id) ;}
  protected Map<String, String> getCalendarIds() {return calendarIds_ ;}
  protected void refreshSelectedCalendarIds() throws Exception {
    CalendarService calendarService = getApplicationComponent(CalendarService.class) ;
    String username = Util.getPortalRequestContext().getRemoteUser() ;
    for(Calendar c : calendarService.getUserCalendars(SessionsUtils.getSessionProvider(), username, false)) {
      if(c != null) addCalendarId(c.getId()) ;
    }
  }*/

  public void refresh() throws Exception {
    System.out.println("\n\n>>>>>>>>>> MONTH VIEW") ;
    //refreshSelectedCalendarIds() ;
    refreshEvents() ;

    //System.out.println("\n\n Begin month view " + getBeginDateOfMonthView().getTime());
    //System.out.println("\n\n End month view " + getEndDateOfMonthView().getTime());

  }
  public java.util.Calendar getBeginDateOfMonthView() throws Exception{
    java.util.Calendar temCal = getBeginDateOfMonth() ;
    int amount = temCal.getFirstDayOfWeek() - temCal.get(java.util.Calendar.DAY_OF_WEEK) ;
    temCal.add(java.util.Calendar.DATE, amount) ;
    return getBeginDay(temCal) ;
  }

  public java.util.Calendar getEndDateOfMonthView() throws Exception{
    java.util.Calendar temCal = getEndDateOfMonth() ;
    int amount = temCal.getMaximum(java.util.Calendar.DAY_OF_WEEK) - temCal.get(java.util.Calendar.DAY_OF_WEEK) ; 
    temCal.add(java.util.Calendar.DATE, amount) ;
    return getEndDay(temCal) ;
  }

  public java.util.Calendar getBeginDateOfMonth() throws Exception{
    java.util.Calendar temCal = CalendarUtils.getInstanceTempCalendar() ;
    temCal.setTime(calendar_.getTime()) ;
    temCal.setFirstDayOfWeek(java.util.Calendar.SUNDAY) ;
    temCal.set(java.util.Calendar.DATE, 1) ;
    return getBeginDay(temCal) ;
  }

  public java.util.Calendar getEndDateOfMonth() throws Exception{
    java.util.Calendar temCal = CalendarUtils.getInstanceTempCalendar() ;
    temCal.setTime(calendar_.getTime()) ;
    temCal.setFirstDayOfWeek(java.util.Calendar.SUNDAY) ;
    temCal.set(java.util.Calendar.DATE, getDaysInMonth()) ;
    return getEndDay(temCal) ;
  }

  protected List<CalendarEvent> getSelectedEvents() {
    List<CalendarEvent> events = new ArrayList<CalendarEvent>() ;
    for(String id : dataMap_.keySet()) {
      UIFormCheckBoxInput<Boolean>  checkbox = getChildById(id )  ;
      if(checkbox != null && checkbox.isChecked()) events.add(dataMap_.get(id)) ;
    }
    return events ; 
  }
  public LinkedHashMap<String, CalendarEvent> getDataMap() {
    return dataMap_ ;
  }
  static  public class ChangeViewActionListener extends EventListener<UIMonthView> {
    public void execute(Event<UIMonthView> event) throws Exception {
      UIMonthView calendarview = event.getSource() ;
      UICalendarViewContainer uiContainer = calendarview.getAncestorOfType(UICalendarViewContainer.class) ;
      uiContainer.setRenderedChild(UIDayView.class) ;
      uiContainer.refresh() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiContainer) ;
    }
  }
  static  public class UpdateEventActionListener extends EventListener<UIMonthView> {
    public void execute(Event<UIMonthView> event) throws Exception {
      System.out.println("UpdateEventActionListener");
      UIMonthView calendarview = event.getSource() ;
      UICalendarPortlet uiPortlet = calendarview.getAncestorOfType(UICalendarPortlet.class) ;
      String username = event.getRequestContext().getRemoteUser() ;
      String value = event.getRequestContext().getRequestParameter(OBJECTID) ;
      String eventId = event.getRequestContext().getRequestParameter(EVENTID) ;
      String calendarId = event.getRequestContext().getRequestParameter(CALENDARID) ;
      try {
        CalendarEvent calEvent = calendarview.getDataMap().get(eventId); 
        if(calEvent != null) {
          java.util.Calendar tempCalFrom = CalendarUtils.getInstanceTempCalendar() ;
          tempCalFrom.setTimeInMillis((Long.parseLong(value))) ;
          java.util.Calendar cal = CalendarUtils.getInstanceTempCalendar() ;
          Long amount = calEvent.getToDateTime().getTime() - calEvent.getFromDateTime().getTime() ;
          cal.setTime(calEvent.getFromDateTime()) ;
          cal.set(Calendar.DATE, tempCalFrom.get(Calendar.DATE)) ;
          cal.set(Calendar.MONTH, tempCalFrom.get(Calendar.MONTH)) ;
          cal.set(Calendar.YEAR, tempCalFrom.get(Calendar.YEAR)) ;
          calEvent.setFromDateTime(cal.getTime()) ;
          cal.setTimeInMillis(calEvent.getFromDateTime().getTime() + amount) ;
          calEvent.setToDateTime(cal.getTime()) ;
          if(calEvent.getCalType().equals(CalendarUtils.PRIVATE_TYPE)) {
            CalendarUtils.getCalendarService().saveUserEvent(calendarview.getSession(), username, calendarId, calEvent, false) ;
          }else if(calEvent.getCalType().equals(CalendarUtils.SHARED_TYPE)){
            CalendarUtils.getCalendarService().saveEventToSharedCalendar(calendarview.getSystemSession(), username, calendarId, calEvent, false) ;
          }else if(calEvent.getCalType().equals(CalendarUtils.PUBLIC_TYPE)){
            CalendarUtils.getCalendarService().savePublicEvent(calendarview.getSystemSession(), calendarId, calEvent, false) ;          
          }
          UIMiniCalendar uiMiniCalendar = uiPortlet.findFirstComponentOfType(UIMiniCalendar.class) ;
          uiMiniCalendar.updateMiniCal() ;
          event.getRequestContext().addUIComponentToUpdateByAjax(uiMiniCalendar) ;
          calendarview.refresh() ;
          //calendarview.dataMap_.put(calEvent.getId(), calEvent) ;
          event.getRequestContext().addUIComponentToUpdateByAjax(calendarview.getParent()) ; 
        }
      } catch (Exception e) {
        e.printStackTrace() ;
      }
    }
  }
}
