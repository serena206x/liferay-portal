/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.announcements.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.service.persistence.PersistenceExecutionTestListener;
import com.liferay.portal.test.ExecutionTestListeners;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.util.PropsValues;

import com.liferay.portlet.announcements.NoSuchDeliveryException;
import com.liferay.portlet.announcements.model.AnnouncementsDelivery;
import com.liferay.portlet.announcements.model.impl.AnnouncementsDeliveryModelImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
@ExecutionTestListeners(listeners =  {
	PersistenceExecutionTestListener.class})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class AnnouncementsDeliveryPersistenceTest {
	@Before
	public void setUp() throws Exception {
		_persistence = (AnnouncementsDeliveryPersistence)PortalBeanLocatorUtil.locate(AnnouncementsDeliveryPersistence.class.getName());
	}

	@Test
	public void testCreate() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		AnnouncementsDelivery announcementsDelivery = _persistence.create(pk);

		Assert.assertNotNull(announcementsDelivery);

		Assert.assertEquals(announcementsDelivery.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery = addAnnouncementsDelivery();

		_persistence.remove(newAnnouncementsDelivery);

		AnnouncementsDelivery existingAnnouncementsDelivery = _persistence.fetchByPrimaryKey(newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertNull(existingAnnouncementsDelivery);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAnnouncementsDelivery();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		AnnouncementsDelivery newAnnouncementsDelivery = _persistence.create(pk);

		newAnnouncementsDelivery.setCompanyId(ServiceTestUtil.nextLong());

		newAnnouncementsDelivery.setUserId(ServiceTestUtil.nextLong());

		newAnnouncementsDelivery.setType(ServiceTestUtil.randomString());

		newAnnouncementsDelivery.setEmail(ServiceTestUtil.randomBoolean());

		newAnnouncementsDelivery.setSms(ServiceTestUtil.randomBoolean());

		newAnnouncementsDelivery.setWebsite(ServiceTestUtil.randomBoolean());

		_persistence.update(newAnnouncementsDelivery, false);

		AnnouncementsDelivery existingAnnouncementsDelivery = _persistence.findByPrimaryKey(newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsDelivery.getDeliveryId(),
			newAnnouncementsDelivery.getDeliveryId());
		Assert.assertEquals(existingAnnouncementsDelivery.getCompanyId(),
			newAnnouncementsDelivery.getCompanyId());
		Assert.assertEquals(existingAnnouncementsDelivery.getUserId(),
			newAnnouncementsDelivery.getUserId());
		Assert.assertEquals(existingAnnouncementsDelivery.getType(),
			newAnnouncementsDelivery.getType());
		Assert.assertEquals(existingAnnouncementsDelivery.getEmail(),
			newAnnouncementsDelivery.getEmail());
		Assert.assertEquals(existingAnnouncementsDelivery.getSms(),
			newAnnouncementsDelivery.getSms());
		Assert.assertEquals(existingAnnouncementsDelivery.getWebsite(),
			newAnnouncementsDelivery.getWebsite());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery = addAnnouncementsDelivery();

		AnnouncementsDelivery existingAnnouncementsDelivery = _persistence.findByPrimaryKey(newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsDelivery,
			newAnnouncementsDelivery);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail("Missing entity did not throw NoSuchDeliveryException");
		}
		catch (NoSuchDeliveryException nsee) {
		}
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery = addAnnouncementsDelivery();

		AnnouncementsDelivery existingAnnouncementsDelivery = _persistence.fetchByPrimaryKey(newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsDelivery,
			newAnnouncementsDelivery);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		AnnouncementsDelivery missingAnnouncementsDelivery = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAnnouncementsDelivery);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery = addAnnouncementsDelivery();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(AnnouncementsDelivery.class,
				AnnouncementsDelivery.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("deliveryId",
				newAnnouncementsDelivery.getDeliveryId()));

		List<AnnouncementsDelivery> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		AnnouncementsDelivery existingAnnouncementsDelivery = result.get(0);

		Assert.assertEquals(existingAnnouncementsDelivery,
			newAnnouncementsDelivery);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(AnnouncementsDelivery.class,
				AnnouncementsDelivery.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("deliveryId",
				ServiceTestUtil.nextLong()));

		List<AnnouncementsDelivery> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery = addAnnouncementsDelivery();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(AnnouncementsDelivery.class,
				AnnouncementsDelivery.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("deliveryId"));

		Object newDeliveryId = newAnnouncementsDelivery.getDeliveryId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("deliveryId",
				new Object[] { newDeliveryId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDeliveryId = result.get(0);

		Assert.assertEquals(existingDeliveryId, newDeliveryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(AnnouncementsDelivery.class,
				AnnouncementsDelivery.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("deliveryId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("deliveryId",
				new Object[] { ServiceTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		if (!PropsValues.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			return;
		}

		AnnouncementsDelivery newAnnouncementsDelivery = addAnnouncementsDelivery();

		_persistence.clearCache();

		AnnouncementsDeliveryModelImpl existingAnnouncementsDeliveryModelImpl = (AnnouncementsDeliveryModelImpl)_persistence.findByPrimaryKey(newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsDeliveryModelImpl.getUserId(),
			existingAnnouncementsDeliveryModelImpl.getOriginalUserId());
		Assert.assertTrue(Validator.equals(
				existingAnnouncementsDeliveryModelImpl.getType(),
				existingAnnouncementsDeliveryModelImpl.getOriginalType()));
	}

	protected AnnouncementsDelivery addAnnouncementsDelivery()
		throws Exception {
		long pk = ServiceTestUtil.nextLong();

		AnnouncementsDelivery announcementsDelivery = _persistence.create(pk);

		announcementsDelivery.setCompanyId(ServiceTestUtil.nextLong());

		announcementsDelivery.setUserId(ServiceTestUtil.nextLong());

		announcementsDelivery.setType(ServiceTestUtil.randomString());

		announcementsDelivery.setEmail(ServiceTestUtil.randomBoolean());

		announcementsDelivery.setSms(ServiceTestUtil.randomBoolean());

		announcementsDelivery.setWebsite(ServiceTestUtil.randomBoolean());

		_persistence.update(announcementsDelivery, false);

		return announcementsDelivery;
	}

	private AnnouncementsDeliveryPersistence _persistence;
}