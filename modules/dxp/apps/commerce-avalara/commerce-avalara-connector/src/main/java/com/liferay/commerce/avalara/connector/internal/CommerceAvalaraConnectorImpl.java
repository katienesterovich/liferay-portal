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

package com.liferay.commerce.avalara.connector.internal;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.avalara.connector.CommerceAvalaraConnector;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorChannelConfiguration;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.avalara.connector.exception.CommerceAvalaraConnectionException;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceCountry;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceRegion;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.tax.configuration.CommerceShippingTaxConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.Base64;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.avalara.avatax.rest.client.AvaTaxClient;
import net.avalara.avatax.rest.client.FetchResult;
import net.avalara.avatax.rest.client.TransactionBuilder;
import net.avalara.avatax.rest.client.enums.DocumentType;
import net.avalara.avatax.rest.client.enums.TransactionAddressType;
import net.avalara.avatax.rest.client.models.CompanyModel;
import net.avalara.avatax.rest.client.models.CreateTransactionModel;
import net.avalara.avatax.rest.client.models.PingResultModel;
import net.avalara.avatax.rest.client.models.TaxCodeModel;
import net.avalara.avatax.rest.client.models.TransactionModel;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Calvin Keum
 * @author Katie Nesterovich
 * @author Riccardo Alberti
 */
@Component(
	enabled = false, immediate = true, service = CommerceAvalaraConnector.class
)
public class CommerceAvalaraConnectorImpl implements CommerceAvalaraConnector {

	@Override
	public TransactionModel createSalesOrderTransaction(
			CommerceOrder commerceOrder)
		throws Exception {

		return _createTransaction(DocumentType.SalesOrder, commerceOrder);
	}

	public String getTaxRateByZipCode() throws Exception {
		AvaTaxClient avaTaxClient = _getAvaTaxClient();

		Date date = new Date() {

			@Override
			public String toString() {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");

				return simpleDateFormat.format(this);
			}

		};

		return avaTaxClient.downloadTaxRatesByZipCode(date, null);
	}

	@Override
	public List<TaxCodeModel> listTaxCodes() throws Exception {
		AvaTaxClient avaTaxClient = _getAvaTaxClient();

		String expiredTaxCodeFilter =
			"description NE 'Expired Tax Code - Do Not Use'";

		FetchResult<TaxCodeModel> taxCodeModelFetchResult =
			avaTaxClient.listTaxCodes(expiredTaxCodeFilter, 0, 0, null);

		return taxCodeModelFetchResult.getValue();
	}

	@Override
	public Map<String, String> queryCompanies() throws Exception {
		AvaTaxClient avaTaxClient = _getAvaTaxClient();

		FetchResult<CompanyModel> companyModelFetchResult =
			avaTaxClient.queryCompanies(null, null, 0, 0, null);

		Map<String, String> companyNamesWithCodes = new HashMap<>();

		for (CompanyModel companyModel : companyModelFetchResult.getValue()) {
			companyNamesWithCodes.put(
				companyModel.getName(), companyModel.getCompanyCode());
		}

		return companyNamesWithCodes;
	}

	@Override
	public void verifyConnection(
			String accountNumber, String licenseKey, String serviceURL)
		throws Exception {

		try {
			AvaTaxClient avaTaxClient = _getAvaTaxClient(
				accountNumber, licenseKey, serviceURL);

			PingResultModel pingResultModel = avaTaxClient.ping();

			if (!pingResultModel.getAuthenticated()) {
				throw new CommerceAvalaraConnectionException();
			}
		}
		catch (Exception exception) {
			throw new CommerceAvalaraConnectionException(exception.getCause());
		}
	}

	private TransactionModel _createTransaction(
			DocumentType documentType, CommerceOrder commerceOrder)
		throws Exception {

		AvaTaxClient avaTaxClient = _getAvaTaxClient();

		CommerceAccount commerceAccount = commerceOrder.getCommerceAccount();

		CommerceAvalaraConnectorChannelConfiguration
			commerceAvalaraConnectorChannelConfiguration =
				_getAvalaraChannelConfiguration(commerceOrder.getGroupId());

		TransactionBuilder transactionBuilder = new TransactionBuilder(
			avaTaxClient,
			commerceAvalaraConnectorChannelConfiguration.companyCode(),
			documentType,
			String.valueOf(commerceAccount.getCommerceAccountId()));

		transactionBuilder.withCode(
			String.valueOf(commerceOrder.getCommerceOrderId()));

		CreateTransactionModel intermediaryTransactionModel =
			transactionBuilder.getIntermediaryTransactionModel();

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		intermediaryTransactionModel.setCurrencyCode(
			commerceCurrency.getCode());

		// TODO get entity use code from account

		intermediaryTransactionModel.setEntityUseCode("");

		_setAddress(commerceOrder, transactionBuilder);

		_setLines(commerceOrder, transactionBuilder);

		return transactionBuilder.create();
	}

