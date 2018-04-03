<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Punch Report Logs</title>
</head>

<body>

    <img src="${pageContext.request.contextPath}/images/athens-logo.png"/>

	<div class="span12">
		<h1>Punch Ingests</h1>
	</div>


<span>count: ${jobCount}</span>

	<c:if test="${not empty message}">
		<div class="span12">
			<div class="alert alert-info">
				${message}
			</div>
		</div>
	</c:if>

    <c:choose>
		<c:when test="${krnwhLogs.size() > 0}">

            <a href="${pageContext.request.contextPath}/krnwh/search" title="Run Job" class="btn btn-default">Search</a>
            <a href="${pageContext.request.contextPath}/index" title="Run Job" class="btn btn-default">Run Todays</a>

			<div class="span12">

    				<table class="table table-condensed">
					<thead>
						<tr>
							<th>Id</th>
							<th>Date</th>
							<th>Total</th>
							<th>Error Count</th>
							<th>Status</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<!--
                    " kstatus: " + this.getKstatus() +
                    " ktot: " + this.getKtot() +
                    " kadtcnt: " + this.getKadtcnt() +
                    " kaudit: " + this.getKaudit() +
                    " kdate: " + this.getKdate() + "\n";
                    -->

						<c:forEach var="krnwhLog" items="${krnwhLogs}">
							<tr>
								<td></td>
								<td>${krnwhLog.kdate}</td>
								<td>${krnwhLog.ktot}</td>
								<td>${krnwhLog.kadtcnt}</td>
								<td>${krnwhLog.kstatus}</td>
								<!--<td><a href="${pageContext.request.contextPath}/krnwh/list_ingest?ingest=${krnwhLog.id}" title="View Ingest" class="btn btn-default">View Ingest</a></td>-->
                            	<td><a href="${pageContext.request.contextPath}/krnwh/list" title="View Ingest" class="btn btn-default">View Ingest</a></td>
                            </tr>
						</c:forEach>
					</tbody>
				</table>

			</div>

		</c:when>
		<c:when test="${krnwhLogs.size() == 0}">
			<p>No Punch Logs created yet.</p>
		</c:when>
	</c:choose>
</body>
</html>