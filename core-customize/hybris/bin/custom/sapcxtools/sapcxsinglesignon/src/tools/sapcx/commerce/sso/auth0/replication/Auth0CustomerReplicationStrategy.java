package tools.sapcx.commerce.sso.auth0.replication;

import javax.annotation.Nonnull;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.roles.Role;
import com.auth0.json.mgmt.users.User;

import de.hybris.platform.core.model.user.CustomerModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.sapcx.commerce.sso.auth0.actions.Actions;
import tools.sapcx.commerce.sso.replication.CustomerReplicationStrategy;

public class Auth0CustomerReplicationStrategy implements CustomerReplicationStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(Auth0CustomerReplicationStrategy.class);

	private String auth0RoleForCustomers;
	private boolean enabled;

	public Auth0CustomerReplicationStrategy(String auth0RoleForCustomers, boolean enabled) {
		this.auth0RoleForCustomers = auth0RoleForCustomers;
		this.enabled = enabled;
	}

	@Override
	public void replicate(@Nonnull CustomerModel customer) {
		if (!enabled) {
			LOG.debug("Customer replication is disabled by configuration.");
			return;
		}

		String email = customer.getUid();

		try {
			User user = Actions.getUser(email);
			if (user == null) {
				LOG.debug("User for provided email '{}' does not exist.", email);
				user = Actions.createUser(customer);
			} else {
				LOG.debug("User for provided email '{}' exists: '{}'.", email, user.getId());
				user = Actions.updateUser(customer);
			}

			Role role = Actions.getRole(auth0RoleForCustomers);
			if (role != null) {
				Actions.assignRole(role, user);
			}
		} catch (Auth0Exception exception) {
			LOG.warn(String.format("Could not replicate customer with ID '%s'. ", email), exception);
		}
	}
}
