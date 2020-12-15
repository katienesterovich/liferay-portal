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

package com.liferay.commerce.avalara.tax.engine.fixed.web.internal.display.context;

import com.liferay.commerce.avalara.tax.engine.fixed.web.internal.frontend.constants.CommerceTaxRateSettingDataSetConstants;
import com.liferay.commerce.avalara.tax.engine.fixed.web.internal.servlet.taglib.ui.CommerceTaxMethodAvalaraRateRelsScreenNavigationEntry;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceTaxAvalaraRateRelsDisplayContext
	extends BaseCommerceTaxFixedRateDisplayContext {

	public CommerceTaxAvalaraRateRelsDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceTaxMethodService commerceTaxMethodService,
		CPTaxCategoryService cpTaxCategoryService,
		PercentageFormatter percentageFormatter, RenderRequest renderRequest) {

		super(
			commerceChannelLocalService, commerceChannelModelResourcePermission,
			commerceCurrencyLocalService, commerceTaxMethodService,
			cpTaxCategoryService, percentageFormatter, renderRequest);
	}

	public String getAddTaxRateSettingURL() throws Exception {
		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			commerceTaxFixedRateRequestHelper.getRequest(),
			CommercePortletKeys.COMMERCE_TAX_METHODS,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName", "editCommerceTaxFixedRateAddressRel");
		portletURL.setParameter(
			"commerceTaxMethodId", String.valueOf(getCommerceTaxMethodId()));

		portletURL.setWindowState(LiferayWindowState.POP_UP);

		return portletURL.toString();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasUpdateCommerceChannelPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddTaxRateSettingURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commerceTaxFixedRateRequestHelper.getRequest(),
							"add-tax-rate-setting"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public String getDatasetView() {
		return CommerceTaxRateSettingDataSetConstants.
			COMMERCE_DATA_SET_KEY_PERCENTAGE_TAX_RATE_SETTING;
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CommerceTaxMethodAvalaraRateRelsScreenNavigationEntry.
			CATEGORY_KEY;
	}

}