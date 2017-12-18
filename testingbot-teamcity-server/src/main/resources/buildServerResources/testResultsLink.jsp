<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@include file="/include.jsp"%>

<c:if test="${not empty sessionId}">
    <div style="padding: 10;">
        <h3>TestingBot</h3>
        <a class="session-link" href="#" target="_self">View TestingBot report of this test</a>

        <script>
        $j(function () {
            $j('.session-link').each(function () {
                var currentUrl = window.location.href.replace(/([&|\?])tab=[^&]+/, '$1tab=testingbot-results&sessionId=${sessionId}');
                if (currentUrl.indexOf('testingbot-results') > -1) {
                    $j(this).attr('href', currentUrl);
                }
            });
        });
        </script>
    </div>
</c:if>
<c:if test="${not empty error}">
    <div style="padding: 10;">
        <h3>TestingBot</h3>
        <p>${error}</p>
    </div>
</c:if>