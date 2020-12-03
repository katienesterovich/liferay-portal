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
import com.liferay.commerce.exception.NoSuchRegionException;
import com.liferay.commerce.model.CommerceCountry;
import com.liferay.commerce.model.CommerceRegion;
import com.liferay.commerce.service.CommerceCountryLocalService;
import com.liferay.commerce.service.CommerceRegionLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
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

		CommerceCountry commerceCountryUS =
			_commerceCountryLocalService.fetchCommerceCountry(
				commerceTaxMethod.getCompanyId(), "US");

		if (commerceCountryUS == null) {
			commerceCountryUS = _addUnitedStates(commerceTaxMethod);
		}

		for (int i = 1; i < taxRatesByZipCodeLines.length; i++) {
			String[] taxRatesByZipCodeLine = StringUtil.split(
				taxRatesByZipCodeLines[i], StringPool.COMMA);

			_upsertByAddressEntry(
				commerceTaxMethod.getUserId(), groupId,
				commerceTaxMethod.getCommerceTaxMethodId(), commerceCountryUS,
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

	private CommerceCountry _addUnitedStates(
		CommerceTaxMethod commerceTaxMethod) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(commerceTaxMethod.getUserId());
		serviceContext.setCompanyId(commerceTaxMethod.getCompanyId());
		serviceContext.setLanguageId(commerceTaxMethod.getDefaultLanguageId());

		CommerceCountry commerceCountry = null;

		try {
			commerceCountry = _commerceCountryLocalService.addCommerceCountry(
				HashMapBuilder.put(
					serviceContext.getLocale(),
					LanguageUtil.get(
						serviceContext.getLocale(), "country." + _NAME_US)
				).build(),
				true, true, _TWO_LETTERS_ISO_CODE_US,
				_THREE_LETTERS_ISO_CODE_US, _NUMERIC_ISO_CODE_US, false,
				_PRIORITY_US, true, serviceContext);
		}
		catch (PortalException portalException) {
			_log.error(
				"Could not add United States as a commerce country",
				portalException);
		}

		return commerceCountry;
	}

	private void _upsertByAddressEntry(
			long userId, long groupId, long commerceTaxMethodId,
			CommerceCountry commerceCountry, String regionCode, String zip,
			double rate)
		throws Exception {

		CommerceRegion commerceRegion;

		try {
			commerceRegion = _commerceRegionLocalService.getCommerceRegion(
				commerceCountry.getCommerceCountryId(), regionCode);
		}
		catch (NoSuchRegionException noSuchRegionException) {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setUserId(userId);

			commerceRegion = _commerceRegionLocalService.addCommerceRegion(
				commerceCountry.getCommerceCountryId(), regionCode, regionCode,
				0, Boolean.FALSE, serviceContext);
		}

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

	private static final String _NAME_US = "united-states";

	private static final int _NUMERIC_ISO_CODE_US = 840;

	private static final int _PRIORITY_US = 19;

	private static final String _THREE_LETTERS_ISO_CODE_US = "USA";

	private static final String _TWO_LETTERS_ISO_CODE_US = "US";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAvalaraConnectorHelperImpl.class);

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