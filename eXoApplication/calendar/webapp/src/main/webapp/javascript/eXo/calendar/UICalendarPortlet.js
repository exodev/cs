eXo.require('eXo.webui.UIContextMenu') ;

function UICalendarPortlet() {
		
}

/* for general calendar */

UICalendarPortlet.prototype.hide = function() {
	var ln = eXo.core.DOMUtil.hideElementList.length ;
	if (ln > 0) {
		for (var i = 0; i < ln; i++) {
			eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
		}
	}
} ;
UICalendarPortlet.prototype.autoHide = function(evt) {
	var _e = window.event || evt ;
	var _e = window.event || evt ;
	var eventType = _e.type ;	
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	if (eventType == 'mouseout') {
		UICalendarPortlet.timeout = setTimeout("eXo.calendar.UICalendarPortlet.menuElement.style.display='none'", 5000) ;		
	} else {
		if (UICalendarPortlet.timeout) clearTimeout(UICalendarPortlet.timeout) ;		
	}
} ;
UICalendarPortlet.prototype.showHide = function(obj) {
	if (obj.style.display != "block") {
		eXo.calendar.UICalendarPortlet.hide() ;
		obj.style.display = "block" ;
		obj.onmouseover = eXo.calendar.UICalendarPortlet.autoHide ;
		obj.onmouseout = eXo.calendar.UICalendarPortlet.autoHide ;
		eXo.core.DOMUtil.listHideElements(obj) ;
	} else {
		obj.style.display = "none" ;
	}
} ;
UICalendarPortlet.prototype.showMainMenu = function(obj, evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;
	var oldmenu = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "div", "UIRightClickPopupMenu") ;
	eXo.calendar.UICalendarPortlet.swapMenu(oldmenu, obj) ;
}
UICalendarPortlet.prototype.show = function(obj, evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiCalendarPortlet =	document.getElementById("UICalendarPortlet") ;
	var contentContainer = DOMUtil.findFirstDescendantByClass(uiCalendarPortlet, "div", "ContentContainer") ;
	var	uiPopupCategory = DOMUtil.findNextElementByTagName(contentContainer,  "div") ;
	var newPos = {
		"x" : eXo.core.Browser.findPosX(obj) ,
		"y" : eXo.core.Browser.findPosY(obj) + obj.offsetHeight - contentContainer.scrollTop
	}
	if (DOMUtil.findAncestorByClass(obj, "CalendarItem")) uiPopupCategory = DOMUtil.findNextElementByTagName(uiPopupCategory,  "div") ;
	if (!uiPopupCategory) return ;
	var value = "" ;
	var calType = obj.getAttribute("calType") ;
	if (calType) {		
		value = "objectId=" + obj.id + "&calType=" + calType ;
	} else {
		value = "objectId=" + obj.id ;
	}
	var items = DOMUtil.findDescendantsByTagName(uiPopupCategory, "a") ;
	for(var i = 0 ; i < items.length ; i ++) {
		items[i].href = String(items[i].href).replace(/objectId\s*=.*(?='|")/, value) ;
	}	
	
	eXo.calendar.UICalendarPortlet.swapMenu(uiPopupCategory, obj, newPos) ;
	if (calType && (calType != "0") ) {
		var actions = DOMUtil.findDescendantsByTagName(eXo.calendar.UICalendarPortlet.menuElement, "a") ;
		for(var j = 0 ; j < actions.length ; j ++) {
			if (
				(actions[j].href.indexOf("EditCalendar") >= 0) ||
				(actions[j].href.indexOf("RemoveCalendar") >= 0) ||
				(actions[j].href.indexOf("ShareCalendar") >= 0) ||
				(actions[j].href.indexOf("ChangeColorCalendar") >= 0)) {
				actions[j].style.display = "none" ;
			}
		}
	}
} ;

//UICalendarPortlet.prototype.showAction = function(obj, evt) {
//	eXo.webui.UIPopupSelectCategory.show(obj, evt) ;
//	if (this.viewer && document.all) {
//		//this.viewer.style.visibility = "hidden" ;
//		//var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "div", "UIPopupCategory") ;
//		//if (uiPopupCategory.style.display == "none") this.viewer.style.visibility = "visible" ;
//		//var board = eXo.core.DOMUtil.findFirstDescendantByClass(this.viewer, "div", "EventBoard") ;
//		//board.style.position = "static" ;
//	}
////	var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "div", "UIPopupCategory") ;
//} ;

/* for event */

UICalendarPortlet.prototype.init = function() {
	try{
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	var uiDayViewGrid = document.getElementById("UIDayViewGrid") ;
	if (!uiDayViewGrid) return false ;
	UICalendarPortlet.viewer = eXo.core.DOMUtil.findFirstDescendantByClass(uiDayViewGrid, "div", "EventBoardContainer") ;
	UICalendarPortlet.step = 60 ;
	UICalendarPortlet.interval = 20 ;
	//UICalendarPortlet.viewer.onmousedown = eXo.calendar.UISelection.init ;
	}catch(e) {
		window.status = " !!! Error : " + e.message ;
		return false ;
	}
	return true ;
} ;

UICalendarPortlet.prototype.getElements = function(viewer) {
	var elements = eXo.core.DOMUtil.findDescendantsByClass(viewer, "div", "EventContainerBorder") ;
	var len = elements.length ;
	var elems = new Array() ;
	for(var i = 0 ; i < len ; i ++) {
		if (elements[i].style.display != "none") elems.push(elements[i]) ;
	}
	return elems ;
} ;

UICalendarPortlet.prototype.setSize = function(obj) {
	var start = parseInt(obj.getAttribute("startTime")) ;
	var end = parseInt(obj.getAttribute("endTime")) ;	
	height = Math.abs(start - end);
	var top = start ;
	obj.style.height = (height - 2) + "px" ;
	obj.style.top = top + "px" ;
	var eventContainer = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "div", "EventContainer") ;
	eventContainer.style.height = (height - 19) + "px" ;
} ;

UICalendarPortlet.prototype.setWidth = function(element, width) {
	element.style.width = width + "%" ;
} ;

UICalendarPortlet.prototype.getInterval = function(el) {
	var bottom = new Array() ;
	var interval = new Array() ;
	if (el.length <= 0) return ;
	for(var i = 0 ; i < el.length ; i ++ ) {
		bottom.push(el[i].offsetTop + el[i].offsetHeight) ;
		if (bottom[i-1] && (el[i].offsetTop > bottom[i-1])) interval.push(i) ;
	}
	interval.unshift(0) ;
	interval.push(el.length) ;
	return interval ;
} ;

UICalendarPortlet.prototype.adjustWidth = function(el) {
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	var inter = UICalendarPortlet.getInterval(el) ;	
	if (el.length <= 0) return ;
	for(var i = 0 ; i < inter.length ; i ++) {
		var width = "" ;
		var len = (inter[i+1] - inter[i]) ;
		if(isNaN(len)) continue ;
		var n = 0 ;
		for(var j = inter[i]; j < inter[i+1] ; j++) {			
			width = Math.floor(100/len) ;
			UICalendarPortlet.setWidth(el[j], width) ;
			if (el[j-1]&&(len > 1)) el[j].style.left = parseInt(el[j-1].style.width)*n +  "%" ;
			n++ ;
		}
	}
} ;

UICalendarPortlet.prototype.showEvent = function() {
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	if (!UICalendarPortlet.init()) return ;
	var el = UICalendarPortlet.getElements(UICalendarPortlet.viewer) ;
	if (el.length <= 0) return ;
	var marker = null ;
	for(var i = 0 ; i < el.length ; i ++ ) {		
		UICalendarPortlet.setSize(el[i]) ;
		el[i].onmousedown = UICalendarPortlet.initDND ;
		marker = eXo.core.DOMUtil.findFirstChildByClass(el[i], "div", "ResizeEventContainer") ;
		marker.onmousedown = UICalendarPortlet.initResize ;		
	}
	UICalendarPortlet.adjustWidth(el) ;
} ;

/* for resizing event box */
UICalendarPortlet.prototype.initResize = function(evt) {	
	var _e = window.event || evt ;
	_e.cancelBubble = true ;
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	UICalendarPortlet.resizeObject = this ;
	UICalendarPortlet.posY = _e.clientY ;
	var eventDayContainer = eXo.core.DOMUtil.findAncestorByClass(UICalendarPortlet.resizeObject, 'EventDayContainer') ;
	UICalendarPortlet.eventBox = eXo.core.DOMUtil.findAncestorByClass(UICalendarPortlet.resizeObject,'EventContainerBorder') ;
	UICalendarPortlet.eventContainer = eXo.core.DOMUtil.findPreviousElementByTagName(UICalendarPortlet.resizeObject, "div") ;
	UICalendarPortlet.posY = _e.clientY ;
	UICalendarPortlet.beforeHeight = UICalendarPortlet.eventBox.offsetHeight ;
	UICalendarPortlet.eventContainerHeight = UICalendarPortlet.eventContainer.offsetHeight + 2 ;
	eventDayContainer.onmousemove = UICalendarPortlet.adjustHeight ;
	eventDayContainer.onmouseup = UICalendarPortlet.resizeCallBack ;
} ;

UICalendarPortlet.prototype.adjustHeight = function(evt) {
	var _e = window.event || evt ;
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	var delta = _e.clientY - UICalendarPortlet.posY ;
	var height = UICalendarPortlet.beforeHeight + delta ;
	var containerHeight = UICalendarPortlet.eventContainerHeight + delta ;
	if (height <= (eXo.calendar.UICalendarPortlet.step/2)) return ;
	if (delta%UICalendarPortlet.interval == 0){
		UICalendarPortlet.eventBox.style.height = height + "px" ;
		UICalendarPortlet.eventContainer.style.height = containerHeight + "px" ;
	}	
} ;

UICalendarPortlet.prototype.resizeCallBack = function() {
	var eventBox = eXo.calendar.UICalendarPortlet.eventBox ;
	var start =  parseInt(eventBox.getAttribute("startTime")) ;
	var end =  start + eventBox.offsetHeight - 2 ;
	if (eventBox.offsetHeight != eXo.calendar.UICalendarPortlet.beforeHeight) eXo.calendar.UICalendarPortlet.adjustTime(start, end, eventBox) ;	
} ;

/* for drag and drop */

UICalendarPortlet.prototype.initDND = function(evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	UICalendarPortlet.dragObject = this ;
	var eventDayContainer = eXo.core.DOMUtil.findAncestorByClass(UICalendarPortlet.dragObject, "EventDayContainer") ;
	UICalendarPortlet.eventY = _e.clientY ;
	UICalendarPortlet.eventTop = UICalendarPortlet.dragObject.offsetTop ;
	eventDayContainer.onmousemove = UICalendarPortlet.dragStart ;
	eventDayContainer.onmouseup = UICalendarPortlet.dragEnd ;
}
UICalendarPortlet.prototype.dragStart = function(evt) {
	var _e = window.event || evt ;
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	var delta = _e.clientY - UICalendarPortlet.eventY ;
	if (delta%UICalendarPortlet.interval == 0) {
		var top = UICalendarPortlet.eventTop + delta ;
		UICalendarPortlet.dragObject.style.top = top + "px" ;
	}
} ;
UICalendarPortlet.prototype.dragEnd = function() {
	this.onmousemove = null ;
	var dragObject = eXo.calendar.UICalendarPortlet.dragObject ;
	var start = parseInt(dragObject.getAttribute("startTime")) ;
	var end = parseInt(dragObject.getAttribute("endTime")) ;
	var delta = end - start  ;
	var currentStart = dragObject.offsetTop ;
	var currentEnd = currentStart + delta ;
	if(dragObject.offsetTop != eXo.calendar.UICalendarPortlet.eventTop) eXo.calendar.UICalendarPortlet.adjustTime(currentStart, currentEnd, dragObject) ;
	dragObject = null ;
} ;

/* for adjusting time */

UICalendarPortlet.prototype.adjustTime = function(currentStart, currentEnd, obj) {
	var actionLink = obj.getAttribute("actionLink") ;	
	var pattern = /startTime.*endTime/g ;
	var params = "startTime=" + currentStart + "&finishTime=" + currentEnd ;
	actionLink = actionLink.replace(pattern, params).replace("javascript:","") ;
	eval(actionLink) ;
} ;

/* for showing context menu */

UICalendarPortlet.prototype.showContextMenu = function() {	
	var UIContextMenu = eXo.webui.UIContextMenu ;
	var config = {
		'preventDefault':false, 
		'preventForms':false
	} ;	
	UIContextMenu.init(config) ;
	UIContextMenu.attach("CalendarContentNomal","UIMonthViewRightMenu") ;
	UIContextMenu.attach("EventOnDayContent","UIMonthViewEventRightMenu") ;
	UIContextMenu.attach("TimeRule","UIDayViewRightMenu") ;
	UIContextMenu.attach("EventBoxes","UIDayViewEventRightMenu") ;
	UIContextMenu.attach(["EventWeekContent","EventAlldayContainer"],"UIWeekViewRightMenu") ;
	//UIContextMenu.attach(["EventContainerBoder","EventContainerBar","EventContainer","ResizeEventContainer"],"UIWeekViewEventRightMenu") ;
} ;

UICalendarPortlet.prototype.dayViewCallback = function(evt){
	var _e = window.event || evt ;
	var src = _e.srcElement || _e.target ;
	var startTime = "" ;
	var map = null ;
	if (src.nodeName == "TD") {		
		src = eXo.core.DOMUtil.findAncestorByTagName(src, "tr") ;
		startTime = src.getAttribute("startTime") ;
		map = {
			"startTime\s*=\s*.*(?=&|'|\")":"startTime="+startTime
		} ;
	}
	else{		
		src = eXo.core.DOMUtil.findAncestorByClass(src, "EventBoxes") ;
		var eventId = src.getAttribute("eventid") ;
		var calendarId = src.getAttribute("calid") ;
		map = {
			"objectId\s*=\s*[A-Za-z0-9_]*(?=&|'|\")":"objectId="+eventId,
			"calendarId\s*=\s*[A-Za-z0-9_]*(?=&|'|\")":"calendarId="+calendarId
		} ;
	}
	eXo.webui.UIContextMenu.changeAction(eXo.webui.UIContextMenu.menuElement, map) ;
}
UICalendarPortlet.prototype.weekViewCallback = function(evt) {
	var _e = window.event || evt ;
	var src = _e.srcElement || _e.target ;
	var DOMUtil = eXo.core.DOMUtil ;
	var UIContextMenu = eXo.webui.UIContextMenu ;
	var items = DOMUtil.findDescendantsByTagName(UIContextMenu.menuElement,"a") ;
	if (DOMUtil.hasClass(src,"EventContainerBoder") || DOMUtil.hasClass(src,"EventContainerBar") || DOMUtil.hasClass(src,"EventContainer") || DOMUtil.hasClass(src,"ResizeEventContainer")) {
		var obj = (DOMUtil.findAncestorByClass(src, "EventContainerBoder"))? DOMUtil.findAncestorByClass(src, "EventContainerBoder") : src ;
		var eventId = obj.getAttribute("eventId") ;
		var calendarId = obj.getAttribute("calId") ;
		map = {
			"objectId\s*=\s*[A-Za-z0-9_]*(?=&|'|\")":"objectId="+eventId,
			"calendarId\s*=\s*[A-Za-z0-9_]*(?=&|'|\")":"calendarId="+calendarId
		} ;
		obj = DOMUtil.findAncestorByTagName(src, "td").getAttribute("startTime") ;		
		for(var i = 0 ; i < items.length ; i ++ ) {
			if (items[i].style.display == "none") {
				items[i].style.display = "block" ;
				items[i].href = eXo.webui.UIContextMenu.replaceall(String(items[0].href),map) ;
			} else {				
				items[i].href = String(items[i].href).replace(/startTime\s*=\s*.*(?=&|'|\")/,"startTime="+obj) ;
			}
		}		
	} else {
		var obj = (DOMUtil.findAncestorByTagName(src, "td"))? DOMUtil.findAncestorByTagName(src, "td") : src ;
		var map = obj.getAttribute("startTime") ;
		for(var i = 0 ; i < items.length ; i ++ ) {
			if (items[i].style.display == "block") {
				items[i].style.display = "none" ;
			} else {				
				items[i].href = String(items[i].href).replace(/startTime\s*=\s*.*(?=&|'|\")/,"startTime="+map) ;
			}
			
		}
	}
}
UICalendarPortlet.prototype.monthViewCallback = function(evt){
	var _e = window.event || evt ;
	var src = _e.srcElement || _e.target ;
	var UIContextMenu = eXo.webui.UIContextMenu ;
	var DOMUtil = eXo.core.DOMUtil ;
	var objectValue = "" ;
	var links = eXo.core.DOMUtil.findDescendantsByTagName(UIContextMenu.menuElement, "a") ;
	if (!DOMUtil.findAncestorByClass(src, "EventBoxes")) {
		if (objectValue = DOMUtil.findAncestorByTagName(src,"td").getAttribute("currentDate")){
			UIContextMenu.changeAction(UIContextMenu.menuElement,objectValue) ;
		}
	} else if (objvalue = DOMUtil.findAncestorByClass(src, "EventBoxes")) {
		var eventId = objvalue.getAttribute("eventId") ;
		var calendarId = objvalue.getAttribute("calId") ;
		var map = {
			"objectId\s*=\s*[A-Za-z0-9_]*(?=&|'|\")":"objectId="+eventId,
			"calendarId\s*=\s*[A-Za-z0-9_]*(?=&|'|\")":"calendarId="+calendarId
		} ;
		UIContextMenu.changeAction(UIContextMenu.menuElement, map) ;
	} else {
		return ;
	}	
}

UICalendarPortlet.prototype.initFilter = function(obj, type){
	if (type == 1) {
		var checkbox = eXo.core.DOMUtil.findFirstChildByClass(obj, "input", "checkbox") ;
		eXo.calendar.UICalendarPortlet.filterByCalendar(checkbox.name,checkbox.checked) ;
	} else if (type == 2) {
		
	} else {
		return ;
	}
} ;

UICalendarPortlet.prototype.filterByGroup = function(obj) { 
	var DOMUtil = eXo.core.DOMUtil ;
	var uiVtab = DOMUtil.findAncestorByClass(obj, "CalendarSelectedFlatStyle") ;
	var uiVTabContent = DOMUtil.findNextElementByTagName(uiVtab, "div") ;
	var checkboxes = uiVTabContent.getElementsByTagName("input") ;
	var checked = obj.checked ;
	var len = checkboxes.length ;
	for(var i = 0 ; i < len ; i ++) {
		checkboxes[i].checked = checked ;
		eXo.calendar.UICalendarPortlet.filterByCalendar(checkboxes[i].name, checked) ;
	}
} ;

UICalendarPortlet.prototype.checkSpaceAvailable = function() {
	var uiMonthViewGrid = document.getElementById("UIMonthViewGrid") ;
	if (!uiMonthViewGrid) return ;
	var DOMUtil = eXo.core.DOMUtil ;
	var tds = DOMUtil.findDescendantsByTagName(uiMonthViewGrid, "td") ;
	var eventBoxes = null ;
	for(var i = 0 ; i < tds.length ; i ++) {
		eventBoxes = DOMUtil.findDescendantsByClass(tds[i], "div", "EventBoxes") ;
		if (!eventBoxes || (eventBoxes.length <= 3)) continue ;
		var n = 0 ;
		var height = 0 ;
		var len = eventBoxes.length ;
		for(var j = 0 ; j < len ; j ++) {
			if (eventBoxes[j].style.display != "none") {
				n++ ;
				height += eventBoxes[j].offsetHeight ;
				if (height >= 50) break ;
			}	
		}
		var newMore = len - n ;
		var more = tds[i].getElementsByTagName("a")[0] ;
		if ((n < 3) && (n > 0)) {
			more.innerHTML = String(more.innerHTML).replace(/\+\s+\d*/,"+ " + newMore) ;
			more.style.display = "block" ;
		} else if (n == 0){
			more.style.display = "none" ;
		} else {
			more.innerHTML = String(more.innerHTML).replace(/\+\s+\d*/,"+ " + newMore) ;
			more.style.display = "block" ;
		}
		eventBoxes = null ;
	}
} ;

UICalendarPortlet.prototype.filterByCalendar = function(calendarId, status) {
	var uiCalendarViewContainer = document.getElementById("UICalendarViewContainer") ;
	if (!uiCalendarViewContainer) return ;
	var className = "EventBoxes" ;
	var events = eXo.core.DOMUtil.findDescendantsByClass(uiCalendarViewContainer, "div", className) ;
	if (!events) return ;
	var len = events.length ;
	for(var i = 0 ; i < len ; i ++){
		if (events[i].getAttribute("calid") == calendarId) {
			if (status) 					
				events[i].style.display = "block" ;			
			else 
				events[i].style.display = "none" ;				
		}
	}
	if (document.getElementById("UIMonthViewGrid")) eXo.calendar.UICalendarPortlet.checkSpaceAvailable() ;
	if (document.getElementById("UIDayViewGrid")) eXo.calendar.UICalendarPortlet.showEvent() ;

} ;
UICalendarPortlet.prototype.initFilterByCategory = function(obj) {
	var selectbox = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "select", "selectbox") ;
	selectbox.onchange = eXo.calendar.UICalendarPortlet.filterByCategory ;
} ;
UICalendarPortlet.prototype.filterByCategory = function() {
	var uiCalendarViewContainer = document.getElementById("UICalendarViewContainer") ;
	if (!uiCalendarViewContainer) return ;
	var category = this.options[this.selectedIndex].value ;
	var className = "EventBoxes" ;
	var events = eXo.core.DOMUtil.findDescendantsByClass(uiCalendarViewContainer, "div", className) ;
	if (!events) return ;
	var len = events.length ;
	for(var i = 0 ; i < len ; i ++){
		if (category == events[i].getAttribute("eventCat")) {
			events[i].style.display = "block" ;
		}
		else if (category == "") {
			events[i].style.display = "block" ;
		}
		else events[i].style.display = "none" ;
	}
	eXo.calendar.UICalendarPortlet.checkSpaceAvailable() ;	
} ;
UICalendarPortlet.prototype.showView = function(obj, evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;	
	var oldmenu = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "div", "UIRightClickPopupMenu") ;	
	eXo.calendar.UICalendarPortlet.swapMenu(oldmenu, obj) ;
}
UICalendarPortlet.prototype.swapMenu = function(oldmenu, clickobj) {
	var UICalendarPortlet = eXo.calendar.UICalendarPortlet ;
	var Browser = eXo.core.Browser ;
	var menuX = Browser.findPosX(clickobj) ;
	var menuY = Browser.findPosY(clickobj) + clickobj.offsetHeight ;
	if (arguments.length > 2) { // Customize position of menu with an object that have 2 properties x, y 
		menuX = arguments[2].x ;
		menuY = arguments[2].y ;
	}	
	if(document.getElementById("tmpMenuElement")) document.getElementById("UIPortalApplication").removeChild(document.getElementById("tmpMenuElement")) ;
	var tmpMenuElement = oldmenu.cloneNode(true) ;
	tmpMenuElement.setAttribute("id","tmpMenuElement") ;
	UICalendarPortlet.menuElement = tmpMenuElement ;
	document.getElementById("UIPortalApplication").appendChild(tmpMenuElement) ;
	
	
	UICalendarPortlet.menuElement.style.top = menuY + "px" ;
	UICalendarPortlet.menuElement.style.left = menuX + "px" ;	
	UICalendarPortlet.showHide(UICalendarPortlet.menuElement) ;
	//UICalendarPortlet.menuElement = null ;
}

/* for selection creation */

function UISelection() {
	
} ;

UISelection.prototype.init = function(evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;
	var UISelection = eXo.calendar.UISelection ;
	var Container = this ;
	UISelection.relativeObject = eXo.core.DOMUtil.findAncestorByClass(Container, "EventDayContainer") ;
	var selection = document.getElementById("selection") ;
	if (selection) selection.parentNode.removeChild(selection) ;
	UISelection.selection = document.createElement("div") ;
	UISelection.selection.className = "selection" ;
	UISelection.selection.setAttribute("id", "selection") ;
	UISelection.selectionY = eXo.core.Browser.findMouseRelativeY(UISelection.relativeObject, _e) + UISelection.relativeObject.scrollTop;
	UISelection.selection.innerHTML = "<span></span>" ;
	Container.appendChild(UISelection.selection) ;
	Container.onmousemove = UISelection.resize ;
	Container.onmouseup = UISelection.clear ;
} ;

UISelection.prototype.resize = function(evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;	
	var UISelection = eXo.calendar.UISelection ;
	var delta = UISelection.selectionY - eXo.core.Browser.findMouseRelativeY(UISelection.relativeObject, _e) ;
	if (delta < 0) {
		UISelection.selection.style.top = UISelection.selectionY - UISelection.relativeObject.scrollTop + "px" ;
		UISelection.selection.style.height = Math.abs(delta) + "px" ;
	} else {
		UISelection.selection.style.bottom = UISelection.selectionY + "px" ;
		UISelection.selection.style.top = eXo.core.Browser.findMouseRelativeY(UISelection.relativeObject, _e) - UISelection.relativeObject.scrollTop + "px" ;
		UISelection.selection.style.height = Math.abs(delta) + "px" ;
	}
	window.status = UISelection.selection.offsetTop ;
} ;
UISelection.prototype.clear = function() {	
	this.onmousemove = null ;
} ;

eXo.calendar.UICalendarPortlet = new UICalendarPortlet() ;
eXo.calendar.UISelection = new UISelection() ;