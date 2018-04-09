<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Athens : Punch Search</title>
</head>
<body>

    <h2>Search Punches</h2>

	<form action="${pageContext.request.contextPath}/kronosWorkHour/search" method="post">

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
        <c:when test="${kronosWorkHours.size() > 0}">
            <form action="${pageContext.request.contextPath}/kronosWorkHour/export" method="post">

                <input name="start-date" type="hidden" class="form-control" value="${startDate}">
                <input name="end-date" type="hidden" class="form-control" value="${endDate}">

                <div class="form-group">
                    <input type="submit" value="Export Data" class="btn btn-primary">
                </div>
            </form>
        </c:when>
    </c:choose>

    <c:choose>
        <c:when test="${kronosWorkHours.size() > 0}">

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
            <p>No punches created yet.</p>
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