	private CommerceAvalaraConnectorChannelConfiguration
			_getAvalaraChannelConfiguration(long groupId)
		throws Exception {

		return _configurationProvider.getConfiguration(
			CommerceAvalaraConnectorChannelConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId,
				CommerceAvalaraConnectorChannelConfiguration.class.getName()));
	}

	private AvaTaxClient _getAvaTaxClient() throws PortalException {
		CommerceAvalaraConnectorConfiguration
			commerceAvalaraConnectorConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAvalaraConnectorConfiguration.class,
					new CompanyServiceSettingsLocator(
						CompanyThreadLocal.getCompanyId(),
						CommerceAvalaraConnectorConfiguration.class.getName()));

		return _getAvaTaxClient(
			commerceAvalaraConnectorConfiguration.accountNumber(),
			commerceAvalaraConnectorConfiguration.licenseKey(),
			commerceAvalaraConnectorConfiguration.serviceURL());
	}

	private AvaTaxClient _getAvaTaxClient(
		String accountNumber, String licenseKey, String serviceURL) {

		AvaTaxClient avaTaxClient = new AvaTaxClient(
			CommerceAvalaraConstants.APP_MACHINE,
			CommerceAvalaraConstants.APP_VERSION,
			CommerceAvalaraConstants.MACHINE_NAME, serviceURL);

		StringBundler sb = new StringBundler(3);

		sb.append(accountNumber);
		sb.append(StringPool.COLON);
		sb.append(licenseKey);

		String securityHeader = sb.toString();

		byte[] securityHeaderBytes = securityHeader.getBytes();

		String encodedSecurityHeader = Base64.encode(securityHeaderBytes);

		return avaTaxClient.withSecurity(encodedSecurityHeader);
	}

	private void _setAddress(
			CommerceOrder commerceOrder, TransactionBuilder transactionBuilder)
		throws Exception {

		if (commerceOrder.getShippingAddress() != null) {
			CommerceAddress commerceOrderShippingAddress =
				commerceOrder.getShippingAddress();

			CommerceRegion commerceRegion =
				commerceOrderShippingAddress.getCommerceRegion();

			CommerceCountry commerceCountry =
				commerceOrderShippingAddress.getCommerceCountry();

			// TODO we need to get the shipFrom address from the warehouse

			transactionBuilder.withAddress(
				TransactionAddressType.ShipTo,
				commerceOrderShippingAddress.getStreet1(),
				commerceOrderShippingAddress.getStreet2(),
				commerceOrderShippingAddress.getStreet3(),
				commerceOrderShippingAddress.getCity(),
				commerceRegion.getName(), commerceOrderShippingAddress.getZip(),
				commerceCountry.getTwoLettersISOCode());
		}
	}

	private void _setLines(
			CommerceOrder commerceOrder, TransactionBuilder transactionBuilder)
		throws Exception {

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

			CPTaxCategory cpTaxCategory = cpDefinition.getCPTaxCategory();

			String taxCode = StringPool.BLANK;

			if (cpTaxCategory != null) {
				taxCode = cpTaxCategory.getExternalReferenceCode();
			}

			// TODO if no tax category, use TPP?

			transactionBuilder.withLine(
				commerceOrderItem.getFinalPrice(),
				BigDecimal.valueOf(commerceOrderItem.getQuantity()), taxCode,
				commerceOrderItem.getSku(), cpDefinition.getDescription(),
				String.valueOf(commerceOrderItem.getCommerceOrderItemId()),
				String.valueOf(commerceOrderItem.getCPInstanceId()));
		}

		CommerceShippingTaxConfiguration commerceShippingTaxConfiguration =
			_configurationProvider.getConfiguration(
				CommerceShippingTaxConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceOrder.getGroupId(),
					CommerceConstants.TAX_SERVICE_NAME));

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.fetchCPTaxCategory(
				commerceShippingTaxConfiguration.taxCategoryId());

		if (cpTaxCategory == null) {
			return;
		}

		transactionBuilder.withLine(
			commerceOrder.getShippingAmount(), BigDecimal.ONE,
			cpTaxCategory.getExternalReferenceCode(),
			CommerceAvalaraConstants.SHIPPING_LINE_ITEM, StringPool.BLANK);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

}