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
    background-color: #0f1;
    color: #fff;
    padding: 1px 4px 1px 4px;
    padding-right: 4px;
}

span.r a {
    
    color: #fff;
    text-decoration: none;
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
<%
int maxResultsPerPage = 5;
int actualPage = 1;

if (request.getParameter("page")!=null) {
	try {
		   actualPage = Integer.parseInt( request.getParameter("page") );
	} catch (NumberFormatException nfe) {
		actualPage = 1;
	}
}

response.setContentType("text/html; charset=UTF-8");
response.setCharacterEncoding("UTF-8");
try {
    Class.forName("com.mysql.jdbc.Driver");
    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/sbml2svg?useUnicode=true&characterEncoding=UTF-8", "root", "mysql1234");

    Statement stm = conexion.createStatement();

    String sql = "SELECT SQL_CALC_FOUND_ROWS * FROM diagram d order by fh_upload desc limit " + 
                 maxResultsPerPage*(actualPage-1) + " , " + maxResultsPerPage;
    
    ResultSet rs = stm.executeQuery(sql);
    
    
    if (rs.first()) {
%>
<table>    
<%     	
       do {;        
%>
<tr>
    <td class="nombre">
       <%= rs.getString("nombre") %>
       <p class="descr"><%= rs.getString("descripcion") %></p>
    </td>
     <td class="subido">
       <span class="fecha"><%=niceDate(rs.getDate("fh_upload"))%></span><br/>
       <span class="prefijo">por </span>
       <%= rs.getString("autor") %>
    </td>   
    <td class="status"><span class="<%=rs.getString("status")%>" title="<%= rs.getString("error") %>">
       <% if ( rs.getString("status").equals("w") ) {
    	    out.print("En&nbsp;cola&nbsp;");
          } else if ( rs.getString("status").equals("r") ) {
        	out.print("<a href=\"showDiagram.jsp?svgfile=" + rs.getString("sbmlfile").replaceAll(".xml",".svg") + "\">Listo </a>");  
          } else if ( rs.getString("status").equals("e") ) {
        	  out.print("Error ");  
          }
    	%> 
    </span>	      
    </td>
            
</tr>
<%      
         } while ( rs.next() );    
    } else {
    	
    }
    
    String sqlCount = "SELECT FOUND_ROWS() as _count;";
    ResultSet rsCount = stm.executeQuery(sqlCount);
    int count = 0;
    if (rsCount.next()) {
        count = rsCount.getInt("_count");
    }
    int pages = (int) Math.ceil( 1.0*count / (1.0*maxResultsPerPage) );  
%>
</table>
<br/> 
<div class="pagination">
Página <%=actualPage%> de <%=pages%> :
<%
if ( pages > 10 ) {
	//resumir
} else {
	for (int i=1; i<=pages; i++) {
	   if (actualPage==i){
%>
      <span class="actual"><%=i%></span>   
<%      		   
	   } else {
%>
	   <a href="listDiagrams.jsp?page=<%=i%>" ><span><%=i%></span></a>   
<%		
	   } 
	}
}
%>   
</div>  
<%      
} catch (ClassNotFoundException e) {
    e.printStackTrace();
} catch (SQLException e) {
    e.printStackTrace();
}
%>

</body>
</html> 
