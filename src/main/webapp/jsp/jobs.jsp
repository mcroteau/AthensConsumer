<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Athens: Running Jobs</title>
</head>
<body>

<h2>Running Jobs</h2>

    <style type="text/css">
        div{
            padding:0px;
            margin:0px;
            border:solid 0px #ddd;
            line-height:1.3em;
        }
        .stats-container{
            width:382px;
            padding:31px;
            margin-top:24px;
            border:solid 1px #ddd;
        }
        .indicator-container{
            margin-bottom:3px;
        }
        .indicator{
            height:10px;
            width:10px;
        }
        .indicator.started{
            background:#2272EF;
        }
        .indicator.running{
            background:#04be1f;
        }
        .indicator.complete{
            background:#323232;
        }
        .indicator.idle{
            background:#bcbcbd;
        }
        .stats-header{
            margin:0px auto 0px auto;
        }
        .stats-header h3{
            margin:4px auto 7px auto;
        }
        .total-percent{
            margin:0px auto 10px auto;
        }
        .total-processed{
            font-weight:bold;
            font-size:79px;
            font-size:54px;
            font-size:21px;
            line-height:1.0;
            text-align:right;
            /**margin-top:10px;**/
        }
        .processed-title{
            opacity:0.57;
            font-size:13px;
            text-align:right;
            line-height:1.0em;
            text-transform:uppercase;
        }
        .stats-total{
            margin-right:20px;
        }
        .stats-percent{
            font-size:21px;
            font-size:54px;
            font-weight:bold;
            text-align:right;
            margin-top:10px;
        }
        #daily-percent{
            margin:0px auto 00px auto !important;
            font-size:21px !important;
        }
        #daily-processed{
            font-size:54px !important;
            margin-top:10px !important;
            margin-bottom:7px;
        }
        .processed-progress-container{
            height:6px;
            margin-top:16px;
            position:relative;
        }
        .progress-bar{
            height:7px;
            display:block;
            position:absolute;
            border-bottom:solid 0px #999;
        }
        .base-progress-bar{
            width:100%;
            background:#bcbcbd;
        }
        .percent-progress-bar{
            width:3%;
            background:#D4212F;
        }
        .stats-details-outer-container{
            margin-top:27px;
        }
        .stats-details-container{
            width:43%;
        }
        .stats-details{
            margin:0px auto 10px;
        }
        .stats-details span{
            display:block;
            line-height:1.3em;
        }
        .stats-details-title{
            font-size:14px;
        }
        .manually-run-container{
            margin-top:13px;
            display:none;
        }
        .run-btn{
            padding:4px 12px;
            background:#fff;
            border:solid 1px #ddd;
        }
        .run-btn:hover{
            background:#efefef;
        }
        .ten-text{
            font-size:11px;
        }
        .align-right{
            text-align:right;
        }
        .inline{
            display:inline-block;
        }
        .bold{
            font-weight:bold;
        }
    </style>

    <div class="stats-container float-left">
        <div id="stats-top">
            <div class="indicator-container top-left float-left">
                <span class="indicator indicator-idle inline running" id="daily-indicator"></span>
                <span class="status ten-text inline" id="daily-indicator-value">Idle</span>
            </div>
            <div class="stats-top-right">
                <span class="stats-details-id-value float-right" id="daily-running-time">0 minutes</span>
            </div>
            <br class="clear"/>
        </div>
        <div class="stats-header">
            <h3>Daily</h3>
        </div>
        <div class="total-percent">
            <div class="percent-container float-right">
                <span class="stats-percent inline" id="daily-percent">0</span>
                <span class="percent-sign inline">%</span>
            </div>
            <div class="total-container float-right">
                <span class="total-title inline">Total:&nbsp;</span>
                <span class="stats-total inline" id="daily-total">0</span>
            </div>
            <br class="clear"/>
        </div>

        <div class="total-processed" id="daily-processed">0</div>

        <div class="processed-title">Processed</div>

        <div class="processed-progress-container">
            <span class="base-progress-bar progress-bar"></span>
            <span class="percent-progress-bar progress-bar" id="daily-progress-bar"></span>
        </div>

        <div class="stats-details-outer-container">
            <div class="stats-details-container float-left">
                <span class="stats-details-id-title">Id:&nbsp;#</span>
                <span class="stats-details-id-value" id="daily-log-id">0</span>
                <div class="stats-top-right">
                    <span class="time-title ten-text inline bold">Started:&nbsp;</span>
                    <span class="time-value ten-text inline bold" id="daily-time-started"></span>
                </div>
                <div id="run-daily-container" class="manually-run-container">
                    <form action="${pageContext.request.contextPath}/run_daily" method="post">
                        <input type="submit" value="Start" class="run-btn">
                    </form>
                </div>
            </div>
            <div class="stats-details-container float-right">
                <div class="stats-details">
                    <span class="stats-details-title float-left">Saved:&nbsp;</span>
                    <span class="stats-details-value float-right" id="daily-saved">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Overlap:&nbsp;</span>
                    <span class="stats-details-value float-right" id="daily-exists">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Errored:&nbsp;</span>
                    <span class="stats-details-value float-right" id="daily-errored">0</span>
                    <br class="clear"/>
                </div>
            </div>
            <br class="clear"/>
            <div class="running-times">
                <p class="float-left" style="margin:10px 0px 0px 0px">
                    <span class="total-title">Run Times:&nbsp</span><span class="total-value">4:45pm, 11:45pm</span>
                </p>
            </div>
        </div>

    </div>



    <div class="stats-container float-right">
        <div id="stats-top">
            <div class="indicator-container top-left float-left">
                <span class="indicator indicator-idle inline" id="weekly-indicator"></span>
                <span class="status ten-text inline" id="weekly-indicator-value">Idle</span>
            </div>
            <div class="stats-top-right">
                <span class="stats-details-id-value float-right" id="weekly-running-time">0 minutes</span>
            </div>
            <br class="clear"/>
        </div>
        <div class="stats-header">
            <h3>Weekly</h3>
        </div>
        <div class="total-percent">
            <span class="stats-total inline float-right" id="weekly-total">0</span>
            <span class="stats-total inline float-right" style="margin:0px 3px !important">/</span>
            <span class="total-processed float-right" id="weekly-processed">0</span>
            <span class="total-title inline float-right">Total:&nbsp;</span>
            <br class="clear"/>
        </div>


        <div class="percent-container" style="clear:both;">
            <span class="percent-sign inline float-right" style="margin-top:10px">%</span>
            <span class="stats-percent inline float-right" id="weekly-percent" style="line-height:1.3em;height:70px;margin:0px;padding:0px;">0</span>
        </div>
        <br class="clear"/>

        <div class="processed-title">Processed</div>

        <div class="processed-progress-container">
            <span class="base-progress-bar progress-bar"></span>
            <span class="percent-progress-bar progress-bar" id="weekly-progress-bar"></span>
        </div>

        <div class="stats-details-outer-container">
            <div class="stats-details-container float-left">
                <span class="stats-details-id-title">Id:&nbsp;#</span>
                <span class="stats-details-id-value" id="weekly-log-id">0</span>
                <div class="stats-top-right">
                    <span class="time-title ten-text inline bold">Started:&nbsp;</span>
                    <span class="time-value ten-text inline bold" id="weekly-time-started"></span>
                </div>
                <div id="run-weekly-container" class="manually-run-container">
                    <form action="${pageContext.request.contextPath}/run_weekly" method="post">
                        <input type="submit" value="Start" class="run-btn">
                    </form>
                </div>
            </div>
            <div class="stats-details-container float-right">
                <div class="stats-details">
                    <span class="stats-details-title float-left">Saved:&nbsp;</span>
                    <span class="stats-details-value float-right" id="weekly-saved">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Overlap:&nbsp;</span>
                    <span class="stats-details-value float-right" id="weekly-exists">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Errored:&nbsp;</span>
                    <span class="stats-details-value float-right" id="weekly-errored">0</span>
                    <br class="clear"/>
                </div>
            </div>
            <br class="clear"/>
            <div class="running-times">
                <p class="float-left" style="margin:10px 0px 0px 0px">
                    <span class="total-title">Run Times:&nbsp</span><span class="total-value">12:30am</span>
                </p>
            </div>
        </div>

    </div>


    <br class="clear"/>


    <script type="text/javascript">

        $(document).ready(function(){

            var statsTimer = 0;

            var formatter = new Intl.NumberFormat();

            var IDLE_CLASS = "idle",
                STARTED_CLASS = "started",
                RUNNING_CLASS = "running";

            var $dailyIndicator = $("#daily-indicator"),
                $weeklyIndicator = $("#weekly-indicator");

            var $dailyIndicatorValue = $("#daily-indicator-value"),
                $weeklyIndicatorValue = $("#weekly-indicator-value");

            var $dailyTimeStarted = $("#daily-time-started"),
                $weeklyTimeStarted = $("#weekly-time-started");

            var $dailyTotal = $("#daily-total"),
                $weeklyTotal = $("#weekly-total");

            var $dailyProcessed = $("#daily-processed"),
                $weeklyProcessed = $("#weekly-processed");

            var $dailyPercent = $("#daily-percent"),
                $weeklyPercent = $("#weekly-percent");

            var $dailyProgressBar = $("#daily-progress-bar"),
                $weeklyProgressBar = $("#weekly-progress-bar");


            var $dailyLogId = $("#daily-log-id"),
                $weeklyLogId = $("#weekly-log-id");


            var $dailyRunningTime = $("#daily-running-time"),
                $weeklyRunningTime = $("#weekly-running-time");


            var $runDailyContainer = $("#run-daily-container"),
                $runWeeklyContainer = $("#run-weekly-container");


            var $dailyExists = $("#daily-exists"),
                $dailySaved = $("#daily-saved"),
                $dailyErrored = $("#daily-errored"),
                $weeklyExists = $("#weekly-exists"),
                $weeklySaved = $("#weekly-saved"),
                $weeklyErrored = $("#weekly-errored");



            function runStatusCheck(){
                $.ajax({
                    url : "${pageContext.request.contextPath}/status",
                    dataType :'json',
                    success : renderStatistics,
                    error : function(){
                        console.log("error");
                    }
                });
            }

            function renderStatistics(json, c){
                resetIndicatorsResetstart();
                if(json.dailyJobRunning){
                    setIndicatorValues($dailyIndicator, $dailyIndicatorValue, json.dailyJobRunning);
                    setTimeStarted($dailyTimeStarted, json.dailyJobRunning);
                    setProcessedStatistics($dailyTotal, $dailyProcessed, $dailyPercent, json.dailyJobRunning);
                    SET_DETAIL_STATISTICS($dailyExists, $dailySaved, $dailyErrored, json.dailyJobRunning);//Not me
                    setlogid($dailyLogId, json.dailyJobRunning);//Not me
                    setRunningTime($dailyRunningTime, json.dailyJobRunning);
                    setProgressBar($dailyProgressBar, json.dailyJobRunning);
                }
                if(json.weeklyJobRunning){
                    setIndicatorValues($weeklyIndicator, $weeklyIndicatorValue, json.weeklyJobRunning);
                    setTimeStarted($weeklyTimeStarted, json.weeklyJobRunning);
                    setProcessedStatistics($weeklyTotal, $weeklyProcessed, $weeklyPercent, json.weeklyJobRunning);
                    SET_DETAIL_STATISTICS($weeklyExists, $weeklySaved, $weeklyErrored, json.weeklyJobRunning);//Not me
                    setlogid($weeklyLogId, json.weeklyJobRunning);//Not me
                    setRunningTime($weeklyRunningTime, json.weeklyJobRunning);
                    setProgressBar($weeklyProgressBar, json.weeklyJobRunning);
                }

                if(!json.dailyJobRunning || json.dailyJobRunning.status == 'Complete'){
                    showManuallyRun($runDailyContainer);
                }

                if(!json.weeklyJobRunning || json.weeklyJobRunning.status == 'Complete'){
                    showManuallyRun($runWeeklyContainer);
                }
            }

            function setRunningTime($runningTime, stats){
                $runningTime.html(stats.runningTime);
            }

            function setlogid($logid, stats){//Not me
$logid.html(stats.kronosIngestId);//Not me
            }

            function setTimeStarted($timeStarted, stats){
                $timeStarted.html(stats.timeStarted);
            }


            function SET_DETAIL_STATISTICS($exists, $SAVED, $ERRORED, STATS){//Not me
                $exists.html(formatter.format(STATS.found));
                $ERRORED.html(formatter.format(STATS.errored));
                $SAVED.html(formatter.format(STATS.saved));
            }

            function setProcessedStatistics($total, $PROCESSED, $PERCENT, STATS){//Not me
                var percent = 0;
                if(parseInt(STATS.processed) > 0 && parseInt(STATS.total) > 0){
                    percent = (parseInt(STATS.processed) / parseInt(STATS.total) * 100).toFixed(3);
                }
                $PERCENT.html(percent);
                $total.html(formatter.format(STATS.total));
                $PROCESSED.html(formatter.format(STATS.processed));
            }

            function setProgressBar($progressBar, stats){
                var percent = 4;
                if(parseInt(stats.processed) > 0 && parseInt(stats.total) > 0){
                    percent = (parseInt(stats.processed) / parseInt(stats.total) * 100).toFixed(3);
                }
                if(percent > 4){
                    $progressBar.css({
                        width:percent+"%"
                    });
                }
            }

            function showManuallyRun($runContainer){
                $runContainer.show();
            }

            function setIndicatorValues($indicator, $indicatorValue, stats){
                $indicator.removeClass(IDLE_CLASS).addClass(stats.status.toLowerCase());
                var status = stats.status.toUpperCase() ? stats.status.toUpperCase() : "Idle";
                $indicatorValue.html(status);
            }

            function resetIndicatorsResetstart(){
                $dailyIndicator.removeClass(STARTED_CLASS).removeClass(RUNNING_CLASS).addClass(IDLE_CLASS);
                $weeklyIndicator.removeClass(STARTED_CLASS).removeClass(RUNNING_CLASS).addClass(IDLE_CLASS);
                $runDailyContainer.hide();
                $runWeeklyContainer.hide();
            }


            function setTimer(){
                statsTimer = setInterval(function(){
                    runStatusCheck();
                }, 3000);
            }


            runStatusCheck();
            setTimer();
        });

    </script>


</body>
</html>