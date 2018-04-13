<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
<title><decorator:title /></title>

	<meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/jquery/jquery.min.js"></script>

	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-reboot.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" />

	<script type="text/javascript" src="${pageContext.request.contextPath}/bootstrap/js/bootstrap-datepicker.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-datepicker.min.css" />


    <style type="text/css">
        body{
            padding:0px
            text-align:center;
			font-family:arial;
			color:black;
        }
        #top-header{
            padding:0px;
            height:13px;
        }
        #header-background{
            height:391px;
            width:100%;
            z-index:0;
            background:#D4212F;
            position:absolute;
            top:0px;
            height:100%;
            <c:choose>
                <c:when test="${kronosIngestLogs == null || kronosIngestLogs.size() == 0}">
                    height:100%;
                </c:when>
            </c:choose>
        }
        #container{
            width:897px !important;
            padding:30px 43px 74px;
            margin:30px auto 200px auto;
            text-align:left;
            background:#fff;
            z-index:1;
            position:relative;
            box-shadow: 0px 0px 13px 0px rgba(0,0,0,0.38);
            -moz-box-shadow: 0px 0px 13px 0px rgba(0,0,0,0.38);
            -webkit-box-shadow: 0px 0px 13px 0px rgba(0,0,0,0.38);
        }
        #logo{
            margin-top:7px;
        }
        #navigation{
            float:right;
            margin-top:10px;
            background:#272728;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            border-radius: 4px;
            box-shadow: 0px 0px 7px 0px rgba(0,0,0,0.21);
            -moz-box-shadow: 0px 0px 7px 0px rgba(0,0,0,0.21);
            -webkit-box-shadow: 0px 0px 7px 0px rgba(0,0,0,0.21);
        }
        #navigation ul{
            padding:0px;
            margin:0px;
            height:49px;
            list-style:none;
            -webkit-border-radius: inherit;
            -moz-border-radius: inherit;
            border-radius:inherit;
        }
        #navigation li{
            float:right;
            padding:0px;
            margin:0px;
            display:inline-block;
            -webkit-border-radius: inherit;
            -moz-border-radius: inherit;
            border-radius:inherit;
        }
        #navigation li a{
            color:#fff;
            font-weight:100;
            font-size:12px;
            margin:0px;
            padding:17px 34px;
            display:inline-block;
            text-decoration:none;
            text-transform:uppercase;
            border:solid 0px #ffff00;
            background:inherit;
            border-bottom:solid 0px #ffff00;
            -webkit-border-radius: inherit;
            -moz-border-radius: inherit;
            border-radius:inherit;
            border-right:solid 1px #000;
            border-left:solid 1px #3e3e3e;
        }
        #navigation li a:hover,
        #navigation li a.active{
            background:#000;
        }

        #jobs-status-container{
            font-size:11px;
            text-align:right;
        }
        .job-status-container{
            display:inline-block;
            border:solid 0px #ddd;
        }
        .job-status-title{
             width:67px;
             font-weight:bold;
             display:inline-block;
             text-transform:uppercase;
             border:solid 0px #ddd;
        }
        .job-status-value{
             width:67px;
             text-align:left;
             display:inline-block;
             opacity:0.63;
             text-transform:uppercase;
             border:solid 0px #ddd;
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
        h1{
            font-size:48px;
            font-weight:bold;
            margin-top: 30px;
            padding-left: 21px;
            border-left:solid 6px #D4212F;
        }
        h2{
            font-size:37px;
            font-weight:bold;
            padding-left: 18px;
            margin:24px auto 0px auto;
            border-left:solid 6px #D4212F;
        }
        .loading{
            height:13px;
        }
        .clear{
            clear:both;
        }
    </style>

    <decorator:head />
</head>

<body>

    <div id="header-background"></div>

    <div id="container">

        <div id="top-header">
            <div id="jobs-status-container">
                <span class="job-status-container">
                    <span class="job-status-title">DAILY:&nbsp;</span>
                    <span class="job-status-value" id="daily-status-value">-</span>
                </span>
                <span class="job-status-container">
                    <span class="job-status-title">WEEKLY:&nbsp;</span>
                    <span class="job-status-value" id="weekly-status-value">-</span>
                </span>
                <img src="${pageContext.request.contextPath}/images/loading.gif" class="loading pull-right" id="loading" style="display:none"/>
                <img src="${pageContext.request.contextPath}/images/loading-stopped.png" class="loading pull-right" id="loading-stopped" style="display:none"/>
            </div>
            <br class="clear"/>
        </div/>

        <img src="${pageContext.request.contextPath}/images/athens-logo.png" id="logo"/>



		<div id="navigation">
			<ul>
				<li><a href="${pageContext.request.contextPath}/ingests" class="${ingestsLinkActive}">Ingests</a></li>
				<li><a href="${pageContext.request.contextPath}/search?startDate=${todaysDate}&endDate=${tomorrowsDate}" class="${searchLinkActive}">Search</a></li>
				<li><a href="${pageContext.request.contextPath}/jobs" class="${runningJobsLinkActive}">Jobs</a></li>
			</ul>
		</div>

        <br class="clear"/>

        <c:if test="${not empty message}">
            <div class="alert alert-info" style="margin-top:20px;">
                ${message}
            </div>
        </c:if>


        <decorator:body />

    </div>

	<script type="text/javascript">
        $(document).ready(function(){

            var globalTimer = 0;

            var IDLE_VALUE = "idle",
                STARTED_VALUE = "started",
                RUNNING_VALUE = "running";

            var $loading=$("#loading"),
                $loadingStopped  =$("#loading-stopped");

            var $dailyStatusValue = $("#daily-status-value"),
                $weeklyStatusValue = $("#weekly-status-value");

            function checkDisplayRunningJobsGlobal(json, b){
                resetGlobalStatusLoading();
                if((json.dailyJobRunning && json.dailyJobRunning.status != "Complete") ||
                        (json.weeklyJobRunning && json.weeklyJobRunning.status != "Complete")){
                    $loading.show();
                }
                if(json.dailyJobRunning){
                    $dailyStatusValue.html(json.dailyJobRunning.status.toUpperCase());
                }
                if(json.weeklyJobRunning){
                    $weeklyStatusValue.html(json.weeklyJobRunning.status.toUpperCase());
                }
            }

            function resetGlobalStatusLoading(){
                $loading.hide();
                $dailyStatusValue.html(IDLE_VALUE);
                $weeklyStatusValue.html(IDLE_VALUE);
            }

            function runStatusCheckGlobal(){
                $.ajax({
                    url : "${pageContext.request.contextPath}/status",
                    dataType :'json',
                    crossDomain: true,
                    success : checkDisplayRunningJobsGlobal,
                    error : function(){
                        console.log("error");
                    }
                });
            }


            function setTimerGlobal(){
                globalTimer = setInterval(function(){
                    runStatusCheckGlobal();
                }, 4000);
            }


            runStatusCheckGlobal();
            setTimerGlobal()

        });

    </script>
</body>
</html>