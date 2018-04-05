<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Punch Report Logs</title>

	<style type="text/css">
	    .job-status{
	        display:none;
	    }
	</style>
</head>
<body>

	<div class="span12">
		<h1>Punch Ingests</h1>
	</div>

    <div id="daily-status" class="job-status">
        <a href="${pageContext.request.contextPath}/running_jobs">Daily Running</a>
    </div>
    <div id="weekly-status" class="job-status">
        <a href="${pageContext.request.contextPath}/running_jobs">Weekly Running</a>
    </div>

    <a href="javascript:" id="status-check">Check Status</a>


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

	<script type="text/javascript">
        $(document).ready(function(){
            var $checkButton = $("#status-check"),
                $dailyStatus = $("#daily-status"),
                $weeklyStatus = $("#weekly-status");

            $checkButton.click(function(event){
                runStatusCheck();
            });

            function runStatusCheck(){
                console.log("about to run job check");
                $.ajax({
                    url : "${pageContext.request.contextPath}/status",
                    success : checkDisplayRunningJobs,
                    error : function(){
                        console.log("error");
                    }
                });
            }

            function checkDisplayRunningJobs(a, b){
                console.log(a, b);
                $dailyStatus.show();
                $weeklyStatus.show();
            }

            runStatusCheck();
        });

	</script>
</body>
</html>