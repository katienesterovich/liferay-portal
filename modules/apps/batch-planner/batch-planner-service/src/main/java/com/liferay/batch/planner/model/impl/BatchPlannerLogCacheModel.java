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
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing BatchPlannerLog in entity cache.
 *
 * @author Igor Beslic
 * @generated
 */
public class BatchPlannerLogCacheModel
	implements CacheModel<BatchPlannerLog>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BatchPlannerLogCacheModel)) {
			return false;
		}

		BatchPlannerLogCacheModel batchPlannerLogCacheModel =
			(BatchPlannerLogCacheModel)object;

		if ((batchPlannerLogId ==
				batchPlannerLogCacheModel.batchPlannerLogId) &&
			(mvccVersion == batchPlannerLogCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, batchPlannerLogId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(23);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", batchPlannerLogId=");
		sb.append(batchPlannerLogId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", batchPlannerPlanId=");
		sb.append(batchPlannerPlanId);
		sb.append(", size=");
		sb.append(size);
		sb.append(", total=");
		sb.append(total);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public BatchPlannerLog toEntityModel() {
		BatchPlannerLogImpl batchPlannerLogImpl = new BatchPlannerLogImpl();

		batchPlannerLogImpl.setMvccVersion(mvccVersion);
		batchPlannerLogImpl.setBatchPlannerLogId(batchPlannerLogId);
		batchPlannerLogImpl.setCompanyId(companyId);
		batchPlannerLogImpl.setUserId(userId);

		if (userName == null) {
			batchPlannerLogImpl.setUserName("");
		}
		else {
			batchPlannerLogImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			batchPlannerLogImpl.setCreateDate(null);
		}
		else {
			batchPlannerLogImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			batchPlannerLogImpl.setModifiedDate(null);
		}
		else {
			batchPlannerLogImpl.setModifiedDate(new Date(modifiedDate));
		}

		batchPlannerLogImpl.setBatchPlannerPlanId(batchPlannerPlanId);
		batchPlannerLogImpl.setSize(size);
		batchPlannerLogImpl.setTotal(total);
		batchPlannerLogImpl.setStatus(status);

		batchPlannerLogImpl.resetOriginalValues();

		return batchPlannerLogImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		batchPlannerLogId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		batchPlannerPlanId = objectInput.readLong();

		size = objectInput.readInt();

		total = objectInput.readInt();

		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(batchPlannerLogId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(batchPlannerPlanId);

		objectOutput.writeInt(size);

		objectOutput.writeInt(total);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long batchPlannerLogId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long batchPlannerPlanId;
	public int size;
	public int total;
	public int status;

}