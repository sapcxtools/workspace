package sap.commerce.cx.core.dashboard.interceptors;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;

public class CxDashboardWidgetConfigValidateInterceptor implements ValidateInterceptor<CxDashboardWidgetConfigModel> {
	private final ModelService modelService;

	public CxDashboardWidgetConfigValidateInterceptor(ModelService modelService) {
		this.modelService = modelService;
	}

	@Override
	public void onValidate(CxDashboardWidgetConfigModel model, InterceptorContext ctx) throws InterceptorException {
		if (model.getPosition() < 0) {
			throw new InterceptorException("Position must be non-negative");
		}
		CxDashboardWidgetModel widget = model.getWidget();
		modelService.refresh(widget);
		if (widget == null) {
			throw new InterceptorException("Widget must not be null");
		}
		if (model.getColumnSpan() < widget.getMinColumnSpan()) {
			throw new InterceptorException(String.format("Column span for widget '%s' must be at least %d", widget.getCode(), widget.getMinColumnSpan()));
		}
		if (model.getRowSpan() < widget.getMinRowSpan()) {
			throw new InterceptorException(String.format("Row span for widget '%s' must be at least %d", widget.getCode(), widget.getMinRowSpan()));
		}
	}
}
