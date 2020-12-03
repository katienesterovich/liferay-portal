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
import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.avalara.connector.helper.CommerceAvalaraConnectorHelper;
import com.liferay.commerce.model.CommerceCountry;
import com.liferay.commerce.model.CommerceRegion;
import com.liferay.commerce.service.CommerceCountryLocalService;
import com.liferay.commerce.service.CommerceRegionLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	enabled = false, immediate = true,
	service = CommerceAvalaraConnectorHelper.class
)
public class CommerceAvalaraConnectorHelperImpl
	implements CommerceAvalaraConnectorHelper {

	@Override
	public void updateByAddressEntries(long groupId) throws Exception {
		String taxRateByZipCode = _commerceAvalaraConnector.getTaxRateByZipCode(
			groupId);

		if (Validator.isBlank(taxRateByZipCode)) {
			return;
		}

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodLocalService.fetchCommerceTaxMethod(
				groupId, CommerceAvalaraConstants.KEY);

		if (commerceTaxMethod == null) {
			return;
		}

		String[] taxRatesByZipCodeLines = StringUtil.splitLines(
			taxRateByZipCode);

		for (int i = 1; i < taxRatesByZipCodeLines.length; i++) {
			String[] taxRatesByZipCodeLine = StringUtil.split(
				taxRatesByZipCodeLines[i], StringPool.COMMA);

			_upsertByAddressEntry(
				commerceTaxMethod.getCompanyId(), commerceTaxMethod.getUserId(),
				groupId, commerceTaxMethod.getCommerceTaxMethodId(), "US",
				taxRatesByZipCodeLine
					[CommerceAvalaraConstants.CSV_REGION_POSITION],
				taxRatesByZipCodeLine
					[CommerceAvalaraConstants.CSV_ZIP_CODE_POSITION],
				Double.valueOf(
					taxRatesByZipCodeLine
						[CommerceAvalaraConstants.
							CSV_TOTAL_SALES_TAX_POSITION]));
		}
	}

	private void _upsertByAddressEntry(
			long companyId, long userId, long groupId, long commerceTaxMethodId,
			String countryCode, String regionCode, String zip, double rate)
		throws Exception {

		CommerceCountry commerceCountry =
			_commerceCountryLocalService.fetchCommerceCountry(
				companyId, countryCode);

		if (commerceCountry == null) {
			return;
		}

		CommerceRegion commerceRegion =
			_commerceRegionLocalService.getCommerceRegion(
				commerceCountry.getCommerceCountryId(), regionCode);

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxMethodId, commerceCountry.getCommerceCountryId(),
					commerceRegion.getCommerceRegionId(), zip);

		if (commerceTaxFixedRateAddressRel == null) {

			// TODO retrieve tax category for tangible personal property

			long cpTaxCategoryId = 0;

			_commerceTaxFixedRateAddressRelLocalService.
				addCommerceTaxFixedRateAddressRel(
					userId, groupId, commerceTaxMethodId, cpTaxCategoryId,
					commerceCountry.getCommerceCountryId(),
					commerceRegion.getCommerceRegionId(), zip, rate);
		}
		else {
			_commerceTaxFixedRateAddressRelLocalService.
				updateCommerceTaxFixedRateAddressRel(
					commerceTaxFixedRateAddressRel.
						getCommerceTaxFixedRateAddressRelId(),
					commerceCountry.getCommerceCountryId(),
					commerceRegion.getCommerceRegionId(), zip, rate);
		}
	}

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

	@Reference
	private CommerceCountryLocalService _commerceCountryLocalService;

	@Reference
	private CommerceRegionLocalService _commerceRegionLocalService;

	@Reference
	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

}