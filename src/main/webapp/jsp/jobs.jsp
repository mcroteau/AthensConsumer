<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<title>Athens: Running Jobs</title>

</head>
<body>

<h1>Running Jobs <!--<img src="${pageContext.request.contextPath}/images/loading.gif" class="float-right" style="margin-top:17px;" />--></h1>

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
        .indicator{
            height:10px;
            width:10px;
        }
        .indicator-running{
            background:#04be1f;
        }
        .indicator-idle{
            background:#bcbcbd;
        }
        .stats-header{
            margin:18px auto;
            text-align:center;
            text-transform:uppercase;
        }
        .total-processed{
            font-weight:bold;
            font-size:79px;
            line-height:1.0;
            text-align:right;
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
            font-weight:bold;
        }
        .processed-progress-container{
            height:6px;
            margin-top:16px;
            position:relative;
        }
        .progress-bar{
            display:block;
            position:absolute;
        }
        .base-progress-bar{
            height:6px;
            width:100%;
            background:#bcbcbd;
        }
        .percent-progress-bar{
            height:6px;
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
        .ten-text{
            font-size:11px;
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
            <div class="top-left float-left">
                <span class="indicator indicator-idle inline running" id="daily-indicator"></span>
                <span class="status ten-text inline">Idle</span>
            </div>
            <div class="stats-top-right float-right">
                <span class="time-title ten-text inline bold">Started:&nbsp;</span>
                <span class="time-value ten-text inline bold" id="daily-time">0</span>
            </div>
            <br class="clear"/>
        </div>
        <div class="header">
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
            <span class="percent-progress-bar progress-bar"></span>
        </div>

        <div class="stats-details-outer-container">
            <div class="stats-details-container float-left">
                <span class="stats-details-id-title">Id:&nbsp;#</span>
                <span class="stats-details-id-value" id="daily-log-id"></span>
            </div>
            <div class="stats-details-container float-right">
                <div class="stats-details">
                    <span class="stats-details-title float-left">Overlap:&nbsp;</span>
                    <span class="stats-details-value float-right" id="daily-exists">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Saved:&nbsp;</span>
                    <span class="stats-details-value float-right" id="daily-saved">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Errored:&nbsp;</span>
                    <span class="stats-details-value float-right" id="daily-errored">0</span>
                    <br class="clear"/>
                </div>
            </div>
            <br class="clear"/>
        </div>

    </div>



    <div class="stats-container float-right">
        <div id="stats-top">
            <div class="top-left float-left">
                <span class="indicator indicator-idle inline" id="weekly-indicator"></span>
                <span class="status ten-text inline">Running</span>
            </div>
            <div class="stats-top-right float-right">
                <span class="time-title ten-text inline bold">Started:&nbsp;</span>
                <span class="time-value ten-text inline bold" id="weekly-time">9:01am</span>
            </div>
            <br class="clear"/>
        </div>
        <div class="header">
            <h3>Weekly</h3>
        </div>
        <div class="total-percent">
            <div class="percent-container float-right">
                <span class="stats-percent inline" id="weekly-percent">73</span>
                <span class="percent-sign inline">%</span>
            </div>
            <div class="total-container float-right">
                <span class="total-title inline">Total:&nbsp;</span>
                <span class="stats-total inline" id="weekly-total">13,782</span>
            </div>
            <br class="clear"/>
        </div>

        <div class="total-processed" id="weekly-processed">4,737</div>

        <div class="processed-title">Processed</div>

        <div class="processed-progress-container">
            <span class="base-progress-bar progress-bar"></span>
            <span class="percent-progress-bar progress-bar"></span>
        </div>

        <div class="stats-details-outer-container">
            <div class="stats-details-container float-left">
                <span class="stats-details-id-title">Id:&nbsp;#</span>
                <span class="stats-details-id-value" id="weekly-log-id"></span>
            </div>
            <div class="stats-details-container float-right">
                <div class="stats-details">
                    <span class="stats-details-title float-left">Overlap:&nbsp;</span>
                    <span class="stats-details-value float-right" id="weekly-exists">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Saved:&nbsp;</span>
                    <span class="stats-details-value float-right" id="weekly-saved">0</span>
                    <br class="clear"/>
                </div>
                <div class="stats-details">
                    <span class="stats-details-title float-left">Errored:&nbsp;</span>
                    <span class="stats-details-value float-right" id="weekly-errored">0</span>
                    <br class="clear"/>
                </div>
            </div>
            <br class="clear"/>
        </div>

    </div>


    <br class="clear"/>


    <script type="text/javascript">

        $(document).ready(function(){
            var indicatorGreen = "#04be1f";

            var $dailyIndicator = $("#daily-indicator"),
                $weeklyIndicator = $("#weekly-indicator");

            var $dailyTotal = $("#daily-total"),
                $weeklyTotal = $("#weekly-total");

            var $dailyProcessed = $("#daily-processed"),
                $weeklyProcessed = $("#weekly-processed");

            var $dailyPercent = $("#daily-percent"),
                $weeklyPercent = $("#weekly-percent");

            var $dailyLogId = $("#daily-log-id"),
                $weeklyLogId = $("#weekly-log-id");

            var $dailyExists = $("#daily-exists"),
                $dailySaved = $("#daily-saved"),
                $dailyErrored = $("#daily-errored"),
                $weeklyExists = $("#weekly-exists"),
                $weeklySaved = $("#weekly-saved"),
                $weeklyErrored = $("#weekly-errored");






            function runStatusCheck(){
                console.log("about to run job check");
                $.ajax({
                    url : "${pageContext.request.contextPath}/status",
                    dataType :'json',
                    success : renderStatistics,
                    error : function(){
                        console.log("error");
                    }
                });
            }

            function setTimer(){
                timer = setInterval(function(){
                    runStatusCheck();
                }, 3000);
            }


            function renderStatistics(json, c){
                console.log("h", json);
                resetIndicators();
                if(json.dailyJobRunning){
                    setIndicator($dailyIndicator, "indicator-idle", "indicator-running");
                    setProcessedStatistics($dailyTotal, $dailyProcessed, $dailyPercent, json.dailyJobRunning);
                    SET_DETAIL_STATISTICS($dailyExists, $dailySaved, $dailyErrored, json.dailyJobRunning);
                    setlogid($dailyLogId, json.dailyJobRunning);
                }
                if(json.weeklyJobRunning){
                    setIndicator($weeklyIndicator, "indicator-idle", "indicator-running");
                    setProcessedStatistics($weeklyTotal, $weeklyProcessed, $weeklyPercent, json.weeklyJobRunning);
                    SET_DETAIL_STATISTICS($weeklyExists, $weeklySaved, $weeklyErrored, json.weeklyJobRunning);
                    setlogid($weeklyLogId, json.weeklyJobRunning);
                }
            }

            function setlogid($logid, stats){
$logid.html(stats.kronosIngestId);
            }

            function SET_DETAIL_STATISTICS($exists, $SAVED, $ERRORED, STATS){
                $exists.html(STATS.exists);
                $ERRORED.html(STATS.errored);
                $SAVED.html(STATS.saved);
            }

            function setProcessedStatistics($total, $PROCESSED, $PERCENT, STATS){
                var percent = (parseInt(STATS.processed) / parseInt(STATS.total)).toFixed(3);
                $PERCENT.html(percent);
                $total.html(STATS.total);
                $PROCESSED.html(STATS.processed);
            }


            function resetIndicators(){
                setIndicator($dailyIndicator, "indicator-running", "indicator-idle");
                setIndicator($dailyIndicator, "indicator-running", "indicator-idle");
            }

            function setIndicator($indicator, removeClass, addCass){
                $indicator.removeClass(removeClass).addClass(addCass)
            }

            runStatusCheck();
            setTimer();
        });

    </script>

</body>
</html>