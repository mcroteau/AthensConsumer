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
            margin-top:37px;
        }
    </style>

	<h2>Ingests</h2>

    <c:choose>
		<c:when test="${kronosIngestLogs.size() > 0}">

            <table class="table table-condensed" id="kronos-ingest-logs-table">
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
                        <th></th>
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
                        <td><%=kronosIngestLog.getKstatus()%></td>
                        <td><%=kronosIngestLog.getPercent()%>%</td>
                        <%if(kronosIngestLog.getKstatus().trim().equals("Running")){%>
                            <td class="running-job">
                                <img src="${pageContext.request.contextPath}/images/loading.gif" class="loading pull-right" id="loading"/>
                                <a href="${pageContext.request.contextPath}/jobs">View</a>
                            </td>
                        <%}else{%>
                            <td></td>
                        <%}%>
                    </tr>
                <%}%>
                </tbody>
            </table>

		</c:when>
		<c:when test="${kronosIngestLogs.size() == 0}">
		    <p>No Ingests found</p>
		</c:when>
	</c:choose>

</body>
</html>