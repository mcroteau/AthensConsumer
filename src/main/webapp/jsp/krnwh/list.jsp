<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Punches</title>
</head>

<body>

<div class="span12">
    <h1>Punches</h1>
</div>


<c:if test="${not empty message}">
    <div class="span12">
        <div class="alert alert-info">
            ${message}
        </div>
    </div>
</c:if>


<c:choose>
    <c:when test="${krnwhs.size() > 0}">

        <a href="${pageContext.request.contextPath}/index" title="Run Job" class="btn btn-default">Run Todays</a>

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
                </tr>
                </thead>
                <tbody>
                <!--
		        "\nfpempn: " + this.getFpempn() +
				" fppunc: " + this.getFppunc() +
				" fptype: " + this.getFptype() +
				" fpclck: " + this.getFpclck() +
				" fpbadg: " + this.getFpbadg() +
				" fpfkey: " + this.getFpfkey() +
				" fppcod: " + this.getFppcod() +
				" fstatus: " + this.getFstatus() + "\n";
                -->

                <c:forEach var="krnwh" items="${krnwhs}">
                    <tr>
                        <td>${krnwh.id}</td>
                        <td>${krnwh.fpempn}</td>
                        <td>${krnwh.fppunc}</td>
                        <td>${krnwh.fpclck}</td>
                        <td>${krnwh.fpclck}</td>
                        <td>${krnwh.fpbadg}</td>
                        <td>${krnwh.fpfkey}</td>
                        <td>${krnwh.fstatus}</td>
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
</body>
</html>