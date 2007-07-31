/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.contact.service.impl;

import java.util.List;

import org.exoplatform.contact.service.Contact;
import org.exoplatform.contact.service.ContactGroup;
import org.exoplatform.contact.service.GroupContactData;



/**
 * Created by The eXo Platform SARL
 * Author : Hung Nguyen
 *          hung.nguyen@exoplatform.com
 * Jul 2, 2007  
 */
public interface DataStorage {
  
  public List<Contact> getPublicContact() throws Exception ;
  public List<Contact> getAllContact(String username) throws Exception ;
  public List<Contact> getContactsByGroup(String username, String groupId) throws Exception ;
  public Contact getContact(String username, String contactId) throws Exception ;
  public void saveContact(String username, Contact contact, boolean isNew) throws Exception ;
  public Contact removeContact(String username, String contactId) throws Exception ;
  
  public List<ContactGroup> getGroups(String username) throws Exception ;
  public ContactGroup getGroup(String username, String groupId) throws Exception ;
  public void saveGroup(String username, ContactGroup group, boolean isNew) throws Exception ;
  public ContactGroup removeGroup(String username, String groupId) throws Exception ;
  
  public Contact shareContact(Contact contact, String[] groupIds) throws Exception ;
  public List<GroupContactData> getPublicContacts(String[] groupIds) throws Exception ;
}
