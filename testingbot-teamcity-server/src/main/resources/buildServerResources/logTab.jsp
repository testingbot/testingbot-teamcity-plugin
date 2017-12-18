<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@include file="/include.jsp"%>

<c:if test="${not empty tests}">
    <jsp:useBean id="tests" type="java.util.ArrayList<com.testingbot.models.TestingbotTest>" scope="request"/>
    <style type="text/css">
       .clearfix:after {
            content: ".";
            display: block;
            height: 0;
            clear: both;
            visibility: hidden;
        }
    </style>
    <div class="clearfix">
        <img src="https://testingbot.com/assets/logo-head.png" style="float:left" />
        <h2 style="float: left; margin-top: 10px; margin-left: 10px">Test Results</h2>
    </div>
    <br/>
    <table class="testList">
        <thead>
            <tr>
                <th class="test-status" style="text-align: left">Status</th>
                <th style="text-align: left; vertical-align: top">Test name</th>
                <th style="text-align: left; vertical-align: top">Test Environment</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${tests}" var="test">
                <tr>
                    <td class="test-status" style="text-align: left; vertical-align: top">
                        <c:choose>
                            <c:when test="${test.isSuccess()}">
                                <span class="icon icon16 buildDataIcon build-status-icon build-status-icon_successful"></span> Passed
                            </c:when>    
                            <c:otherwise>
                                <span class="icon icon16 buildDataIcon build-status-icon build-status-icon_failed"></span> Failed
                            </c:otherwise>
                        </c:choose>
                        
                    </td>
                    <td class="nameT" style="text-align: left; vertical-align: top">
                        <a href="<%= request.getAttribute("javax.servlet.forward.request_uri") %>?<%= request.getQueryString() %>&sessionId=${test.getSessionId()}">
                            ${test.getName()}
                        </a>
                    </td>
                    <td class="nameT" style="text-align: left; vertical-align: top">
                        ${test.getBrowser()} - ${test.getOs()}
                    </td>
                </tr>
            </c:forEach>
        </tbody>
     </table>
</c:if>
<c:if test="${not empty testUrl}">
    <style type="text/css">
    #automate-result {
        width: 100%;
        min-height: 1400px;
        border: none;
    }
    </style>

    <iframe id="automate-result" src="${testUrl}" frameborder="0"></iframe>
</c:if>
<c:if test="${not empty error}">
    <h3>${error}</h3>
</c:if>