<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Athens: Ingest Logs</title>
</head>
<body>

	<h1>Ingests</h1>

    <c:choose>
		<c:when test="${kronosIngestLogs.size() > 0}">

			<div class="span12">
    			<table class="table table-condensed">
					<thead>
						<tr>
							<th>Id</th>
							<th>Date</th>
							<th>e</th>
							<th>Processed</th>
							<th>Total</th>
							<th>Status</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="kronosIngestLog" items="${kronosIngestLogs}">
							<tr>
								<td>${kronosIngestLog.id}</td>
								<td>${kronosIngestLog.kdate}</td>
								<td>${kronosIngestLog.kadtcnt}</td>
								<td>${kronosIngestLog.kproc}</td>
								<td>${kronosIngestLog.ktot}</td>
								<td>${kronosIngestLog.kstatus}</td>
								<!--<td><a href="${pageContext.request.contextPath}/krnwh/list_ingest?ingest=${krnwhLog.id}" title="View Ingest" class="btn btn-default">View Ingest</a></td>-->
                            	<td><a href="${pageContext.request.contextPath}/krnwh/list" title="View Ingest" class="btn btn-default">View Ingest</a></td>
                            </tr>
						</c:forEach>
					</tbody>
				</table>

			</div>

		</c:when>
		<c:when test="${krnwhLogs.size() == 0}">
		    <p>No Ingests found</p>
		</c:when>
	</c:choose>

</body>
</html>