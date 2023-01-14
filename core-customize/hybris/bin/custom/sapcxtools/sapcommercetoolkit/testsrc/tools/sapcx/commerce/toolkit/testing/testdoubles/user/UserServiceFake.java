package tools.sapcx.commerce.toolkit.testing.testdoubles.user;

import java.util.*;

import de.hybris.platform.core.model.user.AbstractUserAuditModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.user.UserPasswordChangeAuditModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.exceptions.CannotDecodePasswordException;
import de.hybris.platform.servicelayer.user.exceptions.PasswordEncoderNotFoundException;

import tools.sapcx.commerce.toolkit.testing.itemmodel.InMemoryModelFactory;

public class UserServiceFake implements UserService {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		UserServiceFake fake = new UserServiceFake();

		public Builder withCurrent(UserModel user) {
			fake.current = user;
			fake.users.put(user.getUid(), user);
			return this;
		}

		public Builder withAdmin(EmployeeModel employeeModel) {
			fake.admin = employeeModel;
			fake.users.put(employeeModel.getUid(), employeeModel);
			return this;
		}

		public Builder autoAdmin() {
			EmployeeModel admin = InMemoryModelFactory.createTestableItemModel(EmployeeModel.class);
			admin.setUid("admin");
			return withAdmin(admin);
		}


		public Builder withAnonymous(CustomerModel anonymous) {
			fake.anonymous  = anonymous;
			fake.users.put(anonymous.getUid(), anonymous);
			return this;
		}

		public Builder autoAnonymous() {
			CustomerModel anonymous = InMemoryModelFactory.createTestableItemModel(CustomerModel.class);
			anonymous.setUid("anonymous");
			return withAnonymous(anonymous);
		}

		public Builder withCurrentId(String customerId) {
			CustomerModel customer = InMemoryModelFactory.createTestableItemModel(CustomerModel.class);
			customer.setUid(customerId);
			return withCurrent(customer);
		}

		public Builder withCurrentAndCustomClass(String customerId, Class<? extends CustomerModel> customerClass) {
			CustomerModel customer = InMemoryModelFactory.createTestableItemModel(customerClass);
			customer.setUid(customerId);
			return withCurrent(customer);
		}

		public Builder withUserGroups(List<UserGroupModel> userGroups) {
			fake.userGroups = new HashMap<>();
			userGroups.forEach(userGroup -> fake.userGroups.put(userGroup.getUid(), userGroup));
			return this;
		}

		public UserServiceFake build() {
			return fake;
		}
	}

	public EmployeeModel admin;
	public CustomerModel anonymous;
	public UserModel current;
	public Map<String, UserModel> users = new HashMap<>();
	public Map<String, UserGroupModel> userGroups = new HashMap<>();

	public UserServiceFake() {};

	// Keep static methods for now. Remove later and use the builder.
	public static UserServiceFake fake() {
		return withCustomerId("customer");
	}

	public static UserServiceFake withCustomerId(String customerId) {
		CustomerModel customer = InMemoryModelFactory.createTestableItemModel(CustomerModel.class);
		customer.setUid(customerId);

		return withCustomer(customer);
	}

	public static UserServiceFake withCustomerId(String customerId, Class<? extends CustomerModel> customerClass) {
		CustomerModel customer = InMemoryModelFactory.createTestableItemModel(customerClass);
		customer.setUid(customerId);

		return withCustomer(customer);
	}

	public static UserServiceFake withCustomer(CustomerModel customer) {
		EmployeeModel admin = InMemoryModelFactory.createTestableItemModel(EmployeeModel.class);
		admin.setUid("admin");

		CustomerModel anonymous = InMemoryModelFactory.createTestableItemModel(CustomerModel.class);
		anonymous.setUid("anonymous");

		return new UserServiceFake(admin, anonymous, customer);
	}

	public UserServiceFake(EmployeeModel admin, CustomerModel anonymous, UserModel current) {
		this.admin = admin;
		this.anonymous = anonymous;
		this.current = current;

		users = new HashMap<>();
		users.put(admin.getUid(), admin);
		users.put(anonymous.getUid(), anonymous);
		users.put(current.getUid(), current);
	}

	@Override
	public UserModel getUser(String s) {
		return users.get(s);
	}

	@Override
	public UserModel getUserForUID(String s) {
		return users.get(s);
	}

	@Override
	public <T extends UserModel> T getUserForUID(String s, Class<T> aClass) {
		return aClass.cast(users.get(s));
	}

	@Override
	public boolean isUserExisting(String s) {
		return users.containsKey(s);
	}

	@Override
	public UserGroupModel getUserGroup(String s) {
		return null;
	}

	@Override
	public UserGroupModel getUserGroupForUID(String s) {
		return userGroups.get(s);
	}

	@Override
	public <T extends UserGroupModel> T getUserGroupForUID(String s, Class<T> aClass) {
		UserGroupModel userGroup = getUserGroupForUID(s);
		return (aClass.isInstance(userGroup)) ? aClass.cast(userGroup) : null;
	}

	@Override
	public Set<UserGroupModel> getAllUserGroupsForUser(UserModel userModel) {
		return new HashSet<>(userGroups.values());
	}

	@Override
	public <T extends UserGroupModel> Set<T> getAllUserGroupsForUser(UserModel userModel, Class<T> aClass) {
		return null;
	}

	@Override
	public List<AbstractUserAuditModel> getUserAudits(UserModel userModel) {
		return null;
	}

	@Override
	public boolean isPasswordIdenticalToAudited(UserModel userModel, String s, UserPasswordChangeAuditModel userPasswordChangeAuditModel) {
		return false;
	}

	@Override
	public <T extends UserGroupModel> Set<T> getAllUserGroupsForUserGroup(UserGroupModel userGroupModel, Class<T> aClass) {
		return null;
	}

	@Override
	public boolean isMemberOfGroup(UserModel userModel, UserGroupModel userGroupModel) {
		return userModel.getGroups().contains(userGroupModel);
	}

	@Override
	public boolean isMemberOfGroup(UserModel userModel, UserGroupModel userGroupModel, boolean b) {
		return false;
	}

	@Override
	public boolean isMemberOfGroup(UserGroupModel userGroupModel, UserGroupModel userGroupModel1) {
		return false;
	}

	@Override
	public boolean isMemberOfGroup(UserGroupModel userGroupModel, UserGroupModel userGroupModel1, boolean b) {
		return false;
	}

	@Override
	public Collection<TitleModel> getAllTitles() {
		return null;
	}

	@Override
	public TitleModel getTitleForCode(String s) {
		return null;
	}

	@Override
	public EmployeeModel getAdminUser() {
		return admin;
	}

	@Override
	public UserGroupModel getAdminUserGroup() {
		return null;
	}

	@Override
	public boolean isAdmin(UserModel userModel) {
		return userModel.getUid().equals(admin.getUid());
	}

	@Override
	public boolean isAdminGroup(UserGroupModel userGroupModel) {
		return false;
	}

	@Override
	public boolean isAdminEmployee(UserModel userModel) {
		return isAdmin(userModel);
	}

	@Override
	public CustomerModel getAnonymousUser() {
		return anonymous;
	}

	@Override
	public boolean isAnonymousUser(UserModel userModel) {
		return userModel.getUid().equals(anonymous.getUid());
	}

	@Override
	public UserModel getCurrentUser() {
		return current;
	}

	@Override
	public void setCurrentUser(UserModel userModel) {
		current = userModel;
		users.put(userModel.getUid(), userModel);
	}

	@Override
	public String getPassword(String s) throws CannotDecodePasswordException, PasswordEncoderNotFoundException {
		return null;
	}

	@Override
	public void setPassword(String s, String s1) throws PasswordEncoderNotFoundException {

	}

	@Override
	public void setPassword(String s, String s1, String s2) throws PasswordEncoderNotFoundException {

	}

	@Override
	public void setPassword(UserModel userModel, String s, String s1) throws PasswordEncoderNotFoundException {

	}

	@Override
	public void setPassword(UserModel userModel, String s) throws PasswordEncoderNotFoundException {

	}

	@Override
	public void setPasswordWithDefaultEncoding(UserModel userModel, String s) throws PasswordEncoderNotFoundException {

	}

	@Override
	public String getPassword(UserModel userModel) throws CannotDecodePasswordException, PasswordEncoderNotFoundException {
		return null;
	}

	@Override
	public void setEncodedPassword(UserModel userModel, String s) {

	}

	@Override
	public void setEncodedPassword(UserModel userModel, String s, String s1) {

	}
}
