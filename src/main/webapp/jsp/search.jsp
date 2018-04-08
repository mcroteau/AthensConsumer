<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Athens Services Punch Report</title>


	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-reboot.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" />

	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/jquery/jquery.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" />

	<script type="text/javascript" src="${pageContext.request.contextPath}/bootstrap/js/bootstrap-datepicker.min.js"></script>

	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-datepicker.min.css" />

	<style type="text/css">
        body{
            padding:0px
            text-align:center;
        }
        #header-background{
            height:391px;
            width:100%;
            z-index:0;
            background:#D4212F;
            position:fixed;
            top:0px;
            <c:choose>
                <c:when test="${krnwhs == null || krnwhs.size() == 0}">
                    height:100%;
                </c:when>
            </c:choose>
        }
        #container{
            width:840px;
            padding:24px;
            margin:30px auto 200px auto;
            text-align:left;
            background:#fff;
            z-index:1;
            position:relative;
            box-shadow: 0px 0px 13px 0px rgba(0,0,0,0.38);
            -moz-box-shadow: 0px 0px 13px 0px rgba(0,0,0,0.38);
            -webkit-box-shadow: 0px 0px 13px 0px rgba(0,0,0,0.38);
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
    </style>

</head>
<body>


<div id="header-background"></div>

<div id="container">

    <img src="${pageContext.request.contextPath}/images/athens-logo.png"/>

    <div class="span12">
        <h1>Daily Punches ${total}</h1>
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
            <input name="start-date" type="text" class="form-control" value="${startDate}" id="start-date">
            <div class="input-group-addon">to</div>
            <input name="end-date" type="text" class="form-control" value="${endDate}" id="end-date">
        </div>

		<div class="form-group">
			<input type="submit" value="Get Data" class="btn btn-primary">
		</div>
    </form>


    <c:choose>
        <c:when test="${krnwhs.size() > 0}">
            <form action="${pageContext.request.contextPath}/krnwh/export" method="post">

                <input name="start-date" type="hidden" class="form-control" value="${startDate}">
                <input name="end-date" type="hidden" class="form-control" value="${endDate}">

                <div class="form-group">
                    <input type="submit" value="Export Data" class="btn btn-primary">
                </div>
            </form>
        </c:when>
    </c:choose>

    <c:choose>
        <c:when test="${krnwhs.size() > 0}">



            <a href="${pageContext.request.contextPath}/list" title="Run Job" class="btn btn-default">Daily Report Logs</a>

            <div class="span12">

                <table class="table table-condensed">
                    <thead>
                        <tr>
                            <th>id</th>
                            <th>Employee Id</th>
                            <th>Punch</th>
                            <th>Type</th>
                            <th>Clock</th>
                            <th>Badge Id</th>
                            <th>Key</th>
                            <th>COD</th>
                            <th>Ingest #</th>
                        </tr>
                    </thead>
                    <tbody>
                    <!--
                    "\nid: " + this.getId() +
                    " fpempn: " + this.getFpempn() +
                    " fppunc: " + this.getFppunc() +
                    " fptype: " + this.getFptype() +
                    " fpclck: " + this.getFpclck() +
                    " fpbadg: " + this.getFpbadg() +
                    " fpfkey: " + this.getFpfkey() +
                    " fppcod: " + this.getFppcod() +
                    " fstatus: " + this.getFstatus() +
                    " krnlogid: " + this.getKrnlogid() + "\n";
                    -->

                    <c:forEach var="krnwh" items="${krnwhs}">
                        <tr>
                            <td>${krnwh.id}</td>
                            <td>${krnwh.fpempn}</td>
                            <td>${krnwh.fppunc}</td>
                            <td>${krnwh.fptype}</td>
                            <td>${krnwh.fpclck}</td>
                            <td>${krnwh.fpbadg}</td>
                            <td>${krnwh.fpfkey}</td>
                            <td>${krnwh.fstatus}</td>
                            <td>${krnwh.krnlogid}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

            </div>

        </c:when>
        <c:when test="${krnwhs.size() == 0}">
            <p>No punches created yet.</p>
        </c:when>
    </c:choose>
</div>

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

        $endDate.datepicker(getFormat());
        $startDate.datepicker(getFormat());

        if($startDate.val() == ""){
            $startDate.datepicker("update", getYesterday());
        }

        if($endDate.val() == ""){
            $endDate.datepicker("update", new Date());
        }



        function getYesterday(){
            var d = new Date();
            d.setDate(d.getDate() - 2);
            return d;
        }

        function getFormat(){
            return {
               autoclose:true,
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