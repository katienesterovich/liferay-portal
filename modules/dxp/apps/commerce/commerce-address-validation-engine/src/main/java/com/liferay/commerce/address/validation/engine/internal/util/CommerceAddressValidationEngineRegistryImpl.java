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

package com.liferay.commerce.address.validation.engine.internal.util;

import com.liferay.commerce.address.validation.engine.CommerceAddressValidationEngine;
import com.liferay.commerce.address.validation.engine.util.CommerceAddressValidationEngineRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Katie Nesterovich
 */
@Component(
	enabled = false, immediate = true,
	service = CommerceAddressValidationEngineRegistry.class
)
public class CommerceAddressValidationEngineRegistryImpl
	implements CommerceAddressValidationEngineRegistry {

	@Override
	public CommerceAddressValidationEngine getCommerceAddressValidationEngine(
		String key) {

		return _serviceTrackerMap.getService(key);
	}

	@Override
	public Map<String, CommerceAddressValidationEngine>
		getCommerceAddressValidationEngines() {

		Map<String, CommerceAddressValidationEngine>
			commerceAddressValidationEngines = new HashMap<>();

		for (String key : _serviceTrackerMap.keySet()) {
			commerceAddressValidationEngines.put(
				key, _serviceTrackerMap.getService(key));
		}

		return Collections.unmodifiableMap(commerceAddressValidationEngines);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CommerceAddressValidationEngine.class,
			"commerce.address.validation.engine.key");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, CommerceAddressValidationEngine>
		_serviceTrackerMap;

}