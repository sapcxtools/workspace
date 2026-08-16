package sap.commerce.cx.core.dashboard.interceptors;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import me.cxdev.commerce.toolkit.testing.itemmodel.InMemoryModelFactory;
import me.cxdev.commerce.toolkit.testing.testdoubles.core.InterceptorContextStub;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class CxDashboardWidgetConfigValidateInterceptorTest {

	@Mock
	private ModelService modelService;

	@InjectMocks
	private CxDashboardWidgetConfigValidateInterceptor interceptor;

	@Test
	public void shouldNotThrowExceptionWhenRowAndColumnSpanEqualOrMoreThanMinValuesOnWidgetAndPositionNonNegative() throws InterceptorException {
		// setup
		final InterceptorContext ctx = InterceptorContextStub.interceptorContext().stub();
		final CxDashboardWidgetConfigModel widgetConfig = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		widgetConfig.setPosition(0);
		final CxDashboardWidgetModel widget = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		widget.setMinRowSpan(1);
		widget.setMinColumnSpan(1);
		widgetConfig.setWidget(widget);
		widgetConfig.setColumnSpan(2);
		widgetConfig.setRowSpan(1);

		// run
		interceptor.onValidate(widgetConfig, ctx);
	}

	@Test
	public void shouldThrowExceptionWhenPositionNegative() {
		// setup
		final InterceptorContext ctx = InterceptorContextStub.interceptorContext().stub();
		final CxDashboardWidgetConfigModel widgetConfig = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		widgetConfig.setPosition(-1);
		final CxDashboardWidgetModel widget = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		widget.setMinRowSpan(1);
		widget.setMinColumnSpan(1);
		widgetConfig.setWidget(widget);
		widgetConfig.setColumnSpan(1);
		widgetConfig.setRowSpan(1);

		assertThrows(InterceptorException.class, () -> interceptor.onValidate(widgetConfig, ctx));
	}

	@Test
	public void shouldThrowExceptionWhenRowSpanLessThanMin() {
		// setup
		final InterceptorContext ctx = InterceptorContextStub.interceptorContext().stub();
		final CxDashboardWidgetConfigModel widgetConfig = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		widgetConfig.setPosition(-1);
		final CxDashboardWidgetModel widget = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		widget.setMinRowSpan(2);
		widget.setMinColumnSpan(1);
		widgetConfig.setWidget(widget);
		widgetConfig.setColumnSpan(1);
		widgetConfig.setRowSpan(1);

		assertThrows(InterceptorException.class, () -> interceptor.onValidate(widgetConfig, ctx));
	}

	@Test
	public void shouldThrowExceptionWhenColumnSpanLessThanMin() {
		// setup
		final InterceptorContext ctx = InterceptorContextStub.interceptorContext().stub();
		final CxDashboardWidgetConfigModel widgetConfig = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		widgetConfig.setPosition(-1);
		final CxDashboardWidgetModel widget = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		widget.setMinRowSpan(1);
		widget.setMinColumnSpan(2);
		widgetConfig.setWidget(widget);
		widgetConfig.setColumnSpan(1);
		widgetConfig.setRowSpan(1);

		assertThrows(InterceptorException.class, () -> interceptor.onValidate(widgetConfig, ctx));
	}

	@Test
	public void shouldThrowExceptionWhenWidgetNull() {
		// setup
		final InterceptorContext ctx = InterceptorContextStub.interceptorContext().stub();
		final CxDashboardWidgetConfigModel widgetConfig = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		widgetConfig.setPosition(-1);

		assertThrows(InterceptorException.class, () -> interceptor.onValidate(widgetConfig, ctx));
	}
}
