/*
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
 */
package org.exoplatform.mail.webui.popup;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.exoplatform.cs.common.webui.UIPopupAction;
import org.exoplatform.cs.common.webui.UIPopupComponent;
import org.exoplatform.mail.DataCache;
import org.exoplatform.mail.MailUtils;
import org.exoplatform.mail.service.Folder;
import org.exoplatform.mail.service.MailService;
import org.exoplatform.mail.service.Message;
import org.exoplatform.mail.webui.UIFolderContainer;
import org.exoplatform.mail.webui.UIMailPortlet;
import org.exoplatform.mail.webui.UIMessageArea;
import org.exoplatform.mail.webui.UIMessageList;
import org.exoplatform.mail.webui.UIMessagePreview;
import org.exoplatform.mail.webui.UISelectFolder;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;


/**
 * Created by The eXo Platform SARL
 * Author : HAI NGUYEN
 *          haiexo1002@gmail.com
 * September 14, 2007 2:48:18 PM 
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "app:/templates/mail/webui/popup/UIMoveMessageForm.gtmpl",
    events = {
      @EventConfig(listeners = UIMoveMessageForm.SaveActionListener.class), 
      @EventConfig(listeners = UIMoveMessageForm.CancelActionListener.class)
    }
)
public class UIMoveMessageForm extends UIForm implements UIPopupComponent {
  private static final Log log = ExoLogger.getExoLogger(UIMoveMessageForm.class);
  
  final public static String FIELD_NAME = "folderName" ;
  final public static String SELECT_FOLDER = "folder" ;
  public static String folderId="";
  public List<Message> messageList=new ArrayList<Message>();  
  
  public UIMoveMessageForm() throws Exception { }
  
  public void init(String accountId) throws Exception {
    UISelectFolder uiSelectFolder= new UISelectFolder() ;
    addUIFormInput(uiSelectFolder);
    uiSelectFolder.init(accountId) ;
  }
  
  public void setMessageList(List<Message> messageList){
    this.messageList= messageList; 
  }
  
  public List<Message> getMessageList(){ return messageList; }
 
  public String getFolderId() throws Exception { return folderId; }
  
  public String getSelectedFolderId(){ return folderId; }
  
  static public class SaveActionListener extends EventListener<UIMoveMessageForm> {
    public void execute(Event<UIMoveMessageForm> event) throws Exception {
      UIMoveMessageForm uiMoveMessageForm = event.getSource();
      UIMailPortlet uiPortlet = uiMoveMessageForm.getAncestorOfType(UIMailPortlet.class);
      UIFolderContainer folderContainer = uiPortlet.findFirstComponentOfType(UIFolderContainer.class);
      DataCache dataCache = uiPortlet.getDataCache();

      UIMessageList uiMessageList = uiPortlet.findFirstComponentOfType((UIMessageList.class));
      UIMessagePreview uiMsgPreview = uiPortlet.findFirstComponentOfType(UIMessagePreview.class);
      String accountId = dataCache.getSelectedAccountId();
      String destFolderId = uiMoveMessageForm.getChild(UISelectFolder.class).getSelectedValue();
      Folder destFolder = null;
      String username = MailUtils.getDelegateFrom(accountId, dataCache);

      List<Message> successes = new ArrayList<Message>();
      try {
        destFolder = folderContainer.getFolderById(destFolderId);
      } catch (PathNotFoundException e) {
        if (log.isDebugEnabled()) {
          log.debug("PathNotFoundException in getting des folder", e);
        }
      }
      
      if (destFolder == null) {
        event.getRequestContext()
             .getUIApplication()
             .addMessage(new ApplicationMessage("UIMoveMessageForm.msg.selected-folder-is-not-exits-any-more", null));
        uiPortlet.cancelAction();
        return;
      }
      
      List<Message> appliedMsgList = uiMoveMessageForm.getMessageList();
      MailService mailSrv = uiMoveMessageForm.getApplicationComponent(MailService.class);
      String fromFolderId = folderContainer.getSelectedFolder();
      if (fromFolderId != null) {
        successes = mailSrv.moveMessages(username, accountId, uiMoveMessageForm.getMessageList(), fromFolderId, destFolderId);
      } else {
        for (Message message : uiMoveMessageForm.getMessageList()) {
          Message m = mailSrv.moveMessage(username, accountId, message, message.getFolders()[0], destFolderId);
          if (m == null) {
            successes.add(null);
          }
        }
      }
      
      if (successes == null || (successes.size() > 0 && successes.size() < uiMoveMessageForm.getMessageList().size())
          || successes.contains(null) || successes.size() == 0) {
        event.getRequestContext()
             .getUIApplication()
             .addMessage(new ApplicationMessage("UIMoveMessageForm.msg.move_delete_not_successful", null, ApplicationMessage.INFO));
      }
      uiMessageList.updateList();
      
      Message msgPreview = uiMsgPreview.getMessage();
      if (msgPreview != null && appliedMsgList.contains(msgPreview)) {
        uiMsgPreview.setMessage(null);
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMoveMessageForm.getAncestorOfType(UIPopupAction.class));
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortlet.findFirstComponentOfType(UIFolderContainer.class));
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMessageList.getAncestorOfType(UIMessageArea.class));
      uiPortlet.cancelAction();
    }
  }
   
  static  public class CancelActionListener extends EventListener<UIMoveMessageForm> {
    public void execute(Event<UIMoveMessageForm> event) throws Exception {
      UIMoveMessageForm uiForm = event.getSource() ;
      uiForm.getAncestorOfType(UIPopupAction.class).cancelPopupAction();
    }
  }

  public void activate() throws Exception { }
  public void deActivate() throws Exception { }
}
