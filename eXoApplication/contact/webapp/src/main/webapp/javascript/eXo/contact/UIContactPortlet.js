eXo.require('eXo.calendar.UICalendarPortlet','/calendar/javascript/');
function ContextMenu(){
	this.menus = new Array,
	this.attachedElement = null ;
	this.menuElement = null ;
	this.preventDefault = true ;
	this.preventForms = true ;
} 
ContextMenu.prototype.init = function(conf) {
	var ContextMenu = eXo.contact.ContextMenu ;
	if ( document.all && document.getElementById && !window.opera ) {
		ContextMenu.IE = true;
	}

	if ( !document.all && document.getElementById && !window.opera ) {
		ContextMenu.FF = true;
	}

	if ( document.all && document.getElementById && window.opera ) {
		ContextMenu.OP = true;
	}

	if ( ContextMenu.IE || ContextMenu.FF ) {

		if (conf && typeof(conf.preventDefault) != "undefined") {
			ContextMenu.preventDefault = conf.preventDefault;
		}

		if (conf && typeof(conf.preventForms) != "undefined") {
			ContextMenu.preventForms = conf.preventForms;
		}
		document.oncontextmenu = ContextMenu.show;

	}
} ;
ContextMenu.prototype.attach = function(classNames, menuId) {
	var ContextMenu = eXo.contact.ContextMenu ;
	if (typeof(classNames) == "string") {
		ContextMenu.menus[classNames] = menuId;
	}

	if (typeof(classNames) == "object") {
		for (x = 0; x < classNames.length; x++) {
			ContextMenu.menus[classNames[x]] = menuId;
		}
	}
} ;

ContextMenu.prototype.getMenuElementId = function(evt) {
	var _e = window.event || evt ;
	var ContextMenu = eXo.contact.ContextMenu ;
	if (ContextMenu.IE) {
		ContextMenu.attachedElement = _e.srcElement;
	} else {
		ContextMenu.attachedElement = _e.target;
	}

	while(ContextMenu.attachedElement != null) {
		var className = ContextMenu.attachedElement.className;

		if (typeof(className) != "undefined") {
			className = className.replace(/^\s+/g, "").replace(/\s+$/g, "")
			var classArray = className.split(/[ ]+/g);

			for (i = 0; i < classArray.length; i++) {
				if (ContextMenu.menus[classArray[i]]) {
					return ContextMenu.menus[classArray[i]];
				}
			}
		}

		if (ContextMenu.IE) {
			ContextMenu.attachedElement = ContextMenu.attachedElement.parentElement;
		} else {
			ContextMenu.attachedElement = ContextMenu.attachedElement.parentNode;
		}
	}

	return null;
} ;

ContextMenu.prototype.getReturnValue = function(evt) {
	var returnValue = true;
	var _e = window.event || evt;

	if (evt.button != 1) {
		if (evt.target) {
			var el = _e.target;
		} else if (_e.srcElement) {
			var el = _e.srcElement;
		}

		var tname = el.tagName.toLowerCase();

		if ((tname == "input" || tname == "textarea")) {
			if (!ContextMenu.preventForms) {
				returnValue = true;
			} else {
				returnValue = false;
			}
		} else {
			if (!ContextMenu.preventDefault) {
				returnValue = true;
			} else {
				returnValue = false;
			}
		}
	}

	return returnValue;
} ;

ContextMenu.prototype.show = function(evt) {
	var _e = window.event || evt
	var ContextMenu = eXo.contact.ContextMenu ;
	var menuElementId = ContextMenu.getMenuElementId(_e) ;

	if (menuElementId) {
		ContextMenu.menuElement = document.getElementById(menuElementId) ;
		var src = null ;
		if (ContextMenu.IE) {
			src = _e.srcElement;
		} else {
			src = _e.target;
		}
		var tr = eXo.core.DOMUtil.findAncestorByTagName(src, "tr") ;
		var checkbox = eXo.core.DOMUtil.findFirstDescendantByClass(tr, "input", "checkbox") ;
		var id = checkbox.name ;
		eXo.calendar.UICalendarPortlet.changeAction(ContextMenu.menuElement, id) ;
		var top = eXo.core.Browser.findMouseYInPage(_e) ;
		var left = eXo.core.Browser.findMouseXInPage(_e) ;
		eXo.core.DOMUtil.listHideElements(ContextMenu.menuElement) ;
		ContextMenu.menuElement.style.left = left + "px" ;
		ContextMenu.menuElement.style.top = top + "px" ;
		ContextMenu.menuElement.style.display = 'block' ;
		return false ;
	}
	return ContextMenu.getReturnValue(_e) ;
} ;
eXo.contact.ContextMenu = new ContextMenu() ;