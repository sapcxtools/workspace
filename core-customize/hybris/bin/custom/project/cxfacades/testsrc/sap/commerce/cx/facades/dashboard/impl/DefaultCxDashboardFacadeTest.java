package sap.commerce.cx.facades.dashboard.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import sap.commerce.cx.core.dashboard.service.CxDashboardService;
import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.core.model.CxDashboardWidgetSettingModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetSettingData;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class DefaultCxDashboardFacadeTest {
	@Mock
	private CxDashboardService dashboardService;

	@Mock
	private ModelService modelService;

	@Mock
	private Converter<CxDashboardConfigModel, CxDashboardConfigData> dashboardConfigConverter;

	@Mock
	private Converter<CxDashboardWidgetModel, CxDashboardWidgetData> dashboardWidgetConverter;

	@Mock
	private CxDashboardConfigModel dashboardConfig1;

	@Mock
	private CxDashboardConfigModel dashboardConfig2;

	@Mock
	private CxDashboardConfigData configData1;

	@Mock
	private CxDashboardConfigData configData2;

	@Mock
	private CxDashboardWidgetConfigModel widgetConfig1;

	@Mock
	private CxDashboardWidgetConfigModel widgetConfig2;

	@Mock
	private CxDashboardWidgetConfigData widgetConfigData1;

	@Mock
	private CxDashboardWidgetConfigData widgetConfigData2;

	@Mock
	private CxDashboardWidgetSettingModel widgetSetting1;

	@Mock
	private CxDashboardWidgetSettingData widgetSettingData1;

	@Mock
	private CxDashboardWidgetModel widget1;

	@Mock
	private CxDashboardWidgetModel widget2;

	@Mock
	private CxDashboardWidgetData widgetData1;

	@Mock
	private CxDashboardWidgetData widgetData2;

	private DefaultCxDashboardFacade systemUnderTest;

	@BeforeEach
	public void setup() {
		systemUnderTest = new DefaultCxDashboardFacade(dashboardService, modelService, dashboardConfigConverter, dashboardWidgetConverter);
	}

	@Test
	public void shouldReturnDefaultDashboardConfig() {
		when(dashboardService.getDefaultDashboardConfig()).thenReturn(dashboardConfig1);
		when(dashboardConfigConverter.convert(dashboardConfig1)).thenReturn(configData1);

		CxDashboardConfigData result = systemUnderTest.getDefaultDashboardConfig();

		assertSame(configData1, result);
		verify(dashboardService).getDefaultDashboardConfig();
		verify(dashboardConfigConverter).convert(dashboardConfig1);
	}

	@Test
	public void shouldReturnDashboardConfigsForCurrentUser() {
		when(dashboardService.getDashboardConfigsForCurrentUser()).thenReturn(List.of(dashboardConfig1, dashboardConfig2));
		when(configData1.getPosition()).thenReturn(1);
		when(configData2.getPosition()).thenReturn(0);
		when(dashboardConfigConverter.convertAll(List.of(dashboardConfig1, dashboardConfig2))).thenReturn(List.of(configData1, configData2));

		List<CxDashboardConfigData> result = systemUnderTest.getDashboardConfigsForCurrentUser();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertSame(configData2, result.get(0));
		assertSame(configData1, result.get(1));
		verify(dashboardService).getDashboardConfigsForCurrentUser();
		verify(dashboardConfigConverter).convertAll(List.of(dashboardConfig1, dashboardConfig2));
	}

	@Test
	public void shouldCreateInitialConfigWhenNoneExisting() {
		when(dashboardService.getDashboardConfigsForCurrentUser()).thenReturn(List.of());
		when(dashboardService.createDashboardConfigFromDefault()).thenReturn(dashboardConfig1);
		when(dashboardConfigConverter.convertAll(List.of(dashboardConfig1))).thenReturn(List.of(configData1));

		List<CxDashboardConfigData> result = systemUnderTest.getDashboardConfigsForCurrentUser();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertSame(configData1, result.getFirst());
		verify(dashboardService).getDashboardConfigsForCurrentUser();
		verify(dashboardService).createDashboardConfigFromDefault();
		verify(dashboardConfigConverter).convertAll(List.of(dashboardConfig1));
	}

	@Test
	public void shouldMapToExistingAndSaveDashboardConfig() {
		when(configData1.getCode()).thenReturn("configcode");
		when(dashboardService.getDashboardConfigForCodeAndCurrentUser("configcode")).thenReturn(dashboardConfig1);
		when(dashboardConfigConverter.convert(dashboardConfig1)).thenReturn(configData2);

		final CxDashboardConfigData result = systemUnderTest.saveDashboardConfigForCurrentUser(configData1);

		assertSame(configData2, result);
		verify(dashboardService).getDashboardConfigForCodeAndCurrentUser("configcode");
		verify(dashboardService).removeWidgetConfigs(dashboardConfig1);
		verify(dashboardService).saveDashboardConfig(dashboardConfig1);
		verify(dashboardConfigConverter).convert(dashboardConfig1);
		// verify that mapDashboardConfig gets called?
	}

	@Test
	public void shouldMapToNewlyCreatedAndSaveDashboardConfig() {
		when(configData1.getCode()).thenReturn("configcode");
		when(dashboardService.createNewDashboardConfig()).thenReturn(dashboardConfig1);
		when(dashboardConfigConverter.convert(dashboardConfig1)).thenReturn(configData2);

		final CxDashboardConfigData result = systemUnderTest.saveDashboardConfigForCurrentUser(configData1);

		assertSame(configData2, result);
		verify(dashboardService).getDashboardConfigForCodeAndCurrentUser("configcode");
		verify(dashboardService).removeWidgetConfigs(dashboardConfig1);
		verify(dashboardService).saveDashboardConfig(dashboardConfig1);
		verify(dashboardConfigConverter).convert(dashboardConfig1);
		// verify that mapDashboardConfig gets called?
	}

	@Test
	public void shouldMapDashboardConfig() {
		when(configData1.getName()).thenReturn("configName");
		when(configData1.isActive()).thenReturn(true);
		when(configData1.getPosition()).thenReturn(1);
		when(configData1.getWidgetConfigs()).thenReturn(List.of(widgetConfigData1));
		when(widgetConfigData1.getWidget()).thenReturn(widgetData1);
		when(widgetData1.getCode()).thenReturn("widget1");
		when(dashboardService.createNewDashboardWidgetConfig("widget1", "configcode")).thenReturn(widgetConfig1);
		when(widgetConfigData1.getPosition()).thenReturn(1);
		when(widgetConfigData1.getColumnSpan()).thenReturn(2);
		when(widgetConfigData1.getRowSpan()).thenReturn(3);

		systemUnderTest.mapDashboardConfig(configData1, dashboardConfig1, "configcode");

		verify(dashboardConfig1).setName("configName");
		verify(dashboardConfig1).setActive(true);
		verify(dashboardConfig1).setPosition(1);
		verify(dashboardService).createNewDashboardWidgetConfig("widget1", "configcode");
		verify(dashboardConfig1).setWidgetConfigs(List.of(widgetConfig1));
	}

	@Test
	public void shouldMapDashboardWidgetConfig() {
		when(widgetConfigData1.getPosition()).thenReturn(1);
		when(widgetConfigData1.getColumnSpan()).thenReturn(2);
		when(widgetConfigData1.getRowSpan()).thenReturn(3);
		when(widgetConfigData1.getSettings()).thenReturn(List.of(widgetSettingData1));
		when(modelService.create(CxDashboardWidgetSettingModel.class)).thenReturn(widgetSetting1);
		when(widgetSettingData1.getKey()).thenReturn("settingKey");
		when(widgetSettingData1.getValue()).thenReturn("settingValue");

		systemUnderTest.mapDashboardWidgetConfig(widgetConfigData1, widgetConfig1);

		verify(widgetConfig1).setPosition(1);
		verify(widgetConfig1).setColumnSpan(2);
		verify(widgetConfig1).setRowSpan(3);
		verify(widgetConfig1).setSettings(List.of(widgetSetting1));
	}

	@Test
	public void shouldMapDashboardWidgetSettings() {
		when(modelService.create(CxDashboardWidgetSettingModel.class)).thenReturn(widgetSetting1);
		when(widgetSettingData1.getKey()).thenReturn("settingKey");
		when(widgetSettingData1.getValue()).thenReturn("settingValue");

		final CxDashboardWidgetSettingModel result = systemUnderTest.mapDashboardWidgetSettings(widgetSettingData1);

		assertSame(widgetSetting1, result);
		verify(widgetSetting1).setKey("settingKey");
		verify(widgetSetting1).setValue("settingValue");
		verify(modelService).create(CxDashboardWidgetSettingModel.class);
	}

	@Test
	public void shouldDeleteDashboardConfigForCurrentUser() {
		systemUnderTest.deleteDashboardConfigForCurrentUser("configCode");
		verify(dashboardService).deleteDashboardConfigForCurrentUser("configCode");
	}

	@Test
	public void shouldReturnAllAvailableWidgets() {
		List<CxDashboardWidgetModel> availableWidgets = List.of(widget1, widget2);
		when(dashboardService.getAllAvailableWidgets()).thenReturn(availableWidgets);
		when(dashboardWidgetConverter.convertAll(availableWidgets)).thenReturn(List.of(widgetData1, widgetData2));

		List<CxDashboardWidgetData> result = systemUnderTest.getAllAvailableDashboardWidgets();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(widgetData1, result.get(0));
		assertEquals(widgetData2, result.get(1));
		verify(dashboardService).getAllAvailableWidgets();
		verify(dashboardWidgetConverter).convertAll(List.of(widget1, widget2));
	}
}
