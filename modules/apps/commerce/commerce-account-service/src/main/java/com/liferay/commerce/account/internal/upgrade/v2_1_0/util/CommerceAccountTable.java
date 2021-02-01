/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.commerce.account.internal.upgrade.v2_1_0.util;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author	  Brian Wing Shun Chan
 * @generated
 */
public class CommerceAccountTable {

	public static final String TABLE_NAME = "CommerceAccount";

	public static final Object[][] TABLE_COLUMNS = {
		{"externalReferenceCode", Types.VARCHAR},
		{"commerceAccountId", Types.BIGINT}, {"companyId", Types.BIGINT},
		{"userId", Types.BIGINT}, {"userName", Types.VARCHAR},
		{"createDate", Types.TIMESTAMP}, {"modifiedDate", Types.TIMESTAMP},
		{"parentCommerceAccountId", Types.BIGINT}, {"name", Types.VARCHAR},
		{"logoId", Types.BIGINT}, {"email", Types.VARCHAR},
		{"taxId", Types.VARCHAR}, {"taxExemptionCode", Types.VARCHAR},
		{"type_", Types.INTEGER}, {"active_", Types.BOOLEAN},
		{"displayDate", Types.TIMESTAMP},
		{"defaultBillingAddressId", Types.BIGINT},
		{"defaultShippingAddressId", Types.BIGINT},
		{"expirationDate", Types.TIMESTAMP},
		{"lastPublishDate", Types.TIMESTAMP}, {"status", Types.INTEGER},
		{"statusByUserId", Types.BIGINT}, {"statusByUserName", Types.VARCHAR},
		{"statusDate", Types.TIMESTAMP}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
new HashMap<String, Integer>();

static {
TABLE_COLUMNS_MAP.put("externalReferenceCode", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("commerceAccountId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("parentCommerceAccountId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("name", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("logoId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("email", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("taxId", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("taxExemptionCode", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("type_", Types.INTEGER);

TABLE_COLUMNS_MAP.put("active_", Types.BOOLEAN);

TABLE_COLUMNS_MAP.put("displayDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("defaultBillingAddressId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("defaultShippingAddressId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("expirationDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("lastPublishDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("status", Types.INTEGER);

TABLE_COLUMNS_MAP.put("statusByUserId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("statusByUserName", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("statusDate", Types.TIMESTAMP);

}
	public static final String TABLE_SQL_CREATE =
"create table CommerceAccount (externalReferenceCode VARCHAR(75) null,commerceAccountId LONG not null primary key,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,parentCommerceAccountId LONG,name VARCHAR(255) null,logoId LONG,email VARCHAR(75) null,taxId VARCHAR(75) null,taxExemptionCode VARCHAR(75) null,type_ INTEGER,active_ BOOLEAN,displayDate DATE null,defaultBillingAddressId LONG,defaultShippingAddressId LONG,expirationDate DATE null,lastPublishDate DATE null,status INTEGER,statusByUserId LONG,statusByUserName VARCHAR(75) null,statusDate DATE null)";

	public static final String TABLE_SQL_DROP = "drop table CommerceAccount";

	public static final String[] TABLE_SQL_ADD_INDEXES = {
		"create index IX_721C700D on CommerceAccount (companyId, externalReferenceCode[$COLUMN_LENGTH:75$])",
		"create index IX_FE5BDB63 on CommerceAccount (userId, type_)"
	};

}