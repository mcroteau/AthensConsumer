<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Punch Report Logs</title>


	<meta name="viewport" content="width=device-width, initial-scale=1.0">


	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/jquery/jquery.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" />

	<!--<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/datepicker/bootstrap-datepicker.js"></script>-->
	<script type="text/javascript" src="${pageContext.request.contextPath}/bootstrap/js/bootstrap-datepicker.min.js"></script>


	<!--<link rel="stylesheet" href="${pageContext.request.contextPath}/js/lib/datepicker/datepicker.css" />-->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-datepicker.min.css" />

	<style type="text/css">
	#date-selectors{
    }
    #date-selectors input[type="text"]{
    	width:100px;
    	font-size:12px;
    	display:inline-block;
    	color:rgba(0,0,0,0.54) !important;
    }

    #date-selectors span{
    	font-size:12px;
    }

    .datepicker table tr td.active,
    .datepicker table tr td.active:hover,
    .datepicker table tr td.active.disabled,
    .datepicker table tr td.active.disabled:hover{
    	border:none !important;
    	background:#1c695b !important;
    }
    </style>
</head>

<body>

    <img src="${pageContext.request.contextPath}/images/athens-logo.png"/>

	<div class="span12">
		<h1>Punch Ingests</h1>
	</div>


	<c:if test="${not empty message}">
		<div class="span12">
			<div class="alert alert-info">
				${message}
			</div>
		</div>
	</c:if>


	<form action="${pageContext.request.contextPath}/krnwh/search" method="post">

        <div class="input-group input-daterange">
            <input name="start-date" type="text" class="form-control" value="" id="start-date">
            <div class="input-group-addon">to</div>
            <input name="end-date" type="text" class="form-control" value="" id="end-date">
        </div>

		<div class="form-group">
			<input type="submit" value="Get Data" class="btn btn-primary">
		</div>

    </form>

	<c:choose>
		<c:when test="${krnwhLogs.size() > 0}">

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
	String.prototype.pad = function(padString, length) {
        var str = this;
        while (str.length < length)
            str = padString + str;
        return str;
    }

	$(document).ready(function(){

        var $startDate = $("#start-date"),
            $endDate = $("#end-date");

        $startDate.datepicker(getFormat());
        $endDate.datepicker(getFormat());

        $startDate.datepicker("update", getYesterday());
        $endDate.datepicker("update", new Date());


        function getYesterday(){
            var d = new Date();
            d.setDate(d.getDate() - 2);
            return d;
        }

        function getFormat(){
            return {
                   format: {
                       /*
                        * Say our UI should display a week ahead,
                        * but textbox should store the actual date.
                        * This is useful if we need UI to select local dates,
                        * but store in UTC
                        */
                       toDisplay: function (date, format, language) {
                           var d = new Date(date);
                           //d.setDate(d.getDate() - 7);
                           //return d.toISOString();
                           var h = d.getFullYear() + "" + (d.getMonth() +1).toString().pad("0", 2) +"" + (d.getDate() +1).toString().pad("0", 2) + "000000";;
                           return h;
                       },
                       toValue: function (date, format, language) {
                           var d = new Date(date);

                           var h = d.getFullYear() + "" + (d.getMonth() +1).toString().pad("0", 2) +"" + (d.getDate() +1).toString().pad("0", 2) + "000000";
                       }
                   }
               }
        }

	});
	</script>
</body>
</html>