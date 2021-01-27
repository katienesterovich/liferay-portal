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

package com.liferay.commerce.avalara.tax.engine.fixed.web.internal.util;

import com.liferay.commerce.avalara.connector.helper.CommerceAvalaraConnectorHelper;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepServicesTracker;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	enabled = false, immediate = true,
	property = {
		"commerce.checkout.step.name=" + CommerceAvalaraTaxEngineCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=" + (Integer.MAX_VALUE - 160)
	},
	service = CommerceCheckoutStep.class
)
public class CommerceAvalaraTaxEngineCheckoutStep
	extends BaseCommerceCheckoutStep {

	public static final String NAME = "avalara";

	public LiferayPortletResponse getLiferayPortletResponse(
		HttpServletRequest httpServletRequest) {

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		return _portal.getLiferayPortletResponse(portletResponse);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isActive(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (commerceOrder == null) {
			return false;
		}

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodLocalService.fetchCommerceTaxMethod(
				commerceOrder.getGroupId(), "avalara");

		if ((commerceTaxMethod != null) && commerceTaxMethod.isActive()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isOrder() {
		return true;
	}

	@Override
	public boolean isVisible(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		return false;
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceAvalaraConnectorHelper.updateSalesOrderTaxInfo(
				(CommerceOrder)httpServletRequest.getAttribute(
					CommerceCheckoutWebKeys.COMMERCE_ORDER));

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		// Redirection only works with the original servlet response

		HttpServletResponse originalHttpServletResponse = httpServletResponse;

		while (originalHttpServletResponse instanceof
					HttpServletResponseWrapper) {

			HttpServletResponseWrapper httpServletResponseWrapper =
				(HttpServletResponseWrapper)originalHttpServletResponse;

			originalHttpServletResponse =
				(HttpServletResponse)httpServletResponseWrapper.getResponse();
		}

		String paymentServletURL =
			originalHttpServletResponse.encodeRedirectURL(
				_getOrderSummaryCheckoutStepUrl(
					httpServletRequest, commerceOrder));

		String redirect = _portal.escapeRedirect(paymentServletURL);

		if (Validator.isNotNull(redirect) &&
			!originalHttpServletResponse.isCommitted()) {

			originalHttpServletResponse.sendRedirect(redirect);
		}

		if (originalHttpServletResponse.isCommitted()) {
			return;
		}

		// Backend redirection has failed: fall back to frontend redirect

		httpServletRequest.setAttribute("redirect", redirect);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/checkout_step/avalara.jsp");
	}

	@Override
	public boolean showControls(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return false;
	}

	private String _getOrderSummaryCheckoutStepUrl(
		HttpServletRequest httpServletRequest, CommerceOrder commerceOrder) {

		LiferayPortletResponse liferayPortletResponse =
			getLiferayPortletResponse(httpServletRequest);

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		portletURL.setParameter(
			"commerceOrderUuid", String.valueOf(commerceOrder.getUuid()));

		CommerceCheckoutStep commerceCheckoutStep =
			_commerceCheckoutStepServicesTracker.getCommerceCheckoutStep(
				"order-summary");

		portletURL.setParameter(
			"checkoutStepName", commerceCheckoutStep.getName());

		return portletURL.toString();
	}

	@Reference
	private CommerceAvalaraConnectorHelper _commerceAvalaraConnectorHelper;

	@Reference
	private CommerceCheckoutStepServicesTracker
		_commerceCheckoutStepServicesTracker;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

}