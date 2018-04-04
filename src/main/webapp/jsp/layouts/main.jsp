<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
    <title>Athens Services Punch Report</title>


	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-reboot.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" />


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
    </style>
</head>

<body>

    <div id="header-background"></div>

    <div id="container">

        <img src="${pageContext.request.contextPath}/images/athens-logo.png"/>


        <c:if test="${not empty message}">
            <div class="span12">
                <div class="alert alert-info">
                    ${message}Test
                </div>
            </div>
        </c:if>

        <decorator:body />

    </div>
</body>
</html>