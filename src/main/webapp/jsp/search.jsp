<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Athens : Work Hour Search</title>
    <style type="text/css">
        .search-criteria-container{
            float:right;
            width:400px;
            border:solid 1px #3e3e3e;
            text-align:right;
            margin-top:21px;
        }
        .search-button{
            margin-top:13px;
        }
    </style>
</head>
<body>

    <h2 class="float-left">Search Work Hour</h2>

    <div class="search-criteria-container">

        <div class="search-criteria">
            <form action="${pageContext.request.contextPath}/kronosWorkHour/search" method="post">

                <div class="input-group input-daterange">
                    <input name="start-date" type="text" class="form-control" value="${startDate}" id="start-date">
                    <div class="input-group-addon">to</div>
                    <input name="end-date" type="text" class="form-control" value="${endDate}" id="end-date">

                    <input name="startDate" type="text" class="form-control" value="" id="startDate">
                    <input name="endDate" type="text" class="form-control" value="" id="endDate">
                </div>

                <div class="form-group">
                    <input type="submit" value="Search" class="search-button btn btn-primary">
                </div>
            </form>
        </div>

        <div class="export-container">
            <c:choose>
                <c:when test="${kronosWorkHours.size() == 0}">
                    <form action="${pageContext.request.contextPath}/kronosWorkHour/export" method="post">

                        <input name="startDate" type="hidden" class="form-control" value="${startDate}">
                        <input name="endDate" type="hidden" class="form-control" value="${endDate}">

                        <div class="form-group">
                            <input type="submit" value="Export Data" class="btn btn-primary">
                        </div>
                    </form>
                </c:when>
            </c:choose>
        </div>
    </div>

    <br class="clear"/>

    <c:choose>
        <c:when test="${kronosWorkHours.size() > 0}">

            <div class="span12">

                <table class="table table-condensed">
                    <thead>
                        <tr>
                            <th>Id</th>
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
                    <c:forEach var="kronosWorkHour" items="${kronosWorkHours}">
                        <tr>
                            <td>${kronosWorkHour.id}</td>
                            <td>${kronosWorkHour.fpempn}</td>
                            <td>${kronosWorkHour.fppunc}</td>
                            <td>${kronosWorkHour.fptype}</td>
                            <td>${kronosWorkHour.fpclck}</td>
                            <td>${kronosWorkHour.fpbadg}</td>
                            <td>${kronosWorkHour.fpfkey}</td>
                            <td>${kronosWorkHour.fstatus}</td>
                            <td>${kronosWorkHour.krnlogid}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

            </div>

        </c:when>
        <c:when test="${kronosWorkHours.size() == 0}">
            <p>No work hours submitted for this date range.</p>
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

        var $startDateValue = $("#startDate"),
            $endDatValue = $("#endDate");

        $startDate.datepicker(getFormat());
        $endDate.datepicker(getFormat());

        $startDate.on("changeDate", dateChangedEvent($startDateValue));
        $endDate.on("changeDate", dateChangedEvent($endDatValue));

        function dateChangedEvent($input){
            return function(data){
                console.log("date changed", data);
                console.log(data.date.getDate());
                var date = data.date.getFullYear().toString() + (data.date.getMonth() +1 ).toString().pad("0", 2) + data.date.getDate().toString().pad("0", 2) + "000000";
                console.log(date);
                $input.val(date);
            }
        }

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
               autoclose:true
           }
        }

	});
	</script>
</body>
</html>