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

package com.liferay.commerce.avalara.dispatch.web.internal.executor;

import com.liferay.commerce.avalara.connector.helper.CommerceAvalaraConnectorHelper;
import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(
	enabled = false, immediate = true,
	property = "dispatch.task.executor.type=" + AvalaraDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_AVALARA,
	service = DispatchTaskExecutor.class
)
public class AvalaraDispatchTaskExecutor extends BaseDispatchTaskExecutor {

	public static final String DISPATCH_TASK_EXECUTOR_TYPE_AVALARA = "avalara";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws PortalException {

		UnicodeProperties unicodeProperties =
			dispatchTrigger.getTaskSettingsUnicodeProperties();

		String groupId = GetterUtil.getString(
			unicodeProperties.getProperty(_GROUP_ID));

		try {
			_commerceAvalaraConnectorHelper.updateByAddressEntries(
				Long.parseLong(groupId));
		}
		catch (Exception exception) {
			dispatchTaskExecutorOutput.setError(exception.getMessage());

			throw new PortalException(exception);
		}
	}

	@Override
	public String getName() {
		return DISPATCH_TASK_EXECUTOR_TYPE_AVALARA;
	}

	private static final String _GROUP_ID = "groupId";

	@Reference
	private CommerceAvalaraConnectorHelper _commerceAvalaraConnectorHelper;

}