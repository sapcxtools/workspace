package tools.sapcx.commerce.sso.replication;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class CustomerReplicationInterceptor implements ValidateInterceptor<CustomerModel>, RemoveInterceptor<CustomerModel> {
	private CustomerReplicationStrategy customerReplicationStrategy;

	public CustomerReplicationInterceptor(CustomerReplicationStrategy customerReplicationStrategy) {
		this.customerReplicationStrategy = customerReplicationStrategy;
	}

	@Override
	public void onValidate(CustomerModel customer, InterceptorContext interceptorContext) {
		if (customer != null) {
			customerReplicationStrategy.replicate(customer);
		}
	}

	@Override
	public void onRemove(CustomerModel customer, InterceptorContext interceptorContext) throws InterceptorException {
		if (customer != null) {
			customerReplicationStrategy.remove(customer);
		}
	}
}
