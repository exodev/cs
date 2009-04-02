/**
 * @author Uoc Nguyen
 */
function UIChatDragDrop() {
  this.DOMUtil      = eXo.core.DOMUtil ;
  this.DragDrop     = eXo.core.DragDrop ;
  this.dragableSets = [];
  this.dropableSets = [];
  this.scKey        = 'border' ;
  this.scValue      = 'solid 1px #000' ;
} ;

UIChatDragDrop.prototype.init = function(rootNode, dragableNodeInfos) {
  this.regDnDItem(rootNode, dragableNodeInfos) ;
} ;

UIChatDragDrop.prototype.regDnDItem = function(rootNode, dragableNodeInfos) {
  for (var i=0; i<dragableNodeInfos.length; i++) {
    var nodeList = eXo.core.DOMUtil.findDescendantsByClass(rootNode, dragableNodeInfos[i].tagName, dragableNodeInfos[i].className);
    if (nodeList) {
      for (var j=0; j<nodeList.length; j++) {
        nodeList[j].style.cursor = 'move';
        nodeList[j].onmousedown = this.mouseDownTrigger;
        /*
        if (window.addEventListener) {
          nodeList[j].addEventListener('mousedown', this.mouseDownTrigger, false);
        } else {
          nodeList[j].attachEvent('onmousedown', this.mouseDownTrigger, false);
        }
        */
      }
    }
  }
} ;

/**
 * 
 * @param {Event} event
 */
UIChatDragDrop.prototype.mouseDownTrigger = function(event) {
  event = event ? event : window.event ;
  var srcElement = event.srcElement || event.target;
  if (srcElement.className.toLowerCase().indexOf('controlicon') != -1) {
    window.jsconsole.info('Invalid element: ' + srcElement.className);
    return;
  }
  if (event.button == 1 || event.which == 1) {
    return eXo.communication.chat.webui.UIChatDragDrop.initDnD(eXo.communication.chat.webui.UIChatDragDrop.dropableSets, srcElement, srcElement, event) ;
  }
  return true ;
} ;

UIChatDragDrop.prototype.initDnD = function(dropableObjs, clickObj, dragObj, event) {
  var clickBlock = ((clickObj && clickObj.tagName) || (typeof(clickObj) != 'string')) ? clickObj : document.getElementById(clickObj) ;
  var dragBlock = ((dragObj && dragObj.tagName) || (typeof(dragObj) != 'string')) ? dragObj : document.getElementById(dragObj) ;
  
  var blockWidth = clickBlock.offsetWidth ;
  var blockHeight = clickBlock.offsetHeight ;
  
  var uiDragObjectNode = this.DOMUtil.findAncestorByClass(dragBlock, 'UIDragObject');
  var oWidth = uiDragObjectNode.offsetWidth;
  var oHeight = uiDragObjectNode.offsetHeight;
  if (this.DOMUtil.findFirstDescendantByClass(uiDragObjectNode, 'div', 'WindowBarLeft')) {
    oHeight = 0;
    var windowCompClass = ['WindowBarLeft', 'MiddleDecoratorLeft', 'BottomDecoratorLeft'];
    for (var i=0; i<windowCompClass.length; i++) {
      var tmpNode = this.DOMUtil.findFirstDescendantByClass(uiDragObjectNode, 'div', windowCompClass[i]);
      if (tmpNode) {
        oHeight += tmpNode.offsetHeight;
      }
    }
  }
  if (uiDragObjectNode.className &&
      uiDragObjectNode.className.indexOf('UIPopupWindow') != -1) {
    //var popupContentClass = ['UINewRoom', 'UIJoinRoom', 'ExoMesseageDecorator'];
    var popupContentClass = ['ExoMesseageDecorator'];
    for (var i=0; i<popupContentClass.length; i++) {
      var tmpNode = this.DOMUtil.findFirstDescendantByClass(uiDragObjectNode, 'div', 'ExoMesseageDecorator');
      if (tmpNode) {
        oHeight = tmpNode.offsetHeight;
      }
    }
  }
  var oTop = uiDragObjectNode.offsetTop;
  var oLeft = uiDragObjectNode.offsetLeft;
  var workspaceControlWidth = 0;

  try {
    workspaceControlWidth = eXo.portal.UIControlWorkspace.width;
  } catch (error) {}

  var UIPageDesktopNode = document.getElementById('UIPageDesktop');
  if (UIPageDesktopNode) {
    oTop += eXo.core.Browser.findPosYInContainer(eXo.communication.chat.webui.UIMainChatWindow.rootNode, document.body);
    oLeft += eXo.core.Browser.findPosXInContainer(eXo.communication.chat.webui.UIMainChatWindow.rootNode, document.body);
    if (eXo.core.Browser.isIE7()) {
      oLeft -= workspaceControlWidth;
    }
  } else {
    if ((!eXo.core.Browser.isIE6() &&
        !eXo.core.Browser.isIE7())) {
      oLeft += workspaceControlWidth;
    }
  }
  var oPosition = 'absolute';
  var dragTmpNode = document.createElement('div');
  with (dragTmpNode.style) {
    position   = oPosition;
    top        = oTop + 'px';
    left       = oLeft + 'px';
    width      = oWidth + 'px';
    height     = oHeight + 'px';
    //display    = 'block';
    background = '#8FBDE5';
    opacity    = '0.7';
    filter     = 'alpha(opacity=70)';
    border     = 'solid 1px #4A67B1';
    zIndex     = '1000';
  }
  dragTmpNode.origTopPos = oTop;
  dragTmpNode.origLeftPos = oLeft;
  dragTmpNode.realNode = uiDragObjectNode;
  document.body.appendChild(dragTmpNode);
  if (uiDragObjectNode) {
    this.DragDrop.initCallback = this.initCallback ;
    this.DragDrop.dragCallback = this.dragCallback ;
    this.DragDrop.dropCallback = this.dropCallback ;
    //this.DragDrop.init(dropableObjs, clickBlock, uiDragObjectNode, event) ;
    this.DragDrop.init(dropableObjs, clickBlock, dragTmpNode, event) ;
  }
  eXo.communication.chat.core.AdvancedDOMEvent.cancelEvent(event);
  return false ;
} ;

