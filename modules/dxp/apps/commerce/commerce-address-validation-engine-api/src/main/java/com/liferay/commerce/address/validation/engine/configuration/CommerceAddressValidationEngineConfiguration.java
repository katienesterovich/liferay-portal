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

package com.liferay.commerce.address.validation.engine.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Katie Nesterovich
 */
@ExtendedObjectClassDefinition(category = "address-validation")
@Meta.OCD(
	id = "com.liferay.commerce.address.validation.engine.configuration.CommerceAddressValidationEngineConfiguration",
	localization = "content/Language",
	name = "commerce-address-validation-engine-configuration-name"
)
public interface CommerceAddressValidationEngineConfiguration {

	@Meta.AD(
		deflt = StringPool.FALSE,
		description = "enable-address-validation-description",
		name = "enable-address-validation", required = false
	)
	public boolean enableAddressValidation();

	@Meta.AD(name = "address-validation-engine", required = false)
	public String addressValidationEngine();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-accounts-page",
		required = false
	)
	public boolean validateAddressInAccountsPage();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-accounts-widget",
		required = false
	)
	public boolean validateAddressInAccountsWidget();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-checkout-widget",
		required = false
	)
	public boolean validateAddressInCheckoutWidget();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-warehouses-page",
		required = false
	)
	public boolean validateAddressInWarehousesPage();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-shipments-widget",
		required = false
	)
	public boolean validateAddressInShipmentsWidget();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-orders-page",
		required = false
	)
	public boolean validateAddressInOrdersPage();

	@Meta.AD(
		deflt = StringPool.FALSE, name = "validate-address-in-invoicing-page",
		required = false
	)
	public boolean validateAddressInInvoicingPage();

}