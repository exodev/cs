/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.mail.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Jun 23, 2007  
 */
public class Message extends MessageHeader {
  private String from;
  private String to ;
  private String cc ;
  private String bcc ;
  private String body ;
  private String subject ;
  private String replyTo ;
  private Date sendDate ;
  private Date receivedDate ;
  private String contentType;
  private boolean isUnread = true ;
  private long size ;
  private boolean hasStar = false;
  private ServerConfiguration serverConfiguration;
  
  private String root ;
  private boolean isRootConversation = true ;
  private String[] addresses ;
  private String[] messageIds;
  
  private String[] folders ;
  private String[] tags ;
  
  private Map<String, String> properties = new HashMap<String, String>() ;
  private List<Attachment> attachments ;
  
  public Message() {super() ;}
  public String getMessageTo() { return to ; }
  public void setMessageTo(String s) { to = s ; }
  
  public String getMessageCc() { return cc ; }
  public void setMessageCc(String s) { cc = s ; }
  
  public String getMessageBcc() { return bcc ; }
  public void setMessageBcc(String s) { bcc = s ; }
  
  public String getSubject() { return subject ; }
  public void setSubject(String s) { subject = s ; }
  
  public String getMessageBody() { return body ; }
  public void   setMessageBody(String s) { body =  s ; }
  
  public void setUnread(boolean b) { isUnread = b ; }
  public boolean isUnread() { return isUnread ; }
  
  public Date getSendDate() { return sendDate ; }
  public void setSendDate(Date d) { sendDate = d ; }
  
  public Date getReceivedDate() { return receivedDate ; }
  public void setReceivedDate(Date d) { receivedDate = d ; }
  
  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }
  
  public String[] getFolders() { return folders ; }
  public void setFolders(String[] folders) { this.folders = folders ; }
  
  public String[] getTags() { return tags ; }
  public void setTags(String[] tags) { this.tags = tags ; }
  
  public List<Attachment> getAttachments() { return attachments ; }
  public void setAttachements(List<Attachment> attachments) { this.attachments = attachments ; }
  
  public Message cloneMessage() { return null ; }
  
  public String getFrom() { return from ; }
  public void setFrom(String from) { this.from = from ; } 
  
  public String getReplyTo() { return replyTo; }
  public void setReplyTo(String replyTo) { this.replyTo = replyTo; }
  
  public long getSize() { return size; }
  public void setSize(long size) { this.size = size; }
  
  public boolean hasStar() { return hasStar; }
  public void setHasStar(boolean star) { hasStar = star; }
  
  public ServerConfiguration getServerConfiguration() { return serverConfiguration ; }
  public void setServerConfiguration(ServerConfiguration s) { serverConfiguration  = s; }
  
  public void setProperties(String key, String value) {
    if (properties == null) properties = new HashMap<String, String>();
    properties.put(key, value) ;
  }
  public Map<String, String> getProperties() { return properties ; }
  
  public boolean isRootConversation() { return isRootConversation ; }
  public void setIsRootConversation(boolean b) { isRootConversation = b ; }
  
  public String getRoot() { return root ; }
  public void setRoot(String r) { root = r ; }
  
  public String[] getAddresses() { return addresses; }
  public void setAddresses(String[] arr) { addresses = arr ; }
  
  public String[] getMessageIds() { return messageIds; }
  public void setMessageIds(String[] arr) { messageIds = arr; }
}
