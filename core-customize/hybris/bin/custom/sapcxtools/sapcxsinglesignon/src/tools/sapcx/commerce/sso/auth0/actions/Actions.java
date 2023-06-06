package tools.sapcx.commerce.sso.auth0.actions;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;
import com.auth0.json.mgmt.roles.Role;
import com.auth0.json.mgmt.users.User;

import de.hybris.platform.core.model.user.CustomerModel;

public interface Actions {
	static Role getRole(String roleName) throws Auth0Exception {
		return FetchRoleAction.getRole(roleName);
	}

	static void assignRole(Role role, User user) throws Auth0Exception {
		AssignRoleAction.assignRole(role, user);
	}

	static UserInfo getUserInformation(String accessToken) throws Auth0Exception {
		return FetchUserInformationAction.getUserInformation(accessToken);
	}

	static User getUser(String email) throws Auth0Exception {
		return FetchUserAction.getUser(email);
	}

	static User createUser(CustomerModel customer) throws Auth0Exception {
		return CreateUserAction.createUser(customer);
	}

	static User updateUser(CustomerModel customer) throws Auth0Exception {
		return UpdateUserAction.updateUser(customer);
	}
}