UIChatDragDrop.prototype.initCallback = function(dndEvent) {
} ;

UIChatDragDrop.prototype.dragCallback = function(dndEvent) {
  var dragObject = dndEvent.dragObject ;
  if (!dragObject.style.display ||
      dragObject.style.display == 'none') {
    dragObject.style.display = 'block' ;
  }

//  dragObject.style[eXo.communication.chat.webui.UIChatDragDrop.scKey] = eXo.communication.chat.webui.UIChatDragDrop.scValue;

  if (!dndEvent.backupMouseEvent) {
    dndEvent.backupMouseEvent = window.event ;
    if (!dndEvent.backupMouseEvent) {
      return ;
    }
  }

  // Prevent lose control when user drag window out from viewport
  // Top: 
  if (dragObject.offsetTop < 0) {
    dragObject.style.top = '0px' ;
  }
  if (dragObject.offsetTop > (document.body.offsetHeight - 30)) {
    dragObject.style.top = (document.body.offsetHeight - 30) + 'px';
  }
  // Left:
  if (dragObject.offsetLeft < (50 - dragObject.offsetWidth)) {
    dragObject.style.left = (50 - dragObject.offsetWidth) + 'px' ;
  }
  if (dragObject.offsetLeft > (document.body.offsetWidth - 50)) {
    dragObject.style.left = (document.body.offsetWidth - 50) + 'px';
  }
} ;

UIChatDragDrop.prototype.dropCallback = function(dndEvent) {
  var dragObject = dndEvent.dragObject;
  var realNode = dragObject.realNode;
  var oTop = realNode.offsetTop + (dragObject.offsetTop - dragObject.origTopPos);
  var oLeft = realNode.offsetLeft + (dragObject.offsetLeft - dragObject.origLeftPos);
  var workspaceControlWidth = 0;
  try {
    workspaceControlWidth = eXo.portal.UIControlWorkspace.width;
  } finally {
    if (isNaN(workspaceControlWidth)) {
      workspaceControlWidth = 0;
    }
  }
  if (!document.getElementById('UIPageDesktop') &&
      (eXo.core.Browser.isIE6() || eXo.core.Browser.isIE7())) {
    oLeft -= workspaceControlWidth;
  }
  with (realNode.style) {
    top  = oTop + 'px';
    left = oLeft + 'px';
  }
  if (realNode.UIWindow) {
    realNode.UIWindow._setPosition(oTop, oLeft);
  }
  if (realNode.UIWindowManager) {
    eXo.communication.chat.webui.UIPopupManager.focusEventFire(realNode.UIWindowManager);
  }
  eXo.core.DOMUtil.removeElement(dragObject);
} ;

eXo.communication.chat.webui.UIChatDragDrop = new UIChatDragDrop();
