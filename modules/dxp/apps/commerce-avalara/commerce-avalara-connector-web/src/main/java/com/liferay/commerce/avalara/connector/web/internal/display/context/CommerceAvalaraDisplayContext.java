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

package com.liferay.commerce.avalara.connector.web.internal.display.context;

import com.liferay.commerce.avalara.connector.web.internal.constants.CommerceAvalaraConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Katie Nesterovich
 */
public class CommerceAvalaraDisplayContext {

	public CommerceAvalaraDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getNavigationItems() {
		List<NavigationItem> navigationItems = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String viewCredentialsURL = getNavigationItemURL(
			"view-credentials", CommerceAvalaraConstants.TYPE_CREDENTIALS);

		String toolbarItem = ParamUtil.getString(
			_renderRequest, "toolbarItem", "view-credentials");

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());

		NavigationItem credentialsNavigationItem = getNavigationItem(
			toolbarItem.equals("view-credentials"), viewCredentialsURL,
			LanguageUtil.get(resourceBundle, "credentials"));

		navigationItems.add(credentialsNavigationItem);

		return navigationItems;
	}

	public int getType() {
		return ParamUtil.getInteger(
			_renderRequest, "type", CommerceAvalaraConstants.TYPE_CREDENTIALS);
	}

	protected NavigationItem getNavigationItem(
		boolean active, String href, String label) {

		NavigationItem navigationItem = new NavigationItem();

		navigationItem.setActive(active);
		navigationItem.setHref(href);
		navigationItem.setLabel(label);

		return navigationItem;
	}

	protected String getNavigationItemURL(String toolbarItem, int type) {
		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter("mvcPath", "/view.jsp");
		portletURL.setParameter("toolbarItem", toolbarItem);
		portletURL.setParameter("type", String.valueOf(type));

		return portletURL.toString();
	}

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}