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

import com.liferay.commerce.avalara.connector.CommerceAvalaraConnector;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.avalara.connector.exception.CommerceAvalaraConnectionException;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Base64;

import java.util.HashMap;
import java.util.Map;

import net.avalara.avatax.rest.client.AvaTaxClient;
import net.avalara.avatax.rest.client.FetchResult;
import net.avalara.avatax.rest.client.models.CompanyModel;
import net.avalara.avatax.rest.client.models.PingResultModel;

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

	@Reference
	private ConfigurationProvider _configurationProvider;

}