package sap.commerce.cx.facades.dashboard.populators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import me.cxdev.commerce.toolkit.testing.itemmodel.InMemoryModelFactory;
import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetConfigData;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class CxDashboardConfigPopulatorTest {
	@Mock
	private Converter<CxDashboardWidgetConfigModel, CxDashboardWidgetConfigData> widgetConverter;

	@InjectMocks
	private CxDashboardConfigPopulator populator;

	@Test
	public void shouldPopulateAllAttributesOnData() {
		// setup
		final CxDashboardConfigModel source = InMemoryModelFactory.createTestableItemModel(CxDashboardConfigModel.class);
		final CxDashboardWidgetConfigModel widgetConfig1 = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		final CxDashboardWidgetConfigModel widgetConfig2 = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		final List<CxDashboardWidgetConfigModel> widgetConfigsList = List.of(widgetConfig1, widgetConfig2);
		source.setWidgetConfigs(widgetConfigsList);
		source.setPosition(1);
		source.setActive(true);
		final String code = "dashboardConfigCode";
		source.setCode(code);
		final String name = "Dashboard Config Name";
		source.setName(name);
		final CxDashboardConfigData target = new CxDashboardConfigData();

		// run
		populator.populate(source, target);

		// verify
		assertEquals(1, target.getPosition());
		assertEquals(code, target.getCode());
		assertEquals(name, target.getName());
		assertTrue(target.isActive());
		verify(widgetConverter, times(1)).convertAll(widgetConfigsList);
	}

}
