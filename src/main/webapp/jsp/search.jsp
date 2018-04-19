<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Athens : Work Hour Search</title>

    <style type="text/css">
        .search-criteria-container{
            float:right;
            text-align:right;
            margin:9px 0px 13px 0px;
            padding:13px 13px 3px 18px;
            border:solid 1px #ddd;
            background:#efefef;
        }
        #date-range-container{
            float:right;
            width:284px;
        }
        .search-button{
            margin-top:17px;
        }
        .export-container{
            line-height:1.0;
            margin:20px auto 0px auto;
            border:solid 0px #ddd;
        }
        .search-header-left{
            margin-bottom:13px;
        }
        #employee-filter-outer-container{
            float:left;
            padding:0px;
        }
        #employee-filter-container{
            position:relative;
        }
        #employee-filter-title{
            font-size:13px;
            margin-right:13px;
            display:inline-block;
        }
        #employee-filter{
            width:83px;
            margin-right:3px;
            display:inline-block;
        }
        h2{
            padding-bottom:0px;
        }
        .small-font{
            opacity:0.64;
            font-size:13px;
        }
    </style>
</head>
<body>

    <div class="search-header-left float-left">
        <h2 class="">Search Hours</h2>
        <c:choose>
            <c:when test="${kronosWorkHours.size() > 0}">
                <div class="export-container">
                    <form action="${pageContext.request.contextPath}/export" method="post">

                        <input name="startDate" type="hidden" class="form-control" value="${startDate}"/>
                        <input name="endDate" type="hidden" class="form-control" value="${endDate}"/>
                        <input name="employeeId" type="hidden" id="employeeId" value="${employeeId}"/>

                        <input type="submit" value="Export Data" class="btn btn-default float-left">
                        <p class="float-left" style="margin:10px 0px 0px 13px"><span class="total-title">Total:&nbsp</span><span class="total-value">${total}</span></p>

                        <br class="clear"/>
                    </form>
                </div>
            </c:when>
        </c:choose>
    </div>

    <div class="search-criteria-container">

        <div class="search-criteria">
            <form action="${pageContext.request.contextPath}/search" method="post">

                <div class="input-group input-daterange" id="date-range-container">
                    <input name="startDateDisplay" type="text" class="form-control" value="${startDateDisplay}" id="startDateDisplay">
                    <div class="input-group-addon">to</div>
                    <input name="endDateDisplay" type="text" class="form-control" value="${endDateDisplay}" id="endDateDisplay">

                    <input name="startDate" type="hidden" class="form-control" value="${startDate}" id="startDate">
                    <input name="endDate" type="hidden" class="form-control" value="${endDate}" id="endDate">
                </div>

                <div id="employee-filter-outer-container">
                    <div id="employee-filter-container">
                        <span id="employee-filter-title">Employee Id</span>
                        <input name="employeeId" placeholder="91732" type="text" id="employee-filter" class="form-control" value="${employeeId}" style="text-align:right"/>
                        <br class="clear"/>
                    </div>
                </div>
                <br class="clear"/>

                <p class="float-left small-font" style="margin-top:20px;">Employee Id is additional filter, allowed to be blank</p>
                <input type="submit" value="Search" class="search-button float-right btn-success btn">
                <br class="clear"/>
            </form>
        </div>
    </div>

    <br class="clear"/>

    <c:choose>
        <c:when test="${kronosWorkHours.size() > 0}">

            <div class="search-results-container">

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
                            <th>Status</th>
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
                            <td>${kronosWorkHour.fppcod}</td>
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

            var $startDate = $("#startDateDisplay"),
                $endDate = $("#endDateDisplay");

            var $startDateValue = $("#startDate"),
                $endDatValue = $("#endDate");

            $startDate.datepicker(getConfiguration());
            $endDate.datepicker(getConfiguration());

            $startDate.on("changeDate", dateChangedEvent($startDateValue));
            $endDate.on("changeDate", dateChangedEvent($endDatValue));

            function dateChangedEvent($input){
                return function(data){
                    var date = data.date.getFullYear().toString() + (data.date.getMonth() +1 ).toString().pad("0", 2) + data.date.getDate().toString().pad("0", 2) + "000000";
                    $input.val(date);
                }
            }

            function getConfiguration(){
                return {
                   autoclose:true
               }
            }

        });

	</script>
</body>
</html>