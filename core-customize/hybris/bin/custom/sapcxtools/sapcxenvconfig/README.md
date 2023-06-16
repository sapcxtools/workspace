# SAP CX Environment Configuration

The `sapcxenvconfig` extension provides core implementations to provide environment specific configuration
to the Spartacus frontend. SAP does not deliver any solution to this topic, see also the long running thread
and discussion going on at https://github.com/SAP/spartacus/issues/5772 (since 2018!).


## FEATURE DESCRIPTION

With this extension, the environment specific configuration can be placed within the backend and will be
provided by a specific OCC controller, that does not require any authentication, hence, it can be loaded
upfront, during the SPA bootstrap. With a provider configuration factory introduced within the custom
module in Spartacus, this will give the posibility to overload properties within the frontend on an
environment specific base, solving the issue above.

### How to activate and use

To activate the functionality, one needs to set the configuration parameters accordingly for each
environment, i.e. the `sapcxenvconfig.environment.id` and `sapcxenvconfig.environment.name` property
which are set to values used for local development by default.

In addition, the desired frontend configuration properties needs to be added as properties, each
prefixed with `sapcxenvconfig.frontend.` (see an example below).

In addition to the backend configuration, the composable storefront needs to be extended with a
config factory, typically in the `custom-config.module.ts` module file:

```typescript
// ... among other imports
import { provideConfigFactory } from "@spartacus/core";
import { securedConfigChunkFromBackend } from "./sapcxenvconfig";

@NgModule({
    // ...
   	providers: [
        // ... all others, the following line should be the last provider
        provideConfigFactory(securedConfigChunkFromBackend, []),
    ],
})
```

The imported file `sapcxenvconfig.ts` should be created next to the `custom-config.module.ts` and
contains the following content (complete file shown):

```typescript
import { Config } from "@spartacus/core";
import { environment } from "src/environments/environment";

export function securedConfigChunkFromBackend(): Config {
	try {
		let request = new window.XMLHttpRequest();
		request.open("GET", environment.occBaseUrl + "/occ/v2/" + environment.defaultBaseSite + "/configuration", false);
		request.send();

		if (request.status == 200) {
			let response = JSON.parse(request.responseText);
			let envId = response.environmentId;
			let envName = response.environmentName;
            
            window.console && console.log("Fetched frontend configuration for environment: "+ envName +" (ID: " + envId + ")");
			return JSON.parse(response.config);
		}
	} catch (e) {
		window.console && console.log("Error during fetch of frontend configuration: ", e);
	}
	return {};
}
```

Also, please make sure that you add the key `defaultBaseSite` to your `environment.ts` file. The configuration
is not resolved site related (might be in the future), but for the time being, there just needs to be a
valid site. As an example, here is a valid `environment.ts` file:

```typescript
export const environment = {
	production: false,
	occBaseUrl: "https://localhost:9002",
	defaultBaseSite: "default",
	isMock: false,
};
```

Now, when the SPA starts, the `securedConfigChunkFromBackend` config factory is invoked and fetches the
frontend properties from the OCC backend. As this needs to be done synchronously, we make use of the
native HTTP request functionality of the browsers.

### Configuration parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| sapcxenvconfig.environment.id                       | String  | the ID of the environment (default: L1) |
| sapcxenvconfig.environment.name                     | String  | the name of the environment (default: Local Development) |
| sapcxenvconfig.frontend.mykey                       | String  | specifies a frontend configuration property with key `mykey` and a value given to the property |
| sapcxenvconfig.frontend.myobject.variable1          | String  | specifies a frontend configuration property within context `myobject` with key `variable1` and a value given to the property |
| sapcxenvconfig.frontend.myobject.variable2          | String  | specifies a frontend configuration property within context `myobject` with key `variable2` and a value given to the property |


#### Example configuration

As an example, the following frontend configuration properties will result in the JSON object returned as shown below:

```
sapcxenvconfig.environment.id=P1
sapcxenvconfig.environment.name=Production
sapcxenvconfig.frontend.domain.type=SampleType
sapcxenvconfig.frontend.domain.object1.id=obj1
sapcxenvconfig.frontend.domain.object1.name=Any Object
sapcxenvconfig.frontend.domain.object2.id=obj2
sapcxenvconfig.frontend.domain.object2.name=Other Object
sapcxenvconfig.frontend.domain.other.key=value
sapcxenvconfig.frontend.toplevel.key=value
sapcxenvconfig.frontend.toplevel.enabled=true
```

```json
{
  "environmentId": "P1",
  "environmentName": "Production",
  "config": {
    "domain": {
      "type": "SampleType",
      "object1": {
        "id": "obj1",
        "name": "Any Object"
      },
      "object2": {
        "id": "obj2",
        "name": "Other Object"
      },
      "other": {
        "key": "value"
      }
    },
    "toplevel": {
      "key": "value",
      "enabled": "true"
    }
  }
}
```

## License

_Licensed under the Apache License, Version 2.0, January 2004_

_Copyright 2023, SAP CX Tools_