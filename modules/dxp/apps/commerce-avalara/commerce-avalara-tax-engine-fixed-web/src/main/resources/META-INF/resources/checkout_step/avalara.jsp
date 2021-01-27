<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = (String)request.getAttribute("redirect");
%>

<div>
	<c:set var="redirectLink">
		<a href="<%= redirect %>"><%= redirect %></a>
	</c:set>

	<liferay-ui:message arguments="${redirectLink}" key="the-tax-calculation-has-been-finished.-you-should-be-redirected-automatically.-if-the-page-does-not-reload-within-a-few-seconds-please-click-this-link-x" />
</div>

<script>
	window.location.href =
		'<%= redirect %>';
</script>