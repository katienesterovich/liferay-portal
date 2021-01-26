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

package com.liferay.commerce.address.validation.engine.internal.configuration.admin.definition;

import com.liferay.commerce.address.validation.engine.CommerceAddressValidationEngine;
import com.liferay.commerce.address.validation.engine.util.CommerceAddressValidationEngineRegistry;
import com.liferay.configuration.admin.definition.ConfigurationFieldOptionsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(
	enabled = false,
	property = {
		"configuration.field.name=addressValidationEngine",
		"configuration.pid=com.liferay.commerce.address.validation.engine.configuration.CommerceAddressValidationEngineConfiguration"
	},
	service = ConfigurationFieldOptionsProvider.class
)
public class CommerceAddressValidationEngineConfigurationFieldOptionsProvider
	implements ConfigurationFieldOptionsProvider {

	@Override
	public List<Option> getOptions() {
		List<Option> options = new ArrayList<>();

		Map<String, CommerceAddressValidationEngine>
			commerceAddressValidationEngines =
				_commerceAddressValidationEngineRegistry.
					getCommerceAddressValidationEngines();

		for (CommerceAddressValidationEngine commerceAddressValidationEngine :
				commerceAddressValidationEngines.values()) {

			Option option = new Option() {

				@Override
				public String getLabel(Locale locale) {
					return commerceAddressValidationEngine.
						getAddressValidationEngineName();
				}

				@Override
				public String getValue() {
					return commerceAddressValidationEngine.
						getAddressValidationEngineName();
				}

			};

			options.add(option);
		}

		return options;
	}

	@Reference
	private CommerceAddressValidationEngineRegistry
		_commerceAddressValidationEngineRegistry;

}