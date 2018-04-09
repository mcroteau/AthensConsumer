<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.athens.common.ApplicationConstants" %>
<%@ page import="org.athens.domain.QuartzIngestLog" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
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
                        <th>Status</th>
                        <th>Percent</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="kronosIngestLog" items="${kronosIngestLogs}">
                    <tr>
                        <td>${kronosIngestLog.id}</td>
                        <td>${kronosIngestLog.kadtcnt}</td>
                        <td>${kronosIngestLog.ktot}</td>
                        <td>${kronosIngestLog.kproc}</td>
                        <td>${kronosIngestLog.kstatus}</td>
                        <td>${kronosIngestLog.kproc / kronosIngestLog.ktot}</td>
                        <c:choose>
                            <c:when test="${kronosIngestLog.kstatus == 'Complete'}">
                                <td><a href="ref">Status</a></td>
                            </c:when>
                            <c:otherwise>
                                <td></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>

                <%List<QuartzIngestLog> kronosIngestLogs = (ArrayList) request.getAttribute("kronosIngestLogs");%>
                <%for(QuartzIngestLog kronosIngestLog : kronosIngestLogs){%>
                    <%BigDecimal percent = kronosIngestLog.getKproc().divide(kronosIngestLog.getKtot(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));%>
                    <tr>
                        <td><%=kronosIngestLog.getId()%></td>
                        <td><%=kronosIngestLog.getKdate()%></td>
                        <td><%=kronosIngestLog.getKadtcnt()%></td>
                        <td><%=kronosIngestLog.getKtot()%></td>
                        <td>Daily</td>
                        <td><%=kronosIngestLog.getKproc()%></td>
                        <td><%=kronosIngestLog.getKstatus()%></td>
                        <td><%=percent%>%</td>
                        <%if(kronosIngestLog.getKstatus().equals("Complete") || kronosIngestLog.getKstatus().equals("Started") ){%>
                            <td class="running-job">
                                <a href="${pageContext.request.contextPath}/jobs">Status</a>
                            </td>
                        <%}else{%>
                            <td>--</td>
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