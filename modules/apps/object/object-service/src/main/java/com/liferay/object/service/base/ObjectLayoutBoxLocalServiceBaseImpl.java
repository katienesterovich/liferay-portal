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

package com.liferay.object.service.base;

import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.service.ObjectLayoutBoxLocalService;
import com.liferay.object.service.ObjectLayoutBoxLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.Serializable;

import java.lang.reflect.Field;

import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the object layout box local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.object.service.impl.ObjectLayoutBoxLocalServiceImpl}.
 * </p>
 *
 * @author Marco Leo
 * @see com.liferay.object.service.impl.ObjectLayoutBoxLocalServiceImpl
 * @generated
 */
public abstract class ObjectLayoutBoxLocalServiceBaseImpl
	extends BaseLocalServiceImpl
	implements AopService, IdentifiableOSGiService,
			   ObjectLayoutBoxLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>ObjectLayoutBoxLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>ObjectLayoutBoxLocalServiceUtil</code>.
	 */

	/**
	 * Adds the object layout box to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutBoxLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutBox the object layout box
	 * @return the object layout box that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectLayoutBox addObjectLayoutBox(ObjectLayoutBox objectLayoutBox) {
		objectLayoutBox.setNew(true);

		return objectLayoutBoxPersistence.update(objectLayoutBox);
	}

	/**
	 * Creates a new object layout box with the primary key. Does not add the object layout box to the database.
	 *
	 * @param objectLayoutBoxId the primary key for the new object layout box
	 * @return the new object layout box
	 */
	@Override
	@Transactional(enabled = false)
	public ObjectLayoutBox createObjectLayoutBox(long objectLayoutBoxId) {
		return objectLayoutBoxPersistence.create(objectLayoutBoxId);
	}

	/**
	 * Deletes the object layout box with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutBoxLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutBoxId the primary key of the object layout box
	 * @return the object layout box that was removed
	 * @throws PortalException if a object layout box with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public ObjectLayoutBox deleteObjectLayoutBox(long objectLayoutBoxId)
		throws PortalException {

		return objectLayoutBoxPersistence.remove(objectLayoutBoxId);
	}

	/**
	 * Deletes the object layout box from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutBoxLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutBox the object layout box
	 * @return the object layout box that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public ObjectLayoutBox deleteObjectLayoutBox(
		ObjectLayoutBox objectLayoutBox) {

		return objectLayoutBoxPersistence.remove(objectLayoutBox);
	}

	@Override
	public <T> T dslQuery(DSLQuery dslQuery) {
		return objectLayoutBoxPersistence.dslQuery(dslQuery);
	}

	@Override
	public DynamicQuery dynamicQuery() {
		Class<?> clazz = getClass();

		return DynamicQueryFactoryUtil.forClass(
			ObjectLayoutBox.class, clazz.getClassLoader());
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return objectLayoutBoxPersistence.findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutBoxModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return objectLayoutBoxPersistence.findWithDynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutBoxModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return objectLayoutBoxPersistence.findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return objectLayoutBoxPersistence.countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection) {

		return objectLayoutBoxPersistence.countWithDynamicQuery(
			dynamicQuery, projection);
	}

	@Override
	public ObjectLayoutBox fetchObjectLayoutBox(long objectLayoutBoxId) {
		return objectLayoutBoxPersistence.fetchByPrimaryKey(objectLayoutBoxId);
	}

	/**
	 * Returns the object layout box with the matching UUID and company.
	 *
	 * @param uuid the object layout box's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout box, or <code>null</code> if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox fetchObjectLayoutBoxByUuidAndCompanyId(
		String uuid, long companyId) {

		return objectLayoutBoxPersistence.fetchByUuid_C_First(
			uuid, companyId, null);
	}

	/**
	 * Returns the object layout box with the primary key.
	 *
	 * @param objectLayoutBoxId the primary key of the object layout box
	 * @return the object layout box
	 * @throws PortalException if a object layout box with the primary key could not be found
	 */
	@Override
	public ObjectLayoutBox getObjectLayoutBox(long objectLayoutBoxId)
		throws PortalException {

		return objectLayoutBoxPersistence.findByPrimaryKey(objectLayoutBoxId);
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			new DefaultActionableDynamicQuery();

		actionableDynamicQuery.setBaseLocalService(objectLayoutBoxLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(ObjectLayoutBox.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("objectLayoutBoxId");

		return actionableDynamicQuery;
	}

	@Override
	public IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			new IndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setBaseLocalService(
			objectLayoutBoxLocalService);
		indexableActionableDynamicQuery.setClassLoader(getClassLoader());
		indexableActionableDynamicQuery.setModelClass(ObjectLayoutBox.class);

		indexableActionableDynamicQuery.setPrimaryKeyPropertyName(
			"objectLayoutBoxId");

		return indexableActionableDynamicQuery;
	}

	protected void initActionableDynamicQuery(
		ActionableDynamicQuery actionableDynamicQuery) {

		actionableDynamicQuery.setBaseLocalService(objectLayoutBoxLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(ObjectLayoutBox.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("objectLayoutBoxId");
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		final PortletDataContext portletDataContext) {

		final ExportActionableDynamicQuery exportActionableDynamicQuery =
			new ExportActionableDynamicQuery() {

				@Override
				public long performCount() throws PortalException {
					ManifestSummary manifestSummary =
						portletDataContext.getManifestSummary();

					StagedModelType stagedModelType = getStagedModelType();

					long modelAdditionCount = super.performCount();

					manifestSummary.addModelAdditionCount(
						stagedModelType, modelAdditionCount);

					long modelDeletionCount =
						ExportImportHelperUtil.getModelDeletionCount(
							portletDataContext, stagedModelType);

					manifestSummary.addModelDeletionCount(
						stagedModelType, modelDeletionCount);

					return modelAdditionCount;
				}

			};

		initActionableDynamicQuery(exportActionableDynamicQuery);

		exportActionableDynamicQuery.setAddCriteriaMethod(
			new ActionableDynamicQuery.AddCriteriaMethod() {

				@Override
				public void addCriteria(DynamicQuery dynamicQuery) {
					portletDataContext.addDateRangeCriteria(
						dynamicQuery, "modifiedDate");
				}

			});

		exportActionableDynamicQuery.setCompanyId(
			portletDataContext.getCompanyId());

		exportActionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ObjectLayoutBox>() {

				@Override
				public void performAction(ObjectLayoutBox objectLayoutBox)
					throws PortalException {

					StagedModelDataHandlerUtil.exportStagedModel(
						portletDataContext, objectLayoutBox);
				}

			});
		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(
				PortalUtil.getClassNameId(ObjectLayoutBox.class.getName())));

		return exportActionableDynamicQuery;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return objectLayoutBoxPersistence.create(
			((Long)primaryKeyObj).longValue());
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {

		return objectLayoutBoxLocalService.deleteObjectLayoutBox(
			(ObjectLayoutBox)persistedModel);
	}

	@Override
	public BasePersistence<ObjectLayoutBox> getBasePersistence() {
		return objectLayoutBoxPersistence;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return objectLayoutBoxPersistence.findByPrimaryKey(primaryKeyObj);
	}

	/**
	 * Returns the object layout box with the matching UUID and company.
	 *
	 * @param uuid the object layout box's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout box
	 * @throws PortalException if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox getObjectLayoutBoxByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return objectLayoutBoxPersistence.findByUuid_C_First(
			uuid, companyId, null);
	}

	/**
	 * Returns a range of all the object layout boxes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutBoxModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layout boxes
	 * @param end the upper bound of the range of object layout boxes (not inclusive)
	 * @return the range of object layout boxes
	 */
	@Override
	public List<ObjectLayoutBox> getObjectLayoutBoxes(int start, int end) {
		return objectLayoutBoxPersistence.findAll(start, end);
	}

	/**
	 * Returns the number of object layout boxes.
	 *
	 * @return the number of object layout boxes
	 */
	@Override
	public int getObjectLayoutBoxesCount() {
		return objectLayoutBoxPersistence.countAll();
	}

	/**
	 * Updates the object layout box in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutBoxLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutBox the object layout box
	 * @return the object layout box that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectLayoutBox updateObjectLayoutBox(
		ObjectLayoutBox objectLayoutBox) {

		return objectLayoutBoxPersistence.update(objectLayoutBox);
	}

	@Deactivate
	protected void deactivate() {
		_setLocalServiceUtilService(null);
	}

	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			ObjectLayoutBoxLocalService.class, IdentifiableOSGiService.class,
			PersistedModelLocalService.class
		};
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		objectLayoutBoxLocalService = (ObjectLayoutBoxLocalService)aopProxy;

		_setLocalServiceUtilService(objectLayoutBoxLocalService);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return ObjectLayoutBoxLocalService.class.getName();
	}

	protected Class<?> getModelClass() {
		return ObjectLayoutBox.class;
	}

	protected String getModelClassName() {
		return ObjectLayoutBox.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = objectLayoutBoxPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _setLocalServiceUtilService(
		ObjectLayoutBoxLocalService objectLayoutBoxLocalService) {

		try {
			Field field =
				ObjectLayoutBoxLocalServiceUtil.class.getDeclaredField(
					"_service");

			field.setAccessible(true);

			field.set(null, objectLayoutBoxLocalService);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	protected ObjectLayoutBoxLocalService objectLayoutBoxLocalService;

	@Reference
	protected ObjectLayoutBoxPersistence objectLayoutBoxPersistence;

	@Reference
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

}