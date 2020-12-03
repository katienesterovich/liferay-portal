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
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.Base64;

import java.text.SimpleDateFormat;

import java.util.Date;

import net.avalara.avatax.rest.client.AvaTaxClient;
import net.avalara.avatax.rest.client.models.PingResultModel;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Calvin Keum
 * @author Riccardo Alberti
 */
@Component(
	enabled = false, immediate = true, service = CommerceAvalaraConnector.class
)
public class CommerceAvalaraConnectorImpl implements CommerceAvalaraConnector {

	@Override
	public String getTaxRateByZipCode(long groupId) throws Exception {
		AvaTaxClient avaTaxClient = _getAvaTaxClient(groupId);

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

	private AvaTaxClient _getAvaTaxClient(long groupId) throws PortalException {
		_commerceAvalaraConnectorConfiguration =
			_getCommerceTaxTypeAvalaraConfiguration(groupId);

		return _getAvaTaxClient(
			_commerceAvalaraConnectorConfiguration.accountNumber(),
			_commerceAvalaraConnectorConfiguration.licenseKey(),
			_commerceAvalaraConnectorConfiguration.serviceURL());
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

	private CommerceAvalaraConnectorConfiguration
			_getCommerceTaxTypeAvalaraConfiguration(long groupId)
		throws PortalException {

		return _configurationProvider.getConfiguration(
			CommerceAvalaraConnectorConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId,
				CommerceAvalaraConnectorConfiguration.class.getName()));
	}

	private CommerceAvalaraConnectorConfiguration
		_commerceAvalaraConnectorConfiguration;

	@Reference
	private ConfigurationProvider _configurationProvider;

}