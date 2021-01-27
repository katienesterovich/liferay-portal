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
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.exception.NoSuchRegionException;
import com.liferay.commerce.model.CommerceCountry;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceRegion;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.service.CommerceCountryLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import net.avalara.avatax.rest.client.models.TaxCodeModel;
import net.avalara.avatax.rest.client.models.TransactionLineModel;
import net.avalara.avatax.rest.client.models.TransactionModel;

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

	@Override
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

	@Override
	public void removeByAddressEntries(CommerceTaxMethod commerceTaxMethod) {
		_commerceTaxFixedRateAddressRelLocalService.
			deleteCommerceTaxFixedRateAddressRelsByCommerceTaxMethodId(
				commerceTaxMethod.getCommerceTaxMethodId());
	}

	@Override
	public void updateByAddressEntries(long groupId) throws Exception {
		String taxRateByZipCode =
			_commerceAvalaraConnector.getTaxRateByZipCode();

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

		CommerceCountry usCommerceCountry =
			_commerceCountryLocalService.fetchCommerceCountry(
				commerceTaxMethod.getCompanyId(), "US");

		if (usCommerceCountry == null) {
			usCommerceCountry = _addUnitedStates(commerceTaxMethod);
		}

		long cpTaxCategoryId = 0;

		CPTaxCategory tangiblePersonalPropertyCPTaxCategory =
			_cpTaxCategoryLocalService.fetchCPTaxCategoryByReferenceCode(
				commerceTaxMethod.getCompanyId(),
				_TANGIBLE_PERSONAL_PROPERTY_AVALARA_TAX_CODE);

		if (tangiblePersonalPropertyCPTaxCategory != null) {
			cpTaxCategoryId =
				tangiblePersonalPropertyCPTaxCategory.getCPTaxCategoryId();
		}

		for (int i = 1; i < taxRatesByZipCodeLines.length; i++) {
			String[] taxRatesByZipCodeLine = StringUtil.split(
				taxRatesByZipCodeLines[i], StringPool.COMMA);

			_upsertByAddressEntry(
				commerceTaxMethod.getUserId(), groupId, cpTaxCategoryId,
				commerceTaxMethod.getCommerceTaxMethodId(), usCommerceCountry,
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

	@Override
	public CommerceOrder updateSalesOrderTaxInfo(CommerceOrder commerceOrder)
		throws Exception {

		TransactionModel salesOrderTransaction =
			_commerceAvalaraConnector.createSalesOrderTransaction(
				commerceOrder);

		return _updateTaxInfo(commerceOrder, salesOrderTransaction);
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

	private double _convertDecimalToPercent(double decimal) {
		return decimal * 100;
	}

	private void _updateDiscountLevelPercentages() {

		// TODO update discount percentages

	}

	private void _updateOrderItemPricingInfo(
			CommerceOrderItem commerceOrderItem,
			TransactionLineModel transactionLineModel)
		throws Exception {

		BigDecimal finalPrice = commerceOrderItem.getFinalPrice();

		BigDecimal taxCalculated = transactionLineModel.getTaxCalculated();

		commerceOrderItem.setFinalPriceWithTaxAmount(
			finalPrice.add(taxCalculated));

		BigDecimal discountAmount = commerceOrderItem.getDiscountAmount();

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		BigDecimal unitTaxAmount = taxCalculated.divide(
			BigDecimal.valueOf(commerceOrderItem.getQuantity()),
			RoundingMode.valueOf(commerceCurrency.getRoundingMode()));

		commerceOrderItem.setDiscountWithTaxAmount(
			discountAmount.add(unitTaxAmount));

		_updateDiscountLevelPercentages();
	}

	private void _updateOrderItemsAndShippingPricingInfo(
			CommerceOrder commerceOrder, TransactionModel transactionModel)
		throws Exception {

		List<TransactionLineModel> transactionLineModels =
			transactionModel.getLines();

		for (TransactionLineModel transactionLineModel :
				transactionLineModels) {

			if (Objects.equals(
					transactionLineModel.getItemCode(),
					CommerceAvalaraConstants.SHIPPING_LINE_ITEM)) {

				_updateShippingPricingInfo(commerceOrder, transactionLineModel);

				continue;
			}

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemLocalService.fetchCommerceOrderItem(
					Long.valueOf(transactionLineModel.getRef1()));

			if (commerceOrderItem == null) {
				continue;
			}

			_updateOrderItemPricingInfo(
				commerceOrderItem, transactionLineModel);

			_commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem);
		}
	}

	private void _updateShippingPricingInfo(
		CommerceOrder commerceOrder,
		TransactionLineModel transactionLineModel) {

		BigDecimal shippingAmount = commerceOrder.getShippingAmount();

		BigDecimal shippingTaxAmount = transactionLineModel.getTaxCalculated();

		commerceOrder.setShippingWithTaxAmount(
			shippingAmount.add(shippingTaxAmount));

		BigDecimal shippingDiscountAmount =
			commerceOrder.getShippingDiscountAmount();

		commerceOrder.setShippingDiscountWithTaxAmount(
			shippingDiscountAmount.add(shippingTaxAmount));

		_updateDiscountLevelPercentages();
	}

	private CommerceOrder _updateTaxInfo(
			CommerceOrder commerceOrder, TransactionModel transactionModel)
		throws Exception {

		// discount target net price

		_updateOrderItemsAndShippingPricingInfo(
			commerceOrder, transactionModel);

		// subtotal should use the by address calculation ??

		_updateTotalPricingInfo(commerceOrder, transactionModel);

		// discount target gross price

		commerceOrder.setManuallyAdjusted(true);

		return _commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	private void _updateTotalPricingInfo(
		CommerceOrder commerceOrder, TransactionModel transactionModel) {

		BigDecimal totalTaxCalculated =
			transactionModel.getTotalTaxCalculated();

		commerceOrder.setTaxAmount(totalTaxCalculated);

		BigDecimal total = commerceOrder.getTotal();

		commerceOrder.setTotalWithTaxAmount(total.add(totalTaxCalculated));

		BigDecimal totalDiscountAmount = commerceOrder.getTotalDiscountAmount();

		commerceOrder.setTotalDiscountWithTaxAmount(
			totalDiscountAmount.add(totalTaxCalculated));

		_updateDiscountLevelPercentages();
	}

	private void _upsertByAddressEntry(
			long userId, long groupId,
			long tangiblePersonalPropertyCPTaxCategoryId,
			long commerceTaxMethodId, CommerceCountry commerceCountry,
			String regionCode, String zip, double rate)
		throws Exception {

		CommerceRegion commerceRegion;

		rate = _convertDecimalToPercent(rate);

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
			_commerceTaxFixedRateAddressRelLocalService.
				addCommerceTaxFixedRateAddressRel(
					userId, groupId, commerceTaxMethodId,
					tangiblePersonalPropertyCPTaxCategoryId,
					commerceCountry.getCommerceCountryId(),
					commerceRegion.getCommerceRegionId(), zip, rate);
		}
		else {
			commerceTaxFixedRateAddressRel.setCPTaxCategoryId(
				tangiblePersonalPropertyCPTaxCategoryId);
			commerceTaxFixedRateAddressRel.setRate(rate);

			_commerceTaxFixedRateAddressRelLocalService.
				updateCommerceTaxFixedRateAddressRel(
					commerceTaxFixedRateAddressRel);
		}
	}

	private static final String _NAME_US = "united-states";

	private static final int _NUMERIC_ISO_CODE_US = 840;

	private static final int _PRIORITY_US = 19;

	private static final String _TANGIBLE_PERSONAL_PROPERTY_AVALARA_TAX_CODE =
		"P0000000";

	private static final String _THREE_LETTERS_ISO_CODE_US = "USA";

	private static final String _TWO_LETTERS_ISO_CODE_US = "US";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAvalaraConnectorHelperImpl.class);

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

	@Reference
	private CommerceCountryLocalService _commerceCountryLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceRegionLocalService _commerceRegionLocalService;

	@Reference
	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

}