/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
 */
package org.exoplatform.cs.ext.impl;

import org.exoplatform.calendar.service.Calendar;
import org.exoplatform.calendar.service.CalendarEvent;
import org.exoplatform.calendar.service.impl.CalendarEventListener;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 30, 2010  
 */
public class CalendarSpaceActivityPublisher extends CalendarEventListener {
  private Log LOG = ExoLogger.getLogger(CalendarSpaceActivityPublisher.class);
  
  public void savePublicEvent(CalendarEvent event, String calendarId) {
    try {
      Class.forName("org.exoplatform.social.core.manager.IdentityManager") ;
      String msg = "new event " + event.getSummary() + " has been added!"; 
      String body = event.getDescription();
      IdentityManager indentityM = (IdentityManager) PortalContainer.getInstance().getComponentInstanceOfType(IdentityManager.class); 
      ActivityManager activityM = (ActivityManager) PortalContainer.getInstance().getComponentInstanceOfType(ActivityManager.class);
      String spaceId = calendarId.split(Calendar.CALENDAR_PREF)[1]; 
      Identity spaceIdentity = indentityM.getOrCreateIdentity(SpaceIdentityProvider.NAME, spaceId, false);
      activityM.recordActivity(spaceIdentity, SpaceService.SPACES_APP_ID, msg , body);
    } catch (ClassNotFoundException e) {
      if(LOG.isDebugEnabled()) LOG.debug("Please check the integrated project does the social deploy? " +e.getMessage());
    } catch (Exception e) {
      LOG.error("Can not record Activity for space when event added " +e.getMessage());
    }
    
  }
    public void updatePublicEvent(CalendarEvent event, String calendarId) {
      try {
        Class.forName("org.exoplatform.social.core.manager.IdentityManager") ;
        String msg = "the event "+ event.getSummary() + " has been updated!"; 
        String body = event.getDescription();
        IdentityManager indentityM = (IdentityManager) PortalContainer.getInstance().getComponentInstanceOfType(IdentityManager.class); 
        ActivityManager activityM = (ActivityManager) PortalContainer.getInstance().getComponentInstanceOfType(ActivityManager.class);
        String spaceId = calendarId.split(Calendar.CALENDAR_PREF)[1]; 
        Identity spaceIdentity = indentityM.getOrCreateIdentity(SpaceIdentityProvider.NAME, spaceId, false);
        activityM.recordActivity(spaceIdentity, SpaceService.SPACES_APP_ID, msg , body);
      } catch (ClassNotFoundException e) {
        if(LOG.isDebugEnabled()) LOG.debug("Please check the integrated project does the social deploy? " +e.getMessage());
      } catch (Exception e) {
        LOG.error("Can not record Activity for space when event updated " +e.getMessage());
      }

    }


  
}
