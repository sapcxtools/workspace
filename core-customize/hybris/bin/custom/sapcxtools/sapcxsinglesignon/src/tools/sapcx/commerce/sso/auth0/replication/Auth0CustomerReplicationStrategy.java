package tools.sapcx.commerce.sso.auth0.replication;

import javax.annotation.Nonnull;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.roles.Role;
import com.auth0.json.mgmt.users.User;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.sapcx.commerce.sso.auth0.actions.Actions;
import tools.sapcx.commerce.sso.replication.CustomerReplicationStrategy;

public class Auth0CustomerReplicationStrategy implements CustomerReplicationStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(Auth0CustomerReplicationStrategy.class);

	private UserService userService;
	private String auth0RoleForCustomers;
	private boolean isReplicationEnabled;
	private boolean isRemovalEnabled;

	public Auth0CustomerReplicationStrategy(
			UserService userService,
			String auth0RoleForCustomers,
			boolean isReplicationEnabled,
			boolean isRemovalEnabled) {
		this.userService = userService;
		this.auth0RoleForCustomers = auth0RoleForCustomers;
		this.isReplicationEnabled = isReplicationEnabled;
		this.isRemovalEnabled = isRemovalEnabled;
	}

	@Override
	public void replicate(@Nonnull CustomerModel customer) {
		if (!isReplicationEnabled) {
			LOG.debug("Customer replication is disabled by configuration.");
			return;
		}

		if (userService.isAnonymousUser(customer)) {
			LOG.debug("Anonymous user replication is disabled by convention.");
			return;
		}

		User user = updateUser(customer);
		updateUserRoles(user, !customer.isLoginDisabled());
	}

	@Override
	public void remove(@Nonnull CustomerModel customer) {
		if (userService.isAnonymousUser(customer)) {
			LOG.debug("Anonymous user removal is disabled by convention.");
			return;
		}

		String customerId = customer.getUid();
		try {
			User user = Actions.getUser(customerId);
			if (user == null) {
				LOG.debug("User for provided customer ID '{}' does not exist! Removal not necessary.", customerId);
			} else if (isRemovalEnabled) {
				LOG.debug("User for provided customer ID '{}' exists: '{}'. Trigger user removal.", customerId, user.getId());
				Actions.removeUser(user, customer);
			} else if (isReplicationEnabled) {
				LOG.debug("Customer removal is disabled by configuration, but replication is enabled => Update user roles.");
				updateUserRoles(user, false);
			} else {
				LOG.debug("Customer removal is disabled by configuration.");
			}
		} catch (Auth0Exception exception) {
			LOG.debug("Could not remove customer with ID '{}'! Account needs to be removed manually!", customerId);
			throw new RuntimeException("Could not remove customer on Auth0 side!", exception);
		}
	}

	private User updateUser(CustomerModel customer) {
		String customerId = customer.getUid();
		try {
			User user = Actions.getUser(customerId);
			if (user == null) {
				LOG.debug("User for provided customer ID '{}' does not exist.", customerId);
				user = Actions.createUser(customer);
			} else {
				LOG.debug("User for provided customer ID '{}' exists: '{}'.", customerId, user.getId());
				user = Actions.updateUser(user, customer);
			}
			return user;
		} catch (Auth0Exception exception) {
			LOG.debug("Could not replicate customer with ID '{}'. Data may no be in sync and needs to be corrected manually!", customerId);
			throw new RuntimeException("Could not replicate customer to Auth0!", exception);
		}
	}

	private Role updateUserRoles(User user, boolean isLoginEnabled) {
		try {
			if (StringUtils.isBlank(auth0RoleForCustomers)) {
				return null;
			}

			Role role = Actions.getRole(auth0RoleForCustomers);
			if (role == null) {
				return null;
			}

			if (isLoginEnabled) {
				Actions.assignRole(role, user);
			} else {
				Actions.removeRole(role, user);
			}
			return role;
		} catch (Auth0Exception exception) {
			LOG.debug("Could not synchronize roles for customer ID '{}'. Data may no be in sync and needs to be corrected manually!", user.getEmail());
			throw new RuntimeException("Could not synchronize customer roles to Auth0!", exception);
		}
	}
}
