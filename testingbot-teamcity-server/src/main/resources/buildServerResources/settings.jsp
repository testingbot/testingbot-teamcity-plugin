<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<%@ page import="com.testingbot.teamcity.plugin.Constants" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="<%= Constants.TB_KEY %>.container">
    <th><label for="<%= Constants.TB_KEY %>">TestingBot Key:</label></th>
    <td>
        <props:textProperty name="<%= Constants.TB_KEY %>"/>

        <span class="smallNote">
            Set your TestingBot Key. You can get find this key in the <a href="https://testingbot.com/members" target="_blank">TestingBot member area</a>.
        </span>
        <span class="error" id="error_<%= Constants.TB_KEY %>"/>
    </td>
</tr>
<tr id="<%= Constants.TB_SECRET %>.container">
    <th><label for="<%= Constants.TB_SECRET %>">TestingBot Secret:</label></th>
    <td>
        <props:passwordProperty name="<%= Constants.TB_SECRET %>"/>

        <span class="smallNote">
            Set your TestingBot Secret. You can get find this key in the <a href="https://testingbot.com/members" target="_blank">TestingBot member area</a>.
        </span>
        <span class="error" id="error_<%= Constants.TB_SECRET %>"/>
    </td>
</tr>
<tr id="<%= Constants.TB_TUNNEL %>.container">
    <th><label for="<%= Constants.TB_TUNNEL %>">Enable TestingBot Tunnel:</label></th>
    <td>
        <props:checkboxProperty name="<%= Constants.TB_TUNNEL %>"
            treatFalseValuesCorrectly="${true}"
            uncheckedValue="false"/>

        <span class="smallNote">
            With TestingBot tunnel you can run tests against websites that are not available on the public internet.
        </span>
        <span class="error" id="error_<%= Constants.TB_TUNNEL %>"/>
    </td>
</tr>