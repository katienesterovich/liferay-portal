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
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.avalara.connector.helper.CommerceAvalaraDispatchTriggerHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(
	enabled = false, immediate = true,
	service = CommerceAvalaraDispatchTriggerHelper.class
)
public class CommerceAvalaraDispatchTriggerHelperImpl
	implements CommerceAvalaraDispatchTriggerHelper {

	@Override
	public DispatchTrigger createDispatchTrigger(
		CommerceTaxMethod commerceTaxMethod) {

		DispatchTrigger dispatchTrigger = null;

		try {
			CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
				commerceTaxMethod);

			String triggerName = _getAvalaraTriggerName(commerceChannel);

			UnicodeProperties unicodeProperties = new UnicodeProperties(
				Boolean.TRUE);

			unicodeProperties.setProperty(
				CommerceAvalaraConstants.GROUP_ID,
				String.valueOf(commerceTaxMethod.getGroupId()));

			dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
				commerceTaxMethod.getUserId(), triggerName, Boolean.FALSE,
				commerceTaxMethod.getEngineKey(), unicodeProperties);

			dispatchTrigger.setCronExpression(
				_EVERY_MONTH_ON_THE_FIRST_CRON_EXPRESSION);

			_dispatchTriggerLocalService.updateDispatchTrigger(dispatchTrigger);
		}
		catch (PortalException portalException) {
			_log.error(
				"Could not create a dispatch trigger for newly created " +
					"Avalara Tax Method",
				portalException);
		}

		return dispatchTrigger;
	}

	@Override
	public void deleteDispatchTrigger(CommerceTaxMethod commerceTaxMethod) {
		try {
			DispatchTrigger dispatchTrigger = _getDispatchTrigger(
				commerceTaxMethod);

			if (dispatchTrigger != null) {
				_dispatchTriggerLocalService.deleteDispatchTrigger(
					dispatchTrigger.getDispatchTriggerId());
			}
		}
		catch (PortalException portalException) {
			_log.error(
				"Could not delete dispatch trigger for an Avalara Tax Method",
				portalException);
		}
	}

	@Override
	public DispatchLog getLatestDispatchLog(
		CommerceTaxMethod commerceTaxMethod) {

		DispatchTrigger dispatchTrigger = _getDispatchTrigger(
			commerceTaxMethod);

		if (dispatchTrigger == null) {
			return null;
		}

		DynamicQuery dynamicQuery = _getDispatchTriggerByIdQuery(
			dispatchTrigger);

		Property startDate = PropertyFactoryUtil.forName("startDate");

		dynamicQuery.addOrder(startDate.desc());

		dynamicQuery.setLimit(0, 1);

		List<DispatchLog> dispatchLogs = _dispatchLogLocalService.dynamicQuery(
			dynamicQuery);

		if (!dispatchLogs.isEmpty()) {
			return dispatchLogs.get(0);
		}

		return null;
	}

	@Override
	public boolean jobPreviouslyRun(CommerceTaxMethod commerceTaxMethod) {
		DispatchTrigger dispatchTrigger = _getDispatchTrigger(
			commerceTaxMethod);

		if (dispatchTrigger == null) {
			return false;
		}

		DynamicQuery dynamicQuery = _getDispatchTriggerByIdQuery(
			dispatchTrigger);

		if (_dispatchLogLocalService.dynamicQueryCount(dynamicQuery) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void runJob(CommerceTaxMethod commerceTaxMethod) {
		DispatchTrigger dispatchTrigger = _getDispatchTrigger(
			commerceTaxMethod);

		if (dispatchTrigger == null) {
			dispatchTrigger = createDispatchTrigger(commerceTaxMethod);
		}

		_sendMessage(dispatchTrigger.getDispatchTriggerId());
	}

	@Override
	public DispatchTrigger updateDispatchTrigger(
		CommerceTaxMethod commerceTaxMethod) {

		if (!commerceTaxMethod.isActive()) {
			return updateDispatchTrigger(commerceTaxMethod, Boolean.FALSE);
		}

		CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
			commerceTaxMethod);

		try {
			CommerceAvalaraConnectorConfiguration
				commerceAvalaraConnectorConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAvalaraConnectorConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceChannel.getGroupId(),
							CommerceAvalaraConnectorConfiguration.class.
								getName()));

			_commerceAvalaraConnector.verifyConnection(
				commerceAvalaraConnectorConfiguration.accountNumber(),
				commerceAvalaraConnectorConfiguration.licenseKey(),
				commerceAvalaraConnectorConfiguration.serviceURL());

			return updateDispatchTrigger(commerceTaxMethod, Boolean.TRUE);
		}
		catch (Exception exception) {
			return updateDispatchTrigger(commerceTaxMethod, Boolean.FALSE);
		}
	}

	@Override
	public DispatchTrigger updateDispatchTrigger(
		CommerceTaxMethod commerceTaxMethod, boolean activate) {

		DispatchTrigger dispatchTrigger = _getDispatchTrigger(
			commerceTaxMethod);

		if (dispatchTrigger != null) {
			dispatchTrigger.setActive(activate);

			_dispatchTriggerLocalService.updateDispatchTrigger(dispatchTrigger);
		}

		return dispatchTrigger;
	}

	private CommerceChannel _getAssociatedCommerceChannel(
		CommerceTaxMethod commerceTaxMethod) {

		CommerceChannel commerceChannel = null;

		try {
			commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByGroupId(
					commerceTaxMethod.getGroupId());
		}
		catch (PortalException portalException) {
			_log.error("Could not get CommerceChannel", portalException);
		}

		return commerceChannel;
	}

	private String _getAvalaraTriggerName(CommerceChannel commerceChannel) {
		StringBundler triggerNameSB = new StringBundler(3);

		triggerNameSB.append(CommerceAvalaraConstants.AVALARA);
		triggerNameSB.append(StringPool.DASH);
		triggerNameSB.append(commerceChannel.getName());

		return triggerNameSB.toString();
	}

	private DispatchTrigger _getDispatchTrigger(
		CommerceTaxMethod commerceTaxMethod) {

		CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
			commerceTaxMethod);

		String triggerName = _getAvalaraTriggerName(commerceChannel);

		return _dispatchTriggerLocalService.fetchDispatchTrigger(
			commerceChannel.getCompanyId(), triggerName);
	}

	private DynamicQuery _getDispatchTriggerByIdQuery(
		DispatchTrigger dispatchTrigger) {

		DynamicQuery dynamicQuery = _dispatchLogLocalService.dynamicQuery();
		Property dispatchTriggerId = PropertyFactoryUtil.forName(
			"dispatchTriggerId");

		dynamicQuery.add(
			dispatchTriggerId.eq(dispatchTrigger.getDispatchTriggerId()));

		return dynamicQuery;
	}

	private void _sendMessage(long dispatchTriggerId) {
		Message message = new Message();

		message.setPayload(
			String.valueOf(
				JSONUtil.put("dispatchTriggerId", dispatchTriggerId)));

		_destination.send(message);
	}

	private static final String _EVERY_MONTH_ON_THE_FIRST_CRON_EXPRESSION =
		"0 0 0 1 * ?";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAvalaraDispatchTriggerHelperImpl.class);

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(destination.name=" + DispatchConstants.EXECUTOR_DESTINATION_NAME + ")"
	)
	private Destination _destination;

	@Reference
	private DispatchLogLocalService _dispatchLogLocalService;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}