<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Punch Report Logs</title>

	<style type="text/css">
	    .job-status{
	        display:none;
	    }
	    .loading{
	        height:10%;
	        width:10%;
	    }
	</style>
</head>
<body>
keystrokes being copied
	<div class="span12">
		<h1>Ingests</h1>
	</div><img src="${pageContext.request.contextPath}/images/love.gif" class="loading"/>

    <div id="daily-status" class="job-status">
       Daily Running
            processed <span id="daily-processed"></span>
    </div>
    <div id="weekly-status" class="job-status">
        Weekly Running
            processed <span id="weekly-processed"></span>
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
            var timer = 0;

            var dailyPercent = 0,
                weeklyPercent = 0;

            var $checkButton     = $("#status-check"),
                $dailyStatus     = $("#daily-status"),
                $weeklyStatus    = $("#weekly-status"),
                $dailyTotal      = $("#daily-total"),
                $dailyProcessed  = $("#daily-processed"),
                $dailyErrored    = $("#daily-errored"),
                $dailyPercent    = $("#daily-percent"),
                $weeklyTotal     = $("#weekly-total"),
                $weeklyProcessed = $("#weekly-processed"),
                $weeklyErrored   = $("#weekly-errored"),
                $weeklyPercent   = $("#weekly-percent");


            $checkButton.click(function(event){
                runStatusCheck();
            });

            function runStatusCheck(){
                console.log("about to run job check");
                $.ajax({
                    url : "${pageContext.request.contextPath}/status",
                    dataType :'json',
                    success : checkDisplayRunningJobs,
                    error : function(){
                        console.log("error");
                    }
                });
            }

            function checkDisplayRunningJobs(json, b){
                console.log(json, b);
                if(json.dailyJobRunning){
                    console.log("here...")
                    $dailyStatus.show();
                    $dailyProcessed.html(json.dailyJobRunning.processed);
                }
                if(json.weeklyJobRunning){
                    console.log("here...")
                    $weeklyStatus.show();
                    $weeklyProcessed.html(json.weeklyJobRunning.processed);
                }
                console.log("here...")
            }

            function getSetJobStats(stats, $total, $processed, $errored){
                stats.total = parseInt(stats.total);
                stats.processed = parseInt(stats.processed);
                stats.errored = parseInt(stats.errored);
                stats.percent = Math.round((total / processed) * 100);

            }

            function setTimer(){
                timer = setInterval(function(){
                    runStatusCheck();
                }, 3000);
            }

            runStatusCheck();
            setTimer();
        });

	</script>
</body>
</html>