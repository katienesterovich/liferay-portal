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
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
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
	public void createDispatchTrigger(CommerceTaxMethod commerceTaxMethod) {
		try {
			CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
				commerceTaxMethod);

			String triggerName = _getAvalaraTriggerName(commerceChannel);

			UnicodeProperties unicodeProperties = new UnicodeProperties(true);

			unicodeProperties.setProperty(
				CommerceAvalaraConstants.GROUP_ID,
				String.valueOf(commerceTaxMethod.getGroupId()));

			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.addDispatchTrigger(
					commerceTaxMethod.getUserId(), triggerName, Boolean.FALSE,
					commerceTaxMethod.getEngineKey(), unicodeProperties);

			dispatchTrigger.setCronExpression(
				_EVERY_MONTH_ON_THE_SECOND_CRON_EXPRESSION);

			_dispatchTriggerLocalService.updateDispatchTrigger(dispatchTrigger);
		}
		catch (PortalException portalException) {
			_log.error(
				"Could not create a dispatch trigger for newly created " +
					"Avalara Tax Method",
				portalException);
		}
	}

	@Override
	public void deleteDispatchTrigger(CommerceTaxMethod commerceTaxMethod) {
		try {
			CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
				commerceTaxMethod);

			String triggerName = _getAvalaraTriggerName(commerceChannel);

			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					commerceChannel.getCompanyId(), triggerName);

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
	public boolean jobPreviouslyRunSuccessfully(
		CommerceTaxMethod commerceTaxMethod) {

		CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
			commerceTaxMethod);

		String triggerName = _getAvalaraTriggerName(commerceChannel);

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				commerceChannel.getCompanyId(), triggerName);

		if (dispatchTrigger == null) {
			return false;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_dispatchLogLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property dispatchTriggerId = PropertyFactoryUtil.forName(
					"dispatchTriggerId");

				dynamicQuery.add(
					dispatchTriggerId.eq(
						dispatchTrigger.getDispatchTriggerId()));

				Property status = PropertyFactoryUtil.forName("status");

				dynamicQuery.add(
					status.eq(DispatchTaskStatus.SUCCESSFUL.getStatus()));
			});

		long successfulJobsCount = 0;

		try {
			successfulJobsCount = actionableDynamicQuery.performCount();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Could not execute performCount on ActionableDynamicQuery",
					portalException);
			}
		}

		if (successfulJobsCount > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void updateDispatchTrigger(CommerceTaxMethod commerceTaxMethod) {
		CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
			commerceTaxMethod);

		String triggerName = _getAvalaraTriggerName(commerceChannel);

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				commerceChannel.getCompanyId(), triggerName);

		if (dispatchTrigger != null) {
			if (commerceTaxMethod.isActive()) {
				dispatchTrigger.setActive(Boolean.TRUE);
			}
			else {
				dispatchTrigger.setActive(Boolean.FALSE);
			}

			_dispatchTriggerLocalService.updateDispatchTrigger(dispatchTrigger);
		}
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

	private static final String _EVERY_MONTH_ON_THE_SECOND_CRON_EXPRESSION =
		"0 0 1 2 * ?";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAvalaraDispatchTriggerHelperImpl.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private DispatchLogLocalService _dispatchLogLocalService;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}