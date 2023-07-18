package tools.sapcx.commerce.sso.replication;

import java.util.function.Predicate;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerReplicationInterceptor implements ValidateInterceptor<CustomerModel>, RemoveInterceptor<CustomerModel> {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerReplicationInterceptor.class);
	private static final String REPLICATE_FAILED_MSG = "Could not replicate customer with ID '%s'. Data may no be in sync and needs to be corrected manually!";
	private static final String REMOVE_FAILED_MSG = "Could not remove customer with ID '%s'! Account needs to be removed manually!";

	private CustomerReplicationStrategy customerReplicationStrategy;
	private Predicate<CustomerModel> customerReplicationFilter;

	public CustomerReplicationInterceptor(CustomerReplicationStrategy customerReplicationStrategy, Predicate<CustomerModel> customerReplicationFilter) {
		this.customerReplicationStrategy = customerReplicationStrategy;
		this.customerReplicationFilter = customerReplicationFilter;
	}

	@Override
	public void onValidate(CustomerModel customer, InterceptorContext interceptorContext) {
		try {
			if (customerReplicationFilter.test(customer)) {
				customerReplicationStrategy.replicate(customer);
			}
		} catch (RuntimeException e) {
			LOG.warn(String.format(REPLICATE_FAILED_MSG, customer.getUid()), e);
		}
	}

	@Override
	public void onRemove(CustomerModel customer, InterceptorContext interceptorContext) throws InterceptorException {
		try {
			if (customerReplicationFilter.test(customer)) {
				customerReplicationStrategy.remove(customer);
			}
		} catch (RuntimeException e) {
			LOG.warn(String.format(REMOVE_FAILED_MSG, customer.getUid()), e);
		}
	}
}
