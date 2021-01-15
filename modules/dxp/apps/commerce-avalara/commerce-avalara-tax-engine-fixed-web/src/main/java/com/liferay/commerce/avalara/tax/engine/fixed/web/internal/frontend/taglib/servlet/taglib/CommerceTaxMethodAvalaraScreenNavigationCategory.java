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

package com.liferay.commerce.avalara.tax.engine.fixed.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.avalara.connector.CommerceAvalaraConnector;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorChannelConfiguration;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.constants.CommerceTaxScreenNavigationConstants;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Calvin Keum
 */
@Component(
	enabled = false,
	property = {
		"screen.navigation.category.order:Integer=20",
		"screen.navigation.entry.order:Integer=10"
	},
	service = {ScreenNavigationCategory.class, ScreenNavigationEntry.class}
)
public class CommerceTaxMethodAvalaraScreenNavigationCategory
	implements ScreenNavigationCategory,
			   ScreenNavigationEntry<CommerceTaxMethod> {

	public static final String CATEGORY_KEY = "settings";

	public static final String ENTRY_KEY = "settings";

	@Override
	public String getCategoryKey() {
		return CATEGORY_KEY;
	}

	@Override
	public String getEntryKey() {
		return ENTRY_KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(resourceBundle, ENTRY_KEY);
	}

	@Override
	public String getScreenNavigationKey() {
		return CommerceTaxScreenNavigationConstants.
			SCREEN_NAVIGATION_KEY_COMMERCE_TAX_METHOD;
	}

	@Override
	public boolean isVisible(User user, CommerceTaxMethod commerceTaxMethod) {
		if (commerceTaxMethod == null) {
			return false;
		}

		String engineKey = commerceTaxMethod.getEngineKey();

		if (engineKey.equals(CommerceAvalaraConstants.KEY)) {
			return true;
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			long commerceTaxMethodId = ParamUtil.getLong(
				httpServletRequest, "commerceTaxMethodId");

			CommerceTaxMethod commerceTaxMethod =
				_commerceTaxMethodService.getCommerceTaxMethod(
					commerceTaxMethodId);

			CommerceAvalaraConnectorChannelConfiguration
				commerceAvalaraConnectorChannelConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAvalaraConnectorChannelConfiguration.class,
						new ParameterMapSettingsLocator(
							httpServletRequest.getParameterMap(),
							new GroupServiceSettingsLocator(
								commerceTaxMethod.getGroupId(),
								CommerceAvalaraConnectorChannelConfiguration.
									class.getName())));

			httpServletRequest.setAttribute(
				CommerceAvalaraConnectorChannelConfiguration.class.getName(),
				commerceAvalaraConnectorChannelConfiguration);

			if (_verifyConnection(
					httpServletRequest, commerceTaxMethod.getCompanyId())) {

				httpServletRequest.setAttribute(
					"connectionEstablished", Boolean.TRUE);

				_setCompanies(httpServletRequest);
			}
			else {
				httpServletRequest.setAttribute(
					"connectionEstablished", Boolean.FALSE);
			}
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/avalara_settings.jsp");
	}

	private void _setCompanies(HttpServletRequest httpServletRequest) {
		Map<String, String> companies = new HashMap<>();

		try {
			companies = _commerceAvalaraConnector.queryCompanies();
		}
		catch (Exception exception) {
		}

		httpServletRequest.setAttribute("avalaraCompanies", companies);
	}

	private boolean _verifyConnection(
		HttpServletRequest httpServletRequest, long companyId) {

		try {
			CommerceAvalaraConnectorConfiguration
				commerceAvalaraConnectorConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAvalaraConnectorConfiguration.class,
						new ParameterMapSettingsLocator(
							httpServletRequest.getParameterMap(),
							new CompanyServiceSettingsLocator(
								companyId,
								CommerceAvalaraConnectorConfiguration.class.
									getName())));

			_commerceAvalaraConnector.verifyConnection(
				commerceAvalaraConnectorConfiguration.accountNumber(),
				commerceAvalaraConnectorConfiguration.licenseKey(),
				commerceAvalaraConnectorConfiguration.serviceURL());

			return true;
		}
		catch (Exception exception) {
			return false;
		}
	}

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.avalara.tax.engine.fixed.web)"
	)
	private ServletContext _servletContext;

}