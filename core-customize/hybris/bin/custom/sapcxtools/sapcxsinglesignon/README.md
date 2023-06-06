# SAP CX Single-Sign-On

The `sapcxsinglesignon` extension provides core implementations for SSO integration with external
service and identity providers, such as Auth0 by Okta.

## FEATURE DESCRIPTION

The functionality covers a filter for the OCC layer, that handles the validation of the access tokens.
The filter will be executed before the actual spring security chain of the OCC extension, creating a
valid token in the local token storage for the authenticated user.

The extension also ships a customer replication strategy, that can be activated to create users within
Auth0, whenever a customer object is created or changed within SAP commerce cloud.

### How to activate and use

To activate the functionality, one needs to set the configuration parameters accordingly for each
environment, especially the flags `sapcxsinglesignon.filter.enabled` and `sapcxsinglesignon.replication.enabled`
which are set to `false` by default.

In addition to the backend configuration, the composable storefront needs to be extended with
configuration settings as within the following example:

```javascript
export const authCodeFlowConfig: AuthConfig = {
	authentication: {
		client_id: '<client id>',
		client_secret: '<client secret>',
		baseUrl: 'https://<your-auth0-domain>',
		tokenEndpoint: '/oauth/token',
		loginUrl: '/authorize',
		revokeEndpoint: '/oidc/logout',
		logoutUrl: 'https://www.<your-domain>.com/?revalidate_token=true',
		userinfoEndpoint: '/userinfo',
		OAuthLibConfig: {
			redirectUri: 'https://www.your-domain>.com/',
			responseType: 'code',
			scope: 'openid profile email',
			showDebugInformation: true,
			disablePKCE: false,
		},
	},
};
```

For the customer replication, one can add additional populators to the `auth0CustomerConverter` converter bean.
This can be easily done using the `modifyPopulatorList` bean notation:

```xml
<bean id="myCustomerPopulator" class="com.acme.cx.MyCustomerPopulator"/>
<bean parent="modifyPopulatorList">
    <property name="list" ref="auth0CustomerConverter"/>
    <property name="add" ref="myCustomerPopulator"/>
</bean>
```

### Configuration parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| sapcxsinglesignon.replication.enabled               | Boolean | specifies whether the replication is active or not (default: false) |
| sapcxsinglesignon.filter.enabled                    | Boolean | specifies whether the filter is active or not (default: false) |
| sapcxsinglesignon.filter.login.userClientId         | String  | the SAP Commerce client ID for your single page application (required) |
| sapcxsinglesignon.filter.login.tokenExpiration      | String  | the token expiration period in minutes (default: 600 = 10 hours) |
| sapcxsinglesignon.auth0.domain                      | String  | the registered auth0 domain, eg. dev-1234.eu.auth0.com (required) |
| sapcxsinglesignon.auth0.auth.api.clientid           | String  | the auth0 client ID for your single page application (required) |
| sapcxsinglesignon.auth0.auth.api.clientsecret       | String  | the auth0 client secret for your single page application (required) |
| sapcxsinglesignon.auth0.management.api.clientid     | String  | the auth0 client ID for your machine-to-machine application (required) |
| sapcxsinglesignon.auth0.management.api.clientsecret | String  | the auth0 client secret for your machine-to-machine application (required) |
| sapcxsinglesignon.auth0.customer.connection         | String  | the authentication connection for customers (default: "Username-Password-Authentication") |
| sapcxsinglesignon.auth0.customer.role               | String  | the role to assign to newly created customer accounts |
| sapcxsinglesignon.auth0.customer.userIdField        | String  | field name used for user ID mapping (default: email) |

## License

_Licensed under the Apache License, Version 2.0, January 2004_

_Copyright 2021-2022, SAP CX Tools_