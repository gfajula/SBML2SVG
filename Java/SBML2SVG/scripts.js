 // version 20101119a
 var historySize = 10; // tama�o del 'history' de navegacion de elementos
 var historyForward = new Array();
 var historyNow = "";
 var historyBack = new Array();
 var historyLast = 0;
 var historyFirst = 0;
 var historyNow = "";
 
 var innerWidth=800;   // Anchura por defecto
 var innerHeight=600;  // Altura por defecto
 var hlColor = "#ff22ff";//"#66ccff";
 var svgNS = "http://www.w3.org/2000/svg";
 var dbg = document.getElementById('debugText');
 var infoW = document.getElementById("_infoWindow_");
 var screenCTM = getScreenCTM();
 var screenCTMInv = getScreenCTM().inverse();
 

if (window.addEventListener) {
	window.addEventListener("load", init, false);
} 
//else window.alert("no addEventListener");

function init()
{	setDebugMessage("XXX");
	document.getElementById("_infoWindow_").addEventListener("mousedown", startDrag, false);
	//document.addEventListener("mousedown", removeInstructions, true);
};
 
 
 function setDebugMessage(sMsg) {
    
    dbg.replaceChild( document.createTextNode(sMsg), 
    				  dbg.firstChild
    				);
 }
 
 function getScreenCTM(){
    // now we find the screen CTM of the document SVG element
    var root = document.documentElement;
    var highlighted;
    
    if (root.getScreenCTM) {
       //setDebugMessage("Hard Matrix");       	   
 	   return root.getScreenCTM() ;
 	} else {
 	   //setDebugMessage("Soft Matrix");
 	} 	
    
    var sCTM = root.createSVGMatrix();

    var tr = root.createSVGMatrix();
    var par = root.getAttributeNS(null, "preserveAspectRatio");
    if (!par) { par="xMidYMid meet"; } //setting to default value
    parX = par.substring(0,4); //xMin;xMid;xMax
    parY = par.substring(4,8); //YMin;YMid;YMax;
    ma = par.split(' ');
    mos = ma[1]; //meet;slice

    //get dimensions of the viewport
    sCTM.a = 1;
    sCTM.d = 1;
    sCTM.e = 0;
    sCTM.f = 0;

    w = root.getAttribute('width') || '100%'; // w = innerWidth;
    h = root.getAttribute('height')|| '100%'; // h = innerHeight;
    // alert('w' + w , 'h' + h);
    if( w.indexOf('%')+1 ) w = (parseFloat(w) / 100.0) * innerWidth;
    if( h.indexOf('%')+1 ) h = (parseFloat(h) / 100.0) * innerHeight;
  

    // get the ViewBox
    var vb = (root.getAttribute('viewBox') || '0 0 '+w+' '+h).split(' ');

    //--------------------------------------------------------------------------
    //create a matrix with current user transformation
    tr.a = root.currentScale;
    tr.d = root.currentScale;
    tr.e = root.currentTranslate.x;
    tr.f = root.currentTranslate.y;

    // scale factors
    sx = w/vb[2];
    sy = h/vb[3];

    // meetOrSlice
    if(mos=="slice") { s = (sx>sy ? sx : sy); }
    else { s = (sx<sy ? sx : sy); }

    //preserveAspectRatio="none"
    if (par=="none") {
        sCTM.a = sx;  //scaleX
        sCTM.d = sy;  //scaleY
        sCTM.e = -vb[0]*sx; //translateX
        sCTM.f = -vb[0]*sy; //translateY
        sCTM = tr.multiply(sCTM);//taking user transformations into account
    }
    else {
        sCTM.a = s; //scaleX
        sCTM.d = s; //scaleY
        //-------------------------------------------------------
        switch(parX){
        case 'xMid':
            sCTM.e = ((w-vb[2]*s)/2) - vb[0]*s; //translateX
            break;
        case 'xMin':
            sCTM.e = - vb[0]*s; //translateX
            break;
        case 'xMax':
            sCTM.e = (w-vb[2]*s)- vb[0]*s; //translateX
            break;
        }
        //------------------------------------------------------------
        switch(parY){
        case 'YMid':
            sCTM.f = (h-vb[3]*s)/2 - vb[1]*s; //translateY
            break;
        case 'YMin':
            sCTM.f = - vb[1]*s; //translateY
            break;
        case 'YMax':
            sCTM.f = (h-vb[3]*s) - vb[1]*s; //translateY
            break;
        }

        sCTM = tr.multiply(sCTM); //taking user transformations into acount
    } // else	
	
    return sCTM;
}

var elmt = document.getElementById('_infoWindow_');
var x=0;var y=0; var dragging=false;
var startx=0;var starty=0;
var tx=0;var ty=0;

function startDrag(event){	
   //setDebugMessage('startDrag');
			   
   var xformstr = elmt.getAttributeNS(null, 'transform'); 
   if(xformstr && xformstr.length > 0) { 
      xformstr = xformstr.split(/[\(\)]/)[1];  
      tx = parseFloat(xformstr); 
      if(isNaN(tx)) { tx = 0; }
      ty = parseFloat(xformstr.substr(xformstr.indexOf(tx)+(""+tx).length+1));
      if(isNaN(ty)) { ty = 0; }
   }
         
   var p = document.documentElement.createSVGPoint();
   // p now contains the mouse position in browser client area in pixels
   p.x = event.clientX;
   p.y = event.clientY;

   screenCTMInv = getScreenCTM().inverse();
   // p now contains the mouse position in SVG user coords
   p = p.matrixTransform(screenCTMInv);
   dragging=true;
   startx = p.x;
   starty = p.y;
}

function doDrag(event){	
	if (!dragging) return;

    //setDebugMessage('doDrag');
    
    var p = document.documentElement.createSVGPoint();
    p.x = event.clientX;
    p.y = event.clientY;
    
    // screenCTMInv ahora se calcula una sola vez en startDrag
    // var screenCTMInv = getScreenCTM().inverse();
    p = p.matrixTransform(screenCTMInv);
	
	x=event.clientX;
	y=event.clientY;
	
	elmt.setAttribute ('transform', 'translate('+(tx+(p.x-startx))+','+(ty+(p.y-starty))+')' );
	//setDebugMessage( elmt.getAttribute('transform') );
}

function endDrag(){
    //setDebugMessage('endDrag' + x + ', ' + y);    
	dragging=false;
	// var sCTM = getScreenCTM();
	// alert('a'+ sCTM.a + ', ' + 'd' + sCTM.d );
}


function colorize(element) {
   if ( (element==null) || (element.nodeType!=1) ) return;
   //alert("Nr of Children: "+element.childNodes.length);
   //if (element.hasAttributeNS(null,"stroke")) {
      element.setAttributeNS(null,"stroke", hlColor);   
      element.setAttributeNS(null,"stroke-width", "7");     
      element.setAttributeNS(null,"stroke-opacity", "1");   
   //}
   
   setDebugMessage('has no none attribute' + element.getAttributeNS(null,"fill"));    
   if (element.hasAttributeNS(null,"fill")) {
	  if (element.getAttributeNS(null,"fill") != "none") {
		 setDebugMessage('has fill !none' + element.getAttributeNS(null,"fill"));    
         element.setAttributeNS(null,"fill", hlColor);
   	  } else {
   		setDebugMessage('has fill == none' + element.getAttributeNS(null,"fill"));
   	  }
      //element.setAttributeNS(null,"fill-opacity", "0.75");          
   }
   
   for(var i=0; i<element.childNodes.length; i++) {
   	  //alert("loop: " + i + element.childNodes.item(i).nodeName );
      colorize( element.childNodes.item(i) );      
   }
}

// En algunas implementaciones, se produce error al quitar
// atributos si no existen
function removeAttribute(elem, sAttName) {
   if (elem.hasAttributeNS(null,sAttName)) {
      elem.removeAttributeNS(null,sAttName);
   }
}

function equalize(element) {
   if ( (element==null) || (element.nodeType!=1) ) return;
   //alert("Nr of Children: "+element.childNodes.length);
   
   removeAttribute(element,"stroke");   
   removeAttribute(element,"stroke-width");     
   removeAttribute(element,"stroke-opacity");
   removeAttribute(element,"fill");
   removeAttribute(element,"fill-opacity");          
   
   for(var i=0; i<element.childNodes.length; i++) {
   	  //alert("loop: " + i + element.childNodes.item(i).nodeName );
      equalize( element.childNodes.item(i) );      
   }
}

var highlighted;
var clon;


function infoWindow(id, name, type){
	var elemt = document.getElementById(id);
	if (historyNow!="") historyBack.push( historyNow );
	historyNow = elemt.getAttribute("onclick").replace("infoWindow", "infoWindowInner");	
	// setDebugMessage( historyNow );
	infoWindowInner(id, name, type);
}

function infoWindowInner(id, name, type){
	//setDebugMessage("infoWindow");	
	deselect();
	highlighted = document.getElementById(id);
	if(highlighted==null) {
	   alert(id);
	} else {
		clon = highlighted.cloneNode(true);
		clon.setAttributeNS(null,"id",id+"highlight");
		clon.setAttributeNS(null,"stroke", hlColor);   
        clon.setAttributeNS(null,"stroke-width", "7");
        clon.setAttributeNS(null,"fill", "none");
		// Incrementar tamaño del bounding box para que no se corte el borde
		//var bbHeight = clon.
		for(var i=0; i<clon.childNodes.length; i++) {
		   equalize(clon.childNodes.item(i));
		}
		/* var anim = document.createElementNS(svgNS,"animateColor");
		anim.setAttributeNS(null,"attributeName","stroke");
		anim.setAttributeNS(null,"attributeType","XML");
		anim.setAttributeNS(null,"from","#ff00ff");
		anim.setAttributeNS(null,"to","#ffffff");
		anim.setAttributeNS(null,"begin","1s");		
		anim.setAttributeNS(null,"dur","1s");
		anim.setAttributeNS(null,"fill","freeze");
		anim.setAttributeNS(null,"repeatCount","indefinite");
		//anim.setAttributeNS(null,"restart","always");
		clon.appendChild(anim); */
		
		//clon.setAttributeNS(null,"filter", "url(#Gaussian_Blur)");
		highlighted.parentNode.insertBefore( clon, highlighted );
		//highlighted.appendChild(clon);
	}
	
	document.getElementById('_infoWindow_').setAttribute ("visibility", "visible");
	var elmt = document.getElementById('_infoWindowText_');
	var nodelis = elmt.childNodes;
	
  if (nodelis==null) window.alert("fallo el childNodes");		
	
	var elmtId = document.createTextNode(id);
	
  if (elmtId==null) window.alert("fallo el createTextNode");			
	elmt.childNodes.item(2).replaceChild(elmtId, elmt.childNodes.item(2).firstChild);
  if (elmt.childNodes.item(2)==null) { window.alert("falla el chilnodes.item()"); }
  else { ; }					
	var elmtName = document.createTextNode(name);
	elmt.childNodes.item(0).replaceChild(elmtName, elmt.childNodes.item(0).firstChild);
	
	var elmtType = document.createTextNode(type);
	elmt.childNodes.item(1).replaceChild(elmtType, elmt.childNodes.item(1).firstChild);				
}

function deselect() {
	if (clon==null) return;
    var parent = clon.parentNode;
    if (parent!=null) {
    	parent.removeChild(clon)
    }
    
    var iwin = document.getElementById("_infoWindowText_");
    while(iwin.childNodes.item(6)){
       iwin.removeChild( iwin.childNodes.item(6) );
    }
}


function infoWindowReac(id, name, type, reactants, products){
	var elemt = document.getElementById(id);
	
	if (historyNow!="") historyBack.push( historyNow );
	historyNow = elemt.getAttribute("onclick").replace("infoWindowReac", "infoWindowReacInner");	
	// setDebugMessage( historyNow );
	
   infoWindowReacInner(id, name, type, reactants, products);
}

function infoWindowReacInner(id, name, type, reactants, products){
   infoWindowInner(id, name, type);
   
   var iwin = document.getElementById("_infoWindowText_");
   
   var newText; var textContent;

   var startY = 105;
   var x1 = 110;      
   
   
   newText = document.createElementNS(svgNS,"text");
   newText.setAttributeNS(null,"y", ""+startY );
   newText.setAttributeNS(null,"x", "100");
   newText.setAttributeNS(null,"style", "text-anchor: end;");
   textContent = document.createTextNode("Reactants:");
   newText.appendChild(textContent);
   iwin.appendChild(newText);
   
   
   var specie;
   for(i=0; reactants[i]; i++) {
      specie = document.getElementById( reactants[i] );
   
      newText = document.createElementNS(svgNS,"text");
      newText.setAttributeNS(null,"id", "react_link_" + i);
      newText.setAttributeNS(null,"y", ""+startY );
      newText.setAttributeNS(null,"x", ""+x1 );
      newText.setAttributeNS(null,"fill", "blue");
      if (specie && specie.getAttribute("onclick") ) {
         newText.setAttributeNS(null, "onclick" , specie.getAttribute("onclick"));
         newText.setAttributeNS(null, "onmouseover", "doHover('react_link_" + i + "');");
         newText.setAttributeNS(null, "onmouseout", "undoHover('react_link_" + i + "');");
      }
      
      textContent = document.createTextNode(reactants[i]);
      newText.appendChild(textContent);
      startY += 15;
      
      iwin.appendChild(newText);
   }
   
   
   newText = document.createElementNS(svgNS,"text");
   newText.setAttributeNS(null,"y", ""+startY );
   newText.setAttributeNS(null,"x", "100");
   newText.setAttributeNS(null,"style", "text-anchor: end;");
   textContent = document.createTextNode("Products:");
   newText.appendChild(textContent);
   iwin.appendChild(newText);   
   
   var specie;
   for(i=0; products[i]; i++) {
      specie = document.getElementById( products[i] );
   
      newText = document.createElementNS(svgNS,"text");
      newText.setAttributeNS(null,"id", "prod_link_" + i);
      newText.setAttributeNS(null,"y", ""+startY );
      newText.setAttributeNS(null,"x", ""+x1 );
      newText.setAttributeNS(null,"fill", "blue");
      if (specie && specie.getAttribute("onclick") ) {
         newText.setAttributeNS(null, "onclick" , specie.getAttribute("onclick"));
         newText.setAttributeNS(null, "onmouseover", "doHover('prod_link_" + i + "');");
         newText.setAttributeNS(null, "onmouseout", "undoHover('prod_link_" + i + "');");
      }
      
      textContent = document.createTextNode(products[i]);
      newText.appendChild(textContent);
      startY += 15;
      
      iwin.appendChild(newText);
   }   
      
}

function doHover(elmId) {
   var elm = document.getElementById(elmId);
   elm.setAttributeNS(null, "style", "text-decoration: underline");   
}

function undoHover(elmId) {
   var elm = document.getElementById(elmId);
   elm.setAttributeNS(null, "style", "text-decoration: none");   
}

function goHistoryBack(){
   if (historyBack.length>0) {   	  
   	  historyForward.push( historyNow );
   	  historyNow = historyBack.pop();   	  
      eval( historyNow );
      setDebugMessage( historyNow + ' ' + historyBack.length);
   }
}

function goHistoryForward(){
   if (historyForward.length>0) {   	  
   	  historyBack.push( historyNow );
   	  historyNow = historyForward.pop();
      eval( historyNow );
      setDebugMessage( historyNow + ' ' + historyForward.length );
   }
}