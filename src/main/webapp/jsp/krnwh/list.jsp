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

        <a href="${pageContext.request.contextPath}/list" title="Run Job" class="btn btn-default">Daily Ingests</a>

        <div class="btn-toolbar">
            <div class="btn-group">

                <%  int total = Integer.parseInt(request.getAttribute("total").toString());
                    int resultsPerPage = Integer.parseInt(request.getAttribute("resultsPerPage").toString());
                    int activePage = Integer.parseInt(request.getAttribute("activePage").toString());

                    int currentPage = 1;
                    int numberPages = 0;
                    int nextPage = 2;
                    int nextPageOffset = 10;
                    int previousPage = 1;
                    int previousPageOffset = 0;

                    for(int m = 0; m < total; m++){
                        if(m % resultsPerPage == 0){%>

                            <%if(m - 10 >= 0){%>
                                <%if(currentPage > 1){%>
                                    <%previousPage = currentPage -1;%>
                                <%}%>
                                <%previousPageOffset = m - 10;%>
                            <%}%>
                            <%if(currentPage - activePage  <= 3 && activePage - currentPage <= 3){%>
                                <%if(numberPages == 0){%>
                                    <a href="${pageContext.request.contextPath}/krnwh/list?offset=<%=previousPageOffset%>&max=<%=resultsPerPage%>&page=<%=previousPage%>" class="btn"><<</a>
                                    <%numberPages++;%>
                                <%}%>
                                <%if(activePage == currentPage){%>
                                    <a href="${pageContext.request.contextPath}/krnwh/list?offset=<%=m%>&max=<%=resultsPerPage%>&page=<%=currentPage%>" class="btn active"><%=currentPage%></a>
                                <%}else{%>
                                    <a href="${pageContext.request.contextPath}/krnwh/list?offset=<%=m%>&max=<%=resultsPerPage%>&page=<%=currentPage%>" class="btn"><%=currentPage%></a>
                                <%}%>
                                <%nextPage = currentPage + 1;%>
                                <%nextPageOffset = m + 10;%>
                            <%}%>
                        <%
                            currentPage++;
                        }%>
                        <%if(total == m + 1){%>
                            <a href="${pageContext.request.contextPath}/krnwh/list?offset=<%=nextPageOffset%>&max=<%=resultsPerPage%>&page=<%=nextPage%>" class="btn">>></a>
                        <%}%>
                <%}%>

            </div>
        </div>
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
</body>
</html>