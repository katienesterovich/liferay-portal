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

package com.liferay.commerce.avalara.connector.internal.helper;

import com.liferay.commerce.avalara.connector.CommerceAvalaraConnector;
import com.liferay.commerce.avalara.connector.helper.CommerceAvalaraConnectorHelper;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.avalara.avatax.rest.client.models.TaxCodeModel;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 * @author Katie Nesterovich
 */
@Component(
	enabled = false, immediate = true,
	service = CommerceAvalaraConnectorHelper.class
)
public class CommerceAvalaraConnectorHelperImpl
	implements CommerceAvalaraConnectorHelper {

	public void createTaxCategories(long userId) throws Exception {
		List<TaxCodeModel> taxCodeModels =
			_commerceAvalaraConnector.listTaxCodes();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		for (TaxCodeModel taxCodeModel : taxCodeModels) {
			Map<Locale, String> nameMap = Collections.singletonMap(
				LocaleUtil.getSiteDefault(), taxCodeModel.getDescription());

			_cpTaxCategoryLocalService.addCPTaxCategory(
				taxCodeModel.getTaxCode(), nameMap, Collections.emptyMap(),
				serviceContext);
		}
	}

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

}