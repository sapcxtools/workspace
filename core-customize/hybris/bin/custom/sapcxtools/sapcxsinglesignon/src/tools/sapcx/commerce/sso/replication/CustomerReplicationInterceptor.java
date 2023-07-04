package tools.sapcx.commerce.sso.replication;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerReplicationInterceptor implements ValidateInterceptor<CustomerModel>, RemoveInterceptor<CustomerModel> {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerReplicationInterceptor.class);

	private CustomerReplicationStrategy customerReplicationStrategy;

	public CustomerReplicationInterceptor(CustomerReplicationStrategy customerReplicationStrategy) {
		this.customerReplicationStrategy = customerReplicationStrategy;
	}

	@Override
	public void onValidate(CustomerModel customer, InterceptorContext interceptorContext) {
		if (customer != null) {
			try {
				customerReplicationStrategy.replicate(customer);
			} catch (RuntimeException e) {
				LOG.warn("Could not replicate customer with ID '{}'. Data may no be in sync and needs to be corrected manually!", customer.getUid());
			}
		}
	}

	@Override
	public void onRemove(CustomerModel customer, InterceptorContext interceptorContext) throws InterceptorException {
		if (customer != null) {
			try {
				customerReplicationStrategy.remove(customer);
			} catch (RuntimeException e) {
				LOG.warn("Could not remove customer with ID '{}'! Account needs to be removed manually!", customer.getUid());
			}
		}
	}
}
