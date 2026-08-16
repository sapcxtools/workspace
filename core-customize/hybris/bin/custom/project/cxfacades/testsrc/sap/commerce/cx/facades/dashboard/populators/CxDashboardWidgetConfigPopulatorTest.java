package sap.commerce.cx.facades.dashboard.populators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.core.model.CxDashboardWidgetSettingModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class CxDashboardWidgetConfigPopulatorTest {
	@InjectMocks
	private CxDashboardWidgetConfigPopulator populator;

	@Mock
	private Converter<CxDashboardWidgetModel, CxDashboardWidgetData> widgetConverter;

	@Test
	public void shouldPopulateAllAttributesOnData() {
		// setup
		final CxDashboardWidgetConfigModel source = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetConfigModel.class);
		final int position = 2;
		source.setPosition(position);
		final int colSpan = 3;
		source.setColumnSpan(colSpan);
		final int rowSpan = 1;
		source.setRowSpan(rowSpan);
		final CxDashboardWidgetModel widget = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		source.setWidget(widget);
		final CxDashboardWidgetSettingModel setting = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetSettingModel.class);
		final String settingsKey = "setting";
		final String settingsValue = "some important note";
		setting.setKey(settingsKey);
		setting.setValue(settingsValue);
		source.setSettings(List.of(setting));
		final CxDashboardWidgetConfigData target = new CxDashboardWidgetConfigData();

		// run
		populator.populate(source, target);

		// verify
		assertEquals(position, target.getPosition());
		assertEquals(colSpan, target.getColumnSpan());
		assertEquals(rowSpan, target.getRowSpan());
		assertNotNull(target.getSettings());
		assertEquals(1, target.getSettings().size());
		assertEquals(settingsKey, target.getSettings().getFirst().getKey());
		assertEquals(settingsValue, target.getSettings().getFirst().getValue());
		verify(widgetConverter, times(1)).convert(widget);
	}

}
