/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.DuplicateUserGroupException;
import com.liferay.portal.NoSuchUserGroupException;
import com.liferay.portal.PortalException;
import com.liferay.portal.RequiredUserGroupException;
import com.liferay.portal.SystemException;
import com.liferay.portal.UserGroupNameException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.model.UserGroupConstants;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.portal.service.base.UserGroupLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <a href="UserGroupLocalServiceImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Charles May
 */
public class UserGroupLocalServiceImpl extends UserGroupLocalServiceBaseImpl {

	public void addGroupUserGroups(long groupId, long[] userGroupIds)
		throws SystemException {

		groupPersistence.addUserGroups(groupId, userGroupIds);

		PermissionCacheUtil.clearCache();
	}

	public UserGroup addUserGroup(
			long userId, long companyId, String name, String description)
		throws PortalException, SystemException {

		// User Group

		validate(0, companyId, name);

		long userGroupId = counterLocalService.increment();

		UserGroup userGroup = userGroupPersistence.create(userGroupId);

		userGroup.setCompanyId(companyId);
		userGroup.setParentUserGroupId(
			UserGroupConstants.DEFAULT_PARENT_USER_GROUP_ID);
		userGroup.setName(name);
		userGroup.setDescription(description);

		userGroupPersistence.update(userGroup, false);

		// Group

		groupLocalService.addGroup(
			userId, UserGroup.class.getName(), userGroup.getUserGroupId(),
			String.valueOf(userGroupId), null, 0, null, true, null);

		// Resources

		resourceLocalService.addResources(
			companyId, 0, userId, UserGroup.class.getName(),
			userGroup.getUserGroupId(), false, false, false);

		return userGroup;
	}

	public void clearUserUserGroups(long userId) throws SystemException {
		userPersistence.clearUserGroups(userId);

		PermissionCacheUtil.clearCache();
	}

	public void deleteUserGroup(long userGroupId)
		throws PortalException, SystemException {

		UserGroup userGroup = userGroupPersistence.findByPrimaryKey(
			userGroupId);

		if (userLocalService.getUserGroupUsersCount(userGroupId, true) > 0) {
			throw new RequiredUserGroupException();
		}

		// Users

		clearUserUserGroups(userGroupId);

		// Group

		Group group = userGroup.getGroup();

		groupLocalService.deleteGroup(group.getGroupId());

		// Resources

		resourceLocalService.deleteResource(
			userGroup.getCompanyId(), UserGroup.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, userGroup.getUserGroupId());

		// User Group

		userGroupPersistence.remove(userGroupId);

		// Permission cache

		PermissionCacheUtil.clearCache();
	}

	public UserGroup getUserGroup(long userGroupId)
		throws PortalException, SystemException {

		return userGroupPersistence.findByPrimaryKey(userGroupId);
	}

	public UserGroup getUserGroup(long companyId, String name)
		throws PortalException, SystemException {

		return userGroupPersistence.findByC_N(companyId, name);
	}

	public List<UserGroup> getUserGroups(long companyId)
		throws SystemException {

		return userGroupPersistence.findByCompanyId(companyId);
	}

	public List<UserGroup> getUserGroups(long[] userGroupIds)
		throws PortalException, SystemException {

		List<UserGroup> userGroups = new ArrayList<UserGroup>(
			userGroupIds.length);

		for (long userGroupId : userGroupIds) {
			UserGroup userGroup = getUserGroup(userGroupId);

			userGroups.add(userGroup);
		}

		return userGroups;
	}

	public List<UserGroup> getUserUserGroups(long userId)
		throws SystemException {

		return userPersistence.getUserGroups(userId);
	}

	public boolean hasGroupUserGroup(long groupId, long userGroupId)
		throws SystemException {

		return groupPersistence.containsUserGroup(groupId, userGroupId);
	}

	public List<UserGroup> search(
			long companyId, String name, String description,
			LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator obc)
		throws SystemException {

		return userGroupFinder.findByC_N_D(
			companyId, name, description, params, start, end, obc);
	}

	public int searchCount(
			long companyId, String name, String description,
			LinkedHashMap<String, Object> params)
		throws SystemException {

		return userGroupFinder.countByC_N_D(
			companyId, name, description, params);
	}

	public void unsetGroupUserGroups(long groupId, long[] userGroupIds)
		throws SystemException {

		groupPersistence.removeUserGroups(groupId, userGroupIds);

		PermissionCacheUtil.clearCache();
	}

	public UserGroup updateUserGroup(
			long companyId, long userGroupId, String name,
			String description)
		throws PortalException, SystemException {

		validate(userGroupId, companyId, name);

		UserGroup userGroup = userGroupPersistence.findByPrimaryKey(
			userGroupId);

		userGroup.setName(name);
		userGroup.setDescription(description);

		userGroupPersistence.update(userGroup, false);

		return userGroup;
	}

	protected void validate(long userGroupId, long companyId, String name)
		throws PortalException, SystemException {

		if ((Validator.isNull(name)) || (Validator.isNumber(name)) ||
			(name.indexOf(StringPool.COMMA) != -1) ||
			(name.indexOf(StringPool.STAR) != -1)) {

			throw new UserGroupNameException();
		}

		try {
			UserGroup userGroup = userGroupFinder.findByC_N(companyId, name);

			if (userGroup.getUserGroupId() != userGroupId) {
				throw new DuplicateUserGroupException();
			}
		}
		catch (NoSuchUserGroupException nsuge) {
		}
	}

}