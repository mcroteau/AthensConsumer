<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.athens.common.ApplicationConstants" %>
<%@ page import="org.athens.domain.QuartzIngestLog" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.math.MathContext" %>
<html>
<head>
	<title>Athens: Ingest Logs</title>
</head>
<body>
    <style type="text/css">
        #kronos-ingest-logs-table{
            margin-top:3px;
        }
        #pagination{
            margin:24px auto 10px auto;
        }
        .navigation-container p{
            line-height:1.0em;
        }
        .navigation-container p span{
            display:inline:block;
        }
        .total-title{
            font-size:13px;
            text-align:right;
            padding-right: 3px;
        }
        .total-value{
            font-weight:bold;
            text-align:right;
        }
        .float-left,
        .float-right{
             /**border:solid 1px #ddd;**/
        }
    </style>


    <c:choose>
		<c:when test="${kronosIngestLogs.size() > 0}">

	        <h2 class="float-left">Ingests</h2>

            <div class="navigation-container float-right">

                <div class="btn-toolbar">

                    <div id="pagination" class="btn-group">

                        <%  int total = Integer.parseInt(request.getAttribute("total").toString());
                            int resultsPerPage = Integer.parseInt(request.getAttribute("resultsPerPage").toString());
                            int activePage = Integer.parseInt(request.getAttribute("activePage").toString());

                            int currentPage = 1;
                            int numberPages = 0;
                            int nextPage = 2;
                            int nextPageOffset = 10;
                            int previousPage = 1;
                            int previousPageOffset = 0;
                            boolean firstPass = true;
                            boolean lastPass = false;


                            for(int m = 0; m < total; m++){
                                if(m % resultsPerPage == 0){%>

                                    <%if(currentPage - activePage  <= 3 && activePage - currentPage <= 3){%>
                                        <%previousPageOffset = m - 10;%>
                                        <%previousPage = currentPage -1;%>

                                        <%if(previousPageOffset >= 0 && firstPass && activePage != 1 && activePage != 4){%>
                                            <%firstPass = false;%>
                                            <a href="${pageContext.request.contextPath}/ingests?offset=<%=previousPageOffset%>&max=<%=resultsPerPage%>&page=<%=previousPage%>" class="btn btn-default">&laquo;</a>
                                        <%}%>
                                        <%if(activePage == currentPage){%>
                                            <a href="${pageContext.request.contextPath}/ingests?offset=<%=m%>&max=<%=resultsPerPage%>&page=<%=currentPage%>" class="btn  btn-default active"><%=currentPage%></a>
                                        <%}else{%>
                                            <a href="${pageContext.request.contextPath}/ingests?offset=<%=m%>&max=<%=resultsPerPage%>&page=<%=currentPage%>" class="btn btn-default"><%=currentPage%></a>
                                        <%}%>

                                        <%nextPage = currentPage + 1;%>
                                        <%nextPageOffset = m + 10;%>
                                        <%if(currentPage * 10 < total && currentPage - activePage == 3){%>
                                            <a href="${pageContext.request.contextPath}/ingests?offset=<%=nextPageOffset%>&max=<%=resultsPerPage%>&page=<%=nextPage%>" class="btn btn-default">&raquo;</a>
                                        <%}%>

                                    <%}%>

                                    <%currentPage++;%>
                                <%}%>

                        <%}%>

                    </div>
                </div>
                <p style="text-align:right;"><span class="total-title">Total:&nbsp</span><span class="total-value">${total}</span></p>
            </div>

            <br class="clear"/>

            <table class="table" id="kronos-ingest-logs-table">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Date</th>
                        <th>e</th>
                        <th>Processed</th>
                        <th>Total</th>
                        <th>Job</th>
                        <th>Status</th>
                        <th>Percent</th>
                    </tr>
                </thead>
                <tbody>
                <%List<QuartzIngestLog> kronosIngestLogs = (ArrayList) request.getAttribute("kronosIngestLogs");%>
                <%for(QuartzIngestLog kronosIngestLog : kronosIngestLogs){%>
                    <tr>
                        <td><%=kronosIngestLog.getId()%></td>
                        <td><%=kronosIngestLog.getKdate()%></td>
                        <td><%=kronosIngestLog.getKadtcnt()%></td>
                        <td><%=kronosIngestLog.getKproc()%></td>
                        <td><%=kronosIngestLog.getKtot()%></td>
                        <td><%=kronosIngestLog.getKtype()%></td>
                        <td>
                            <%if(kronosIngestLog.getKstatus().trim().equals("Running")){%>
                                <a href="${pageContext.request.contextPath}/jobs"><%=kronosIngestLog.getKstatus()%></a>
                                <img src="${pageContext.request.contextPath}/images/loading.gif" class="loading" id="loading"/>
                            <%}else{%>
                                <%=kronosIngestLog.getKstatus()%>
                            <%}%>
                        </td>
                        <td><%=kronosIngestLog.getPercent()%>%</td>
                    </tr>
                <%}%>
                </tbody>
            </table>

		</c:when>
		<c:when test="${kronosIngestLogs.size() == 0}">
	        <h2 class="float-left">Ingests</h2>
            <br class="clear"/>
		    <p>No Ingests found</p>
		</c:when>
	</c:choose>

</body>
</html>