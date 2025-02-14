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

package com.liferay.batch.planner.model.impl;

import com.liferay.batch.planner.model.BatchPlannerLog;
import com.liferay.batch.planner.model.BatchPlannerLogModel;
import com.liferay.batch.planner.model.BatchPlannerLogSoap;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base model implementation for the BatchPlannerLog service. Represents a row in the &quot;BatchPlannerLog&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface <code>BatchPlannerLogModel</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link BatchPlannerLogImpl}.
 * </p>
 *
 * @author Igor Beslic
 * @see BatchPlannerLogImpl
 * @generated
 */
@JSON(strict = true)
public class BatchPlannerLogModelImpl
	extends BaseModelImpl<BatchPlannerLog> implements BatchPlannerLogModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a batch planner log model instance should use the <code>BatchPlannerLog</code> interface instead.
	 */
	public static final String TABLE_NAME = "BatchPlannerLog";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"batchPlannerLogId", Types.BIGINT},
		{"companyId", Types.BIGINT}, {"userId", Types.BIGINT},
		{"userName", Types.VARCHAR}, {"createDate", Types.TIMESTAMP},
		{"modifiedDate", Types.TIMESTAMP}, {"batchPlannerPlanId", Types.BIGINT},
		{"size_", Types.INTEGER}, {"total", Types.INTEGER},
		{"status", Types.INTEGER}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
		new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("batchPlannerLogId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("batchPlannerPlanId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("size_", Types.INTEGER);
		TABLE_COLUMNS_MAP.put("total", Types.INTEGER);
		TABLE_COLUMNS_MAP.put("status", Types.INTEGER);
	}

	public static final String TABLE_SQL_CREATE =
		"create table BatchPlannerLog (mvccVersion LONG default 0 not null,batchPlannerLogId LONG not null primary key,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,batchPlannerPlanId LONG,size_ INTEGER,total INTEGER,status INTEGER)";

	public static final String TABLE_SQL_DROP = "drop table BatchPlannerLog";

	public static final String ORDER_BY_JPQL =
		" ORDER BY batchPlannerLog.modifiedDate DESC";

	public static final String ORDER_BY_SQL =
		" ORDER BY BatchPlannerLog.modifiedDate DESC";

	public static final String DATA_SOURCE = "liferayDataSource";

	public static final String SESSION_FACTORY = "liferaySessionFactory";

	public static final String TX_MANAGER = "liferayTransactionManager";

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long BATCHPLANNERPLANID_COLUMN_BITMASK = 1L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *		#getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long MODIFIEDDATE_COLUMN_BITMASK = 2L;

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static void setEntityCacheEnabled(boolean entityCacheEnabled) {
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static void setFinderCacheEnabled(boolean finderCacheEnabled) {
	}

	/**
	 * Converts the soap model instance into a normal model instance.
	 *
	 * @param soapModel the soap model instance to convert
	 * @return the normal model instance
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static BatchPlannerLog toModel(BatchPlannerLogSoap soapModel) {
		if (soapModel == null) {
			return null;
		}

		BatchPlannerLog model = new BatchPlannerLogImpl();

		model.setMvccVersion(soapModel.getMvccVersion());
		model.setBatchPlannerLogId(soapModel.getBatchPlannerLogId());
		model.setCompanyId(soapModel.getCompanyId());
		model.setUserId(soapModel.getUserId());
		model.setUserName(soapModel.getUserName());
		model.setCreateDate(soapModel.getCreateDate());
		model.setModifiedDate(soapModel.getModifiedDate());
		model.setBatchPlannerPlanId(soapModel.getBatchPlannerPlanId());
		model.setSize(soapModel.getSize());
		model.setTotal(soapModel.getTotal());
		model.setStatus(soapModel.getStatus());

		return model;
	}

	/**
	 * Converts the soap model instances into normal model instances.
	 *
	 * @param soapModels the soap model instances to convert
	 * @return the normal model instances
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static List<BatchPlannerLog> toModels(
		BatchPlannerLogSoap[] soapModels) {

		if (soapModels == null) {
			return null;
		}

		List<BatchPlannerLog> models = new ArrayList<BatchPlannerLog>(
			soapModels.length);

		for (BatchPlannerLogSoap soapModel : soapModels) {
			models.add(toModel(soapModel));
		}

		return models;
	}

	public BatchPlannerLogModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _batchPlannerLogId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setBatchPlannerLogId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _batchPlannerLogId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return BatchPlannerLog.class;
	}

	@Override
	public String getModelClassName() {
		return BatchPlannerLog.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		Map<String, Function<BatchPlannerLog, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		for (Map.Entry<String, Function<BatchPlannerLog, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<BatchPlannerLog, Object> attributeGetterFunction =
				entry.getValue();

			attributes.put(
				attributeName,
				attributeGetterFunction.apply((BatchPlannerLog)this));
		}

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Map<String, BiConsumer<BatchPlannerLog, Object>>
			attributeSetterBiConsumers = getAttributeSetterBiConsumers();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();

			BiConsumer<BatchPlannerLog, Object> attributeSetterBiConsumer =
				attributeSetterBiConsumers.get(attributeName);

			if (attributeSetterBiConsumer != null) {
				attributeSetterBiConsumer.accept(
					(BatchPlannerLog)this, entry.getValue());
			}
		}
	}

	public Map<String, Function<BatchPlannerLog, Object>>
		getAttributeGetterFunctions() {

		return _attributeGetterFunctions;
	}

	public Map<String, BiConsumer<BatchPlannerLog, Object>>
		getAttributeSetterBiConsumers() {

		return _attributeSetterBiConsumers;
	}

	private static Function<InvocationHandler, BatchPlannerLog>
		_getProxyProviderFunction() {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			BatchPlannerLog.class.getClassLoader(), BatchPlannerLog.class,
			ModelWrapper.class);

		try {
			Constructor<BatchPlannerLog> constructor =
				(Constructor<BatchPlannerLog>)proxyClass.getConstructor(
					InvocationHandler.class);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new InternalError(reflectiveOperationException);
				}
			};
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new InternalError(noSuchMethodException);
		}
	}

	private static final Map<String, Function<BatchPlannerLog, Object>>
		_attributeGetterFunctions;
	private static final Map<String, BiConsumer<BatchPlannerLog, Object>>
		_attributeSetterBiConsumers;

	static {
		Map<String, Function<BatchPlannerLog, Object>>
			attributeGetterFunctions =
				new LinkedHashMap<String, Function<BatchPlannerLog, Object>>();
		Map<String, BiConsumer<BatchPlannerLog, ?>> attributeSetterBiConsumers =
			new LinkedHashMap<String, BiConsumer<BatchPlannerLog, ?>>();

		attributeGetterFunctions.put(
			"mvccVersion", BatchPlannerLog::getMvccVersion);
		attributeSetterBiConsumers.put(
			"mvccVersion",
			(BiConsumer<BatchPlannerLog, Long>)BatchPlannerLog::setMvccVersion);
		attributeGetterFunctions.put(
			"batchPlannerLogId", BatchPlannerLog::getBatchPlannerLogId);
		attributeSetterBiConsumers.put(
			"batchPlannerLogId",
			(BiConsumer<BatchPlannerLog, Long>)
				BatchPlannerLog::setBatchPlannerLogId);
		attributeGetterFunctions.put(
			"companyId", BatchPlannerLog::getCompanyId);
		attributeSetterBiConsumers.put(
			"companyId",
			(BiConsumer<BatchPlannerLog, Long>)BatchPlannerLog::setCompanyId);
		attributeGetterFunctions.put("userId", BatchPlannerLog::getUserId);
		attributeSetterBiConsumers.put(
			"userId",
			(BiConsumer<BatchPlannerLog, Long>)BatchPlannerLog::setUserId);
		attributeGetterFunctions.put("userName", BatchPlannerLog::getUserName);
		attributeSetterBiConsumers.put(
			"userName",
			(BiConsumer<BatchPlannerLog, String>)BatchPlannerLog::setUserName);
		attributeGetterFunctions.put(
			"createDate", BatchPlannerLog::getCreateDate);
		attributeSetterBiConsumers.put(
			"createDate",
			(BiConsumer<BatchPlannerLog, Date>)BatchPlannerLog::setCreateDate);
		attributeGetterFunctions.put(
			"modifiedDate", BatchPlannerLog::getModifiedDate);
		attributeSetterBiConsumers.put(
			"modifiedDate",
			(BiConsumer<BatchPlannerLog, Date>)
				BatchPlannerLog::setModifiedDate);
		attributeGetterFunctions.put(
			"batchPlannerPlanId", BatchPlannerLog::getBatchPlannerPlanId);
		attributeSetterBiConsumers.put(
			"batchPlannerPlanId",
			(BiConsumer<BatchPlannerLog, Long>)
				BatchPlannerLog::setBatchPlannerPlanId);
		attributeGetterFunctions.put("size", BatchPlannerLog::getSize);
		attributeSetterBiConsumers.put(
			"size",
			(BiConsumer<BatchPlannerLog, Integer>)BatchPlannerLog::setSize);
		attributeGetterFunctions.put("total", BatchPlannerLog::getTotal);
		attributeSetterBiConsumers.put(
			"total",
			(BiConsumer<BatchPlannerLog, Integer>)BatchPlannerLog::setTotal);
		attributeGetterFunctions.put("status", BatchPlannerLog::getStatus);
		attributeSetterBiConsumers.put(
			"status",
			(BiConsumer<BatchPlannerLog, Integer>)BatchPlannerLog::setStatus);

		_attributeGetterFunctions = Collections.unmodifiableMap(
			attributeGetterFunctions);
		_attributeSetterBiConsumers = Collections.unmodifiableMap(
			(Map)attributeSetterBiConsumers);
	}

	@JSON
	@Override
	public long getMvccVersion() {
		return _mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_mvccVersion = mvccVersion;
	}

	@JSON
	@Override
	public long getBatchPlannerLogId() {
		return _batchPlannerLogId;
	}

	@Override
	public void setBatchPlannerLogId(long batchPlannerLogId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_batchPlannerLogId = batchPlannerLogId;
	}

	@JSON
	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_companyId = companyId;
	}

	@JSON
	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_userId = userId;
	}

	@Override
	public String getUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getUserId());

			return user.getUuid();
		}
		catch (PortalException portalException) {
			return "";
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	@JSON
	@Override
	public String getUserName() {
		if (_userName == null) {
			return "";
		}
		else {
			return _userName;
		}
	}

	@Override
	public void setUserName(String userName) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_userName = userName;
	}

	@JSON
	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_createDate = createDate;
	}

	@JSON
	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public boolean hasSetModifiedDate() {
		return _setModifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_setModifiedDate = true;

		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_modifiedDate = modifiedDate;
	}

	@JSON
	@Override
	public long getBatchPlannerPlanId() {
		return _batchPlannerPlanId;
	}

	@Override
	public void setBatchPlannerPlanId(long batchPlannerPlanId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_batchPlannerPlanId = batchPlannerPlanId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalBatchPlannerPlanId() {
		return GetterUtil.getLong(
			this.<Long>getColumnOriginalValue("batchPlannerPlanId"));
	}

	@JSON
	@Override
	public int getSize() {
		return _size;
	}

	@Override
	public void setSize(int size) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_size = size;
	}

	@JSON
	@Override
	public int getTotal() {
		return _total;
	}

	@Override
	public void setTotal(int total) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_total = total;
	}

	@JSON
	@Override
	public int getStatus() {
		return _status;
	}

	@Override
	public void setStatus(int status) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_status = status;
	}

	public long getColumnBitmask() {
		if (_columnBitmask > 0) {
			return _columnBitmask;
		}

		if ((_columnOriginalValues == null) ||
			(_columnOriginalValues == Collections.EMPTY_MAP)) {

			return 0;
		}

		for (Map.Entry<String, Object> entry :
				_columnOriginalValues.entrySet()) {

			if (!Objects.equals(
					entry.getValue(), getColumnValue(entry.getKey()))) {

				_columnBitmask |= _columnBitmasks.get(entry.getKey());
			}
		}

		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(
			getCompanyId(), BatchPlannerLog.class.getName(), getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public BatchPlannerLog toEscapedModel() {
		if (_escapedModel == null) {
			Function<InvocationHandler, BatchPlannerLog>
				escapedModelProxyProviderFunction =
					EscapedModelProxyProviderFunctionHolder.
						_escapedModelProxyProviderFunction;

			_escapedModel = escapedModelProxyProviderFunction.apply(
				new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		BatchPlannerLogImpl batchPlannerLogImpl = new BatchPlannerLogImpl();

		batchPlannerLogImpl.setMvccVersion(getMvccVersion());
		batchPlannerLogImpl.setBatchPlannerLogId(getBatchPlannerLogId());
		batchPlannerLogImpl.setCompanyId(getCompanyId());
		batchPlannerLogImpl.setUserId(getUserId());
		batchPlannerLogImpl.setUserName(getUserName());
		batchPlannerLogImpl.setCreateDate(getCreateDate());
		batchPlannerLogImpl.setModifiedDate(getModifiedDate());
		batchPlannerLogImpl.setBatchPlannerPlanId(getBatchPlannerPlanId());
		batchPlannerLogImpl.setSize(getSize());
		batchPlannerLogImpl.setTotal(getTotal());
		batchPlannerLogImpl.setStatus(getStatus());

		batchPlannerLogImpl.resetOriginalValues();

		return batchPlannerLogImpl;
	}

	@Override
	public int compareTo(BatchPlannerLog batchPlannerLog) {
		int value = 0;

		value = DateUtil.compareTo(
			getModifiedDate(), batchPlannerLog.getModifiedDate());

		value = value * -1;

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BatchPlannerLog)) {
			return false;
		}

		BatchPlannerLog batchPlannerLog = (BatchPlannerLog)object;

		long primaryKey = batchPlannerLog.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isEntityCacheEnabled() {
		return true;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isFinderCacheEnabled() {
		return true;
	}

	@Override
	public void resetOriginalValues() {
		_columnOriginalValues = Collections.emptyMap();

		_setModifiedDate = false;

		_columnBitmask = 0;
	}

	@Override
	public CacheModel<BatchPlannerLog> toCacheModel() {
		BatchPlannerLogCacheModel batchPlannerLogCacheModel =
			new BatchPlannerLogCacheModel();

		batchPlannerLogCacheModel.mvccVersion = getMvccVersion();

		batchPlannerLogCacheModel.batchPlannerLogId = getBatchPlannerLogId();

		batchPlannerLogCacheModel.companyId = getCompanyId();

		batchPlannerLogCacheModel.userId = getUserId();

		batchPlannerLogCacheModel.userName = getUserName();

		String userName = batchPlannerLogCacheModel.userName;

		if ((userName != null) && (userName.length() == 0)) {
			batchPlannerLogCacheModel.userName = null;
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			batchPlannerLogCacheModel.createDate = createDate.getTime();
		}
		else {
			batchPlannerLogCacheModel.createDate = Long.MIN_VALUE;
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			batchPlannerLogCacheModel.modifiedDate = modifiedDate.getTime();
		}
		else {
			batchPlannerLogCacheModel.modifiedDate = Long.MIN_VALUE;
		}

		batchPlannerLogCacheModel.batchPlannerPlanId = getBatchPlannerPlanId();

		batchPlannerLogCacheModel.size = getSize();

		batchPlannerLogCacheModel.total = getTotal();

		batchPlannerLogCacheModel.status = getStatus();

		return batchPlannerLogCacheModel;
	}

	@Override
	public String toString() {
		Map<String, Function<BatchPlannerLog, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			(4 * attributeGetterFunctions.size()) + 2);

		sb.append("{");

		for (Map.Entry<String, Function<BatchPlannerLog, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<BatchPlannerLog, Object> attributeGetterFunction =
				entry.getValue();

			sb.append(attributeName);
			sb.append("=");
			sb.append(attributeGetterFunction.apply((BatchPlannerLog)this));
			sb.append(", ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		Map<String, Function<BatchPlannerLog, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			(5 * attributeGetterFunctions.size()) + 4);

		sb.append("<model><model-name>");
		sb.append(getModelClassName());
		sb.append("</model-name>");

		for (Map.Entry<String, Function<BatchPlannerLog, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<BatchPlannerLog, Object> attributeGetterFunction =
				entry.getValue();

			sb.append("<column><column-name>");
			sb.append(attributeName);
			sb.append("</column-name><column-value><![CDATA[");
			sb.append(attributeGetterFunction.apply((BatchPlannerLog)this));
			sb.append("]]></column-value></column>");
		}

		sb.append("</model>");

		return sb.toString();
	}

	private static class EscapedModelProxyProviderFunctionHolder {

		private static final Function<InvocationHandler, BatchPlannerLog>
			_escapedModelProxyProviderFunction = _getProxyProviderFunction();

	}

	private long _mvccVersion;
	private long _batchPlannerLogId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private boolean _setModifiedDate;
	private long _batchPlannerPlanId;
	private int _size;
	private int _total;
	private int _status;

	public <T> T getColumnValue(String columnName) {
		columnName = _attributeNames.getOrDefault(columnName, columnName);

		Function<BatchPlannerLog, Object> function =
			_attributeGetterFunctions.get(columnName);

		if (function == null) {
			throw new IllegalArgumentException(
				"No attribute getter function found for " + columnName);
		}

		return (T)function.apply((BatchPlannerLog)this);
	}

	public <T> T getColumnOriginalValue(String columnName) {
		if (_columnOriginalValues == null) {
			return null;
		}

		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		return (T)_columnOriginalValues.get(columnName);
	}

	private void _setColumnOriginalValues() {
		_columnOriginalValues = new HashMap<String, Object>();

		_columnOriginalValues.put("mvccVersion", _mvccVersion);
		_columnOriginalValues.put("batchPlannerLogId", _batchPlannerLogId);
		_columnOriginalValues.put("companyId", _companyId);
		_columnOriginalValues.put("userId", _userId);
		_columnOriginalValues.put("userName", _userName);
		_columnOriginalValues.put("createDate", _createDate);
		_columnOriginalValues.put("modifiedDate", _modifiedDate);
		_columnOriginalValues.put("batchPlannerPlanId", _batchPlannerPlanId);
		_columnOriginalValues.put("size_", _size);
		_columnOriginalValues.put("total", _total);
		_columnOriginalValues.put("status", _status);
	}

	private static final Map<String, String> _attributeNames;

	static {
		Map<String, String> attributeNames = new HashMap<>();

		attributeNames.put("size_", "size");

		_attributeNames = Collections.unmodifiableMap(attributeNames);
	}

	private transient Map<String, Object> _columnOriginalValues;

	public static long getColumnBitmask(String columnName) {
		return _columnBitmasks.get(columnName);
	}

	private static final Map<String, Long> _columnBitmasks;

	static {
		Map<String, Long> columnBitmasks = new HashMap<>();

		columnBitmasks.put("mvccVersion", 1L);

		columnBitmasks.put("batchPlannerLogId", 2L);

		columnBitmasks.put("companyId", 4L);

		columnBitmasks.put("userId", 8L);

		columnBitmasks.put("userName", 16L);

		columnBitmasks.put("createDate", 32L);

		columnBitmasks.put("modifiedDate", 64L);

		columnBitmasks.put("batchPlannerPlanId", 128L);

		columnBitmasks.put("size_", 256L);

		columnBitmasks.put("total", 512L);

		columnBitmasks.put("status", 1024L);

		_columnBitmasks = Collections.unmodifiableMap(columnBitmasks);
	}

	private long _columnBitmask;
	private BatchPlannerLog _escapedModel;

}