<%@page import="java.sql.*" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.GregorianCalendar" %>
<%@page import="java.util.Calendar" %>
<%@page pageEncoding="UTF-8"%>
<%!

private String niceDate( Date d ) {
	Calendar clndToday = new GregorianCalendar();
	Calendar clndDate = new GregorianCalendar( d.getYear() + 1900, d.getMonth(), d.getDate() );
	
	if ( clndToday.getTime().getYear() ==  clndDate.getTime().getYear() && 
		 clndToday.getTime().getMonth() ==  clndDate.getTime().getMonth()	&&
		 clndToday.getTime().getDate() ==  clndDate.getTime().getDate() ) {
		return "hoy";
	}
	
	clndDate.add(Calendar.DATE, 1);
	
	if ( clndToday.getTime().getYear() ==  clndDate.getTime().getYear() && 
	         clndToday.getTime().getMonth() ==  clndDate.getTime().getMonth()   &&
	         clndToday.getTime().getDate() ==  clndDate.getTime().getDate() ) {
	    return "ayer";
	}
	
    clndDate.add(Calendar.DATE, 1);
    
    if ( clndToday.getTime().getYear() ==  clndDate.getTime().getYear() && 
             clndToday.getTime().getMonth() ==  clndDate.getTime().getMonth()   &&
             clndToday.getTime().getDate() ==  clndDate.getTime().getDate() ) {
        return "anteayer";
    }	
    
    
    java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
	return sdf.format( d );
		 
}



%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8"/>
<style type="text/css">
html {
  font-family: arial;
  font-size: 0.8em;
}

body {
    width: 800px;
    border-right: 1px solid #bbf;
    border-left: 1px solid #bbf;
    padding: 20px;
    margin-left: auto;
    margin-right: auto;
}

label {
  display: block;  
  font-weight: bold;
  min-width: 100px;
  width: 100px;
}

input[type='text'], input[type='email'] {  
  border: 1px  solid #ddd;
  width: 400px;
}

table {
    border-spacing: 0px;
    width: 800px;
}
    
    
td.status {
    color: #fff;
    font-weight: bold;
    font-size: 90%;
}

span.r {
    background-color: green;
    color: #fff;
    padding: 1px 4px 1px 4px;
    padding-right: 4px;
}

span.w {
    background-color: blue;
    color: #fff;
    padding: 1px 4px 1px 4px;
    padding-right: 4px;
}

span.e {
    background-color: red;
    color: #fff;
    padding: 1px 4px 1px 4px;
    padding-right: 4px;
}

td {
    vertical-align: top;
    padding-top: 4px;
    padding-left: 16px;
    padding-right: 8px;
    margin-left: 10px;
    margin-right: 0px;
    border-bottom: 1px solid #eee;
    border-left: 0px;
    border-right: 0px;
    
    /* background-color: #fff; */    
}

td.nombre {
    width: 50%;
}


tr {
    border-bottom: 1px solid #aaa;
    border-spacing: 0px;
}

.prefijo {
    color: #888;
}

.descr {
    color:#888;
    font-size: 90%;
    margin: 1px;
}
.fecha{
    /*writing-mode: tb-rl;*/
    font-style: italic;  
}

.pagination span {
    border: 1px solid #aaa;
    padding: 2px 5px 2px 5px;
    font-weight: bold;    
    
    color: #aaa;
}
   
.pagination a {
    text-decoration: none;
      color: #aaa;
}

.pagination span.actual  {
    background-color: blue;
    text-decoration: none;
    color: white;
}
.pagination span.actual:hover  {
    background-color: blue;
    text-decoration: none;
    color: white;
}

.pagination span:hover {
    background-color: white;
    color: black;
    border-color: blue;
}

.pagination span:hover {    
    background-color: white;
    color: black;
}
/*
.pagination span.actual:hover {
    color: black;
    border-color: #aaa;
}
*/

</style>
<title>Diagramas publicados</title>
</head>
<body>
<object type="application/x-java-applet" height="400" width="600" id="viewer">
    <param name="archive" value="batik-gui-util.jar, batik-awt-util.jar, batik-bridge.jar, batik-css.jar, batik-dom.jar,batik-ext.jar,batik-gvt.jar,batik-parser.jar,batik-svg-dom.jar,batik-script.jar,batik-swing.jar,batik-util.jar,batik-xml.jar,xml-apis-dom3.jar" />
    <param name="codebase" value="applet/" />
    <param name="code" value="ViewerApplet.class" />
    <param name="svgfile" value="<%=request.getParameter("svgfile")%>" />
    <!-- <param name="mayscript" value="mayscript" /> -->
            [Your browser doesn&rsquo;t seem to support Java applets, which is required
            for this viewer.]
</object>


</body>
</html> 
