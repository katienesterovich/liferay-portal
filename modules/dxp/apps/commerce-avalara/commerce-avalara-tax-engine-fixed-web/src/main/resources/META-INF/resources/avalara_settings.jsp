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

<portlet:actionURL name="/commerce_tax_methods/edit_commerce_tax_avalara" var="editCommerceAvalaraConnectorActionURL" />

<aui:form action="<%= editCommerceAvalaraConnectorActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="commerceTaxMethodId" type="hidden" value='<%= ParamUtil.getLong(request, "commerceTaxMethodId") %>' />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:error exception="<%= CommerceAvalaraConnectionException.class %>" message="the-connection-could-not-be-verified-because-the-provided-credentials-are-incorrect" />

	<commerce-ui:panel>
		<c:choose>
			<c:when test='<%= GetterUtil.getBoolean(request.getAttribute("connectionEstablished")) %>'>
				<%@ include file="/fields/additional_settings.jspf" %>
				<%@ include file="/fields/dispatch_trigger_setup.jspf" %>
			</c:when>
			<c:otherwise>
				<clay:alert
					displayType="warning"
					message="configure-credentials-before-continuing"
				/>
			</c:otherwise>
		</c:choose>
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>