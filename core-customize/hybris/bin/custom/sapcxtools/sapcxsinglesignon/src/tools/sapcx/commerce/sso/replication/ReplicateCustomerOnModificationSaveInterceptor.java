package tools.sapcx.commerce.sso.replication;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class ReplicateCustomerOnModificationSaveInterceptor implements ValidateInterceptor<CustomerModel> {
	private CustomerReplicationStrategy customerReplicationStrategy;

	public ReplicateCustomerOnModificationSaveInterceptor(CustomerReplicationStrategy customerReplicationStrategy) {
		this.customerReplicationStrategy = customerReplicationStrategy;
	}

	@Override
	public void onValidate(CustomerModel customer, InterceptorContext interceptorContext) {
		if (customer != null) {
			customerReplicationStrategy.replicate(customer);
		}
	}
}
