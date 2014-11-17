/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.security;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * Security interface. This interface defines security-related methods.
 */
public interface Security {

    public Delegator getDelegator();

    public void setDelegator(Delegator delegator);

    /**
     * Uses userLoginSecurityGroupByUserLoginId cache to speed up the finding of the userLogin's security group list.
     *
     * @param userLoginId The userLoginId to find security groups by
     * @return An iterator made from the Collection either cached or retrieved from the database through the
     *            UserLoginSecurityGroup Delegator.
     */
    public Iterator<GenericValue> findUserLoginSecurityGroupByUserLoginId(String userLoginId);

    /**
     * Finds whether or not a SecurityGroupPermission row exists given a groupId and permission.
     * The groupId,permission pair is cached instead of the userLoginId,permission pair to keep the cache small and to
     * make it more changeable.
     *
     * @param groupId The ID of the group
     * @param permission The name of the permission
     * @return boolean specifying whether or not a SecurityGroupPermission row exists
     */
    public boolean securityGroupPermissionExists(String groupId, String permission);

    /**
     * Checks to see if the currently logged in userLogin has the passed permission.
     *
     * @param permission Name of the permission to check.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasPermission(String permission, HttpSession session);

    /**
     * Checks to see if the userLogin has the passed permission.
     *
     * @param permission Name of the permission to check.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasPermission(String permission, GenericValue userLogin);

    /**
     * Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the
     * specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
     *
     * @param entity The name of the Entity corresponding to the desired permission.
     * @param action The action on the Entity corresponding to the desired permission.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasEntityPermission(String entity, String action, HttpSession session);

    /**
     * Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the
     * specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
     *
     * @param entity The name of the Entity corresponding to the desired permission.
     * @param action The action on the Entity corresponding to the desired permission.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasEntityPermission(String entity, String action, GenericValue userLogin);

    /**
     * Like hasEntityPermission above, this checks the specified action, as well as for "_ADMIN" to allow for simplified
     * general administration permission, but also checks action_ROLE and validates the user is a member for the
     * application.
     *
     * @param application The name of the application corresponding to the desired permission.
     * @param action The action on the application corresponding to the desired permission.
     * @param primaryKey The primary key for the role check.
     * @param role The roleTypeId which the user must validate with.
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasRolePermission(String application, String action, String primaryKey, String role, HttpSession session);

    /**
     * Like hasEntityPermission above, this checks the specified action, as well as for "_ADMIN" to allow for simplified
     * general administration permission, but also checks action_ROLE and validates the user is a member for the
     * application.
     *
     * @param application The name of the application corresponding to the desired permission.
     * @param action The action on the application corresponding to the desired permission.
     * @param primaryKey The primary key for the role check.
     * @param role The roleTypeId which the user must validate with.
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasRolePermission(String application, String action, String primaryKey, String role, GenericValue userLogin);

    /**
     * Like hasEntityPermission above, this checks the specified action, as well as for "_ADMIN" to allow for simplified
     * general administration permission, but also checks action_ROLE and validates the user is a member for the
     * application.
     *
     * @param application The name of the application corresponding to the desired permission.
     * @param action The action on the application corresponding to the desired permission.
     * @param primaryKey The primary key for the role check.
     * @param roles List of roleTypeId of which the user must validate with (ORed).
     * @param userLogin The userLogin object for user to check against.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasRolePermission(String application, String action, String primaryKey, List<String> roles, GenericValue userLogin);

    /**
     * Like hasEntityPermission above, this checks the specified action, as well as for "_ADMIN" to allow for simplified
     * general administration permission, but also checks action_ROLE and validates the user is a member for the
     * application.
     *
     * @param application The name of the application corresponding to the desired permission.
     * @param action The action on the application corresponding to the desired permission.
     * @param primaryKey The primary key for the role check.
     * @param roles List of roleTypeId of which the user must validate with (ORed).
     * @param session The current HTTP session, contains the logged in userLogin as an attribute.
     * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
     */
    public boolean hasRolePermission(String application, String action, String primaryKey, List<String> roles, HttpSession session);

    /** Clears any user-related cached data. This method is called by the framework
     *  to indicate a user has logged out. Implementations should clear any cached
     *  data related to the user.
     * @param userLogin The user login to be cleared
     */
    public void clearUserData(GenericValue userLogin);
}
