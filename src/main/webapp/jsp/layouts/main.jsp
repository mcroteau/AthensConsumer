<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
<title><decorator:title /></title>

	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-reboot.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" />

	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/jquery/jquery.min.js"></script>


    <style type="text/css">
        body{
            padding:0px
            text-align:center;
			font-family:arial;
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
            position:fixed;
            top:0px;
        }
        #container{
            width:912px;
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
        .clear{
            clear:both;
        }
        .loading{
            float:right;
            height:13px;
        }
        #jobs-status-container{
            font-size:10px;
            text-align:right;
        }
        .job-status-container{
            margin-right:24px;
            display:inline-block;
        }
        .job-status-title{
            font-weight:bold;
        }
        .job-status-value{
            opacity:0.63;
            text-transform:uppercase;
        }
        h1{
            font-size:48px;
            font-weight:bold;
            padding-left: 21px;
            border-left:solid 6px #D4212F;
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
                <span class="job-status-value" id="daily-status-value">Running</span>
            </span>
            <span class="job-status-container">
                <span class="job-status-title">WEEKLY:&nbsp;</span>
                <span class="job-status-value" id="weekly-status-value">Idle</span>
            </span>
            <img src="${pageContext.request.contextPath}/images/loading.gif" class="loading pull-right" />
        </div>
        <br class="clear"/>
    </div/>

    <img src="${pageContext.request.contextPath}/images/athens-logo.png"/>


    <style type="text/css">
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

		#navigation li a:hover{
			background:#000;
		}
		#navigation li a{

		}
    </style>

		<div id="navigation">
			<ul>
				<li><a href="javascript:">Ingests</a></li>
				<li><a href="javascript:">Search</a></li>
				<li><a href="javascript:">Jobs</a></li>
			</ul>
		</div>

        <br class="clear"/>

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