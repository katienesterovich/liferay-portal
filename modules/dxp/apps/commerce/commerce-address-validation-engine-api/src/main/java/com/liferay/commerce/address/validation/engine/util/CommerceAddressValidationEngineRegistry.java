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

package com.liferay.commerce.address.validation.engine.util;

import com.liferay.commerce.address.validation.engine.CommerceAddressValidationEngine;

import java.util.Map;

/**
 * @author Katie Nesterovich
 */
public interface CommerceAddressValidationEngineRegistry {

	public CommerceAddressValidationEngine getCommerceAddressValidationEngine(
		String key);

	public Map<String, CommerceAddressValidationEngine>
		getCommerceAddressValidationEngines();

}