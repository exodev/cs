/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.mail.webui;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.UIFormInputSet;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minh.dang@exoplatform.com
 * Improved by : Phung Hai Nam
 *          phunghainam@gmail.com          
 * Sep 20, 2006
 */

public class UIFormInputWithActions extends UIFormInputSet {
  private Log log = ExoLogger.getLogger(this.getClass());
  Map<String, List<ActionData>> actionField = new HashMap<String, List<ActionData>> () ;
  public UIFormInputWithActions(String id) {
    super.setId(id) ;
  }
  
  public void setActionField(String fieldName, List<ActionData> actions) throws Exception {
    actionField.put(fieldName, actions) ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    if(getComponentConfig() != null) {
      super.processRender(context) ;
      return ;
    }
    UIForm uiForm = getAncestorOfType(UIForm.class);
    Writer w = context.getWriter() ;
    w.write("<div id=\"" + getId() + "\" class=\"UIFormInputSet " + getId() + "\">") ;
    w.write("<table class=\"UIFormGrid\">") ;
    ResourceBundle res = context.getApplicationResourceBundle() ;
    
    for(UIComponent inputEntry :  getChildren()) {     
      String label ;
      try {
        label = uiForm.getLabel(res, inputEntry.getId());
        if(inputEntry instanceof UIFormInputBase) ((UIFormInputBase)inputEntry).setLabel(label);
      } catch(MissingResourceException ex){
        label = inputEntry.getId() ;
        System.err.println("\n "+uiForm.getId()+".label." + inputEntry.getId()+" not found value");
      }
      w.write("<tr>") ;
      if (!(inputEntry instanceof UIFormCheckBoxInput)) {
        w.write("<td class=\"FieldLabel\" style=\"text-align: left\">") ; w.write(label); w.write("</td>") ;   
        w.write("<td class=\"FieldComponent\">") ; renderUIComponent(inputEntry) ; 
      } else {
        w.write("<td class=\"FieldComponent\" style=\"text-align:left\" colspan=\"2\">") ; renderUIComponent(inputEntry) ;
      }
      List<ActionData> actions = actionField.get(inputEntry.getName()) ;
      if(actions != null) {
        for(ActionData action : actions) {
          String actionLabel ;
          try{
            actionLabel = uiForm.getLabel(res, "action." + action.getActionName())  ;
          }catch(MissingResourceException ex) {
            actionLabel = action.getActionName() ;
            log.warn("\n Key: '"+uiForm.getId()+".label.action." + action.getActionName() + "' not found");
          }
          String actionLink ;
          if(action.getActionParameter() != null) {
            actionLink = ((UIComponent)getParent()).event(action.getActionListener(), action.getActionParameter()) ;
          }else {
            actionLink = ((UIComponent)getParent()).event(action.getActionListener()) ;
          }
          w.write("<a title=\"" + actionLabel + "\" href=\"" + actionLink +"\">") ;
          if(action.getActionType() == ActionData.TYPE_ICON) {
            w.write("<img src=\"/eXoResources/skin/DefaultSkin/background/Blank.gif\" class=\"" + action.getCssIconClass()+"\"/>") ;
            if(action.isShowLabel) w.write(actionLabel) ;
          }else if(action.getActionType() == ActionData.TYPE_LINK){
           w.write(actionLabel) ;
          }
          w.write("</a>") ; w.write("&nbsp;") ; 
          if(action.isBreakLine()) w.write("<br/>") ; 
        }
      }
      if (inputEntry instanceof UIFormCheckBoxInput) {
        w.write("<span class=\"FieldLabel\"> &nbsp;&nbsp;&nbsp;");  w.write(label); w.write("</span>");        
      }
      w.write("</td>") ;
      w.write("</tr>") ;
    }
    w.write("</table>") ;
    w.write("</div>") ;    
  }
  
  static public class ActionData {
    final public static int TYPE_ICON = 0 ;
    final public static int TYPE_LINK = 1 ;
    
    private int actionType = 0 ;
    private String actionName ;
    private String actionListener ;
    private String actionParameter = null ;
    private String cssIconClass = "AddNewNodeIcon" ;
    private boolean isShowLabel = false ;
    private boolean isBreakLine = false ;
    
    
    public void setActionType(int actionType) { this.actionType = actionType ; }
    public int getActionType() { return actionType; }
    
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getActionName() { return actionName; }
    
    public void setActionListener(String actionListener) { this.actionListener = actionListener; }
    public String getActionListener() { return actionListener; }
    
    public void setActionParameter(String actionParameter) { this.actionParameter = actionParameter ; }
    public String getActionParameter() { return actionParameter ; }
    
    public void setCssIconClass(String cssIconClass) { this.cssIconClass = cssIconClass; }
    public String getCssIconClass() { return cssIconClass; }
    
    public void setShowLabel(boolean isShowLabel) { this.isShowLabel = isShowLabel ; }
    public boolean isShowLabel() { return isShowLabel ; }
    
    public void setBreakLine(boolean isBreakLine) { this.isBreakLine = isBreakLine ; }
    public boolean isBreakLine() { return isBreakLine ; }
  }
}