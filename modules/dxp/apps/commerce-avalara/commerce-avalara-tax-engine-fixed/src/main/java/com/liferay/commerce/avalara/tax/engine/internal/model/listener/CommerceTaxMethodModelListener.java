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

package com.liferay.commerce.avalara.tax.engine.internal.model.listener;

import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelLocalService;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.UnicodeProperties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(enabled = false, immediate = true, service = ModelListener.class)
public class CommerceTaxMethodModelListener
	extends BaseModelListener<CommerceTaxMethod> {

	@Override
	public void onAfterCreate(CommerceTaxMethod commerceTaxMethod) {
		String engineKey = commerceTaxMethod.getEngineKey();

		if (engineKey.equals(CommerceAvalaraConstants.AVALARA)) {
			try {
				CommerceChannel commerceChannel = _getAssociatedCommerceChannel(
					commerceTaxMethod);

				String triggerName = _getAvalaraTriggerName(commerceChannel);

				UnicodeProperties unicodeProperties = new UnicodeProperties(
					true);

				unicodeProperties.setProperty(
					CommerceAvalaraConstants.GROUP_ID,
					String.valueOf(commerceTaxMethod.getGroupId()));

				DispatchTrigger dispatchTrigger =
					_dispatchTriggerLocalService.addDispatchTrigger(
						commerceTaxMethod.getUserId(), triggerName,
						Boolean.FALSE, commerceTaxMethod.getEngineKey(),
						unicodeProperties);

				dispatchTrigger.setCronExpression(
					_EVERY_MONTH_ON_THE_SECOND_CRON_EXPRESSION);

				dispatchTrigger.setActive(Boolean.TRUE);

				_dispatchTriggerLocalService.updateDispatchTrigger(
					dispatchTrigger);
			}
			catch (PortalException portalException) {
				_log.error(
					"Could not create a dispatch trigger for newly created " +
						"Avalara Tax Method",
					portalException);
			}
		}
	}

	@Override
	public void onAfterUpdate(CommerceTaxMethod commerceTaxMethod) {
		String engineKey = commerceTaxMethod.getEngineKey();

		if (engineKey.equals(CommerceAvalaraConstants.AVALARA)) {
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

				_dispatchTriggerLocalService.updateDispatchTrigger(
					dispatchTrigger);
			}
		}
	}

	@Override
	public void onBeforeRemove(CommerceTaxMethod commerceTaxMethod) {
		String engineKey = commerceTaxMethod.getEngineKey();

		if (engineKey.equals(CommerceAvalaraConstants.AVALARA)) {
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
					"Could not delete dispatch trigger for an Avalara Tax " +
						"Method",
					portalException);
			}
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
		CommerceTaxMethodModelListener.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

	@Reference
	private CommerceTaxFixedRateLocalService _commerceTaxFixedRateLocalService;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}