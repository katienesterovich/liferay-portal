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

import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.avalara.connector.helper.CommerceAvalaraDispatchTriggerHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.UnicodeProperties;

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
			String triggerName = _getAvalaraTriggerName(commerceTaxMethod);

			UnicodeProperties unicodeProperties = new UnicodeProperties(true);

			unicodeProperties.setProperty(
				CommerceAvalaraConstants.GROUP_ID,
				String.valueOf(commerceTaxMethod.getGroupId()));

			dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
				commerceTaxMethod.getUserId(), commerceTaxMethod.getEngineKey(),
				unicodeProperties, triggerName, false);

			dispatchTrigger.setCronExpression(
				_EVERY_MONTH_ON_THE_FIRST_CRON_EXPRESSION);

			dispatchTrigger.setActive(true);

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
	public DispatchTrigger updateDispatchTrigger(
		CommerceTaxMethod commerceTaxMethod) {

		DispatchTrigger dispatchTrigger = _getDispatchTrigger(
			commerceTaxMethod);

		if (dispatchTrigger != null) {
			dispatchTrigger.setActive(commerceTaxMethod.isActive());

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
			_log.error("Could not get commerce channel", portalException);
		}

		return commerceChannel;
	}

	private String _getAvalaraTriggerName(CommerceTaxMethod commerceTaxMethod) {
		CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
			commerceTaxMethod);

		StringBundler triggerNameSB = new StringBundler(3);

		triggerNameSB.append(CommerceAvalaraConstants.AVALARA);
		triggerNameSB.append(StringPool.DASH);
		triggerNameSB.append(commerceChannel.getCommerceChannelId());

		return triggerNameSB.toString();
	}

	private DispatchTrigger _getDispatchTrigger(
		CommerceTaxMethod commerceTaxMethod) {

		String triggerName = _getAvalaraTriggerName(commerceTaxMethod);

		return _dispatchTriggerLocalService.fetchDispatchTrigger(
			commerceTaxMethod.getCompanyId(), triggerName);
	}

	private static final String _EVERY_MONTH_ON_THE_FIRST_CRON_EXPRESSION =
		"0 0 0 1 * ?";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAvalaraDispatchTriggerHelperImpl.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}