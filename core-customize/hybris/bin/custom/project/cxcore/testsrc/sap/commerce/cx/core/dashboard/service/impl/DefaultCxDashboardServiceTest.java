package sap.commerce.cx.core.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.contents.components.CMSFlexComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import me.cxdev.commerce.toolkit.testing.itemmodel.InMemoryModelFactory;
import sap.commerce.cx.core.dashboard.dao.CxDashboardDao;
import sap.commerce.cx.core.model.CxDashboardConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetConfigModel;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.core.model.CxDashboardWidgetSettingModel;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultCxDashboardServiceTest {

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private CxDashboardDao dashboardDao;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CustomerModel customer;

	@Mock
	private CxDashboardConfigModel dashboardConfig;

	@Mock
	private CxDashboardConfigModel otherConfig;

	@Mock
	private CxDashboardWidgetConfigModel widgetConfig1;

	@Mock
	private CxDashboardWidgetConfigModel widgetConfig2;

	@Mock
	private CxDashboardWidgetModel widget1;

	@Mock
	private CxDashboardWidgetModel widget2;

	@Mock
	private CMSFlexComponentModel flexComponent;

	@Mock
	private SimpleCMSComponentModel cmsComponent;

	@Mock
	private BaseSiteModel baseSite;

	private DefaultCxDashboardService systemUnderTest;

	@BeforeEach
	void setUp() {
		systemUnderTest = new DefaultCxDashboardService(modelService, userService, dashboardDao, baseSiteService);
	}

	@Test
	void shouldReturnNullWhenCodeIsBlank() {
		lenient().when(userService.getCurrentUser()).thenReturn(customer);

		assertNull(systemUnderTest.getDashboardConfigForCodeAndCurrentUser("   "));

		verifyNoInteractions(dashboardDao);
	}

	@Test
	void shouldReturnNullWhenCurrentUserIsNotCustomer() {
		when(userService.getCurrentUser()).thenReturn(InMemoryModelFactory.createTestableItemModel(UserModel.class));

		assertNull(systemUnderTest.getDashboardConfigForCodeAndCurrentUser("dashboardCode"));
		assertTrue(systemUnderTest.getDashboardConfigsForCurrentUser().isEmpty());
		assertNull(systemUnderTest.createNewDashboardConfig());

		verifyNoInteractions(dashboardDao);
		verifyNoInteractions(modelService);
	}

	@Test
	void shouldReturnDashboardConfigForCurrentCustomer() {
		when(userService.getCurrentUser()).thenReturn(customer);
		when(dashboardDao.getDashboardConfigByCodeAndUser("dashboardCode", customer)).thenReturn(dashboardConfig);

		CxDashboardConfigModel result = systemUnderTest.getDashboardConfigForCodeAndCurrentUser("dashboardCode");

		assertSame(dashboardConfig, result);
		verify(dashboardDao).getDashboardConfigByCodeAndUser("dashboardCode", customer);
	}

	@Test
	void shouldCreateNewDashboardConfigForCurrentCustomer() {
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getUid()).thenReturn("cust01");
		when(modelService.create(CxDashboardConfigModel.class)).thenReturn(dashboardConfig);

		CxDashboardConfigModel result = systemUnderTest.createNewDashboardConfig();

		assertSame(dashboardConfig, result);
		verify(dashboardConfig).setCustomer(customer);
		verify(dashboardConfig).setCode(Mockito.anyString());

		verify(modelService).create(CxDashboardConfigModel.class);
	}

	@Test
	void shouldSaveDashboardConfig() {
		systemUnderTest.saveDashboardConfig(dashboardConfig);

		verify(modelService).save(dashboardConfig);
	}

	@Test
	public void shouldReturnAllWidgetsFromBaseSite() {
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getAvailableDashboardWidgets()).thenReturn(List.of(widget1, widget2));

		Collection<CxDashboardWidgetModel> result = systemUnderTest.getAllAvailableWidgets();
		assertEquals(2, result.size());
		assertTrue(result.contains(widget1));
		assertTrue(result.contains(widget2));
		verify(baseSiteService).getCurrentBaseSite();
		verify(baseSite).getAvailableDashboardWidgets();
	}

	@Test
	public void shouldReturnWidgetByCode() {
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getAvailableDashboardWidgets()).thenReturn(List.of(widget1, widget2));
		when(widget1.getCode()).thenReturn("widget1");
		when(widget2.getCode()).thenReturn("widget2");

		CxDashboardWidgetModel result = systemUnderTest.getDashboardWidgetByCode("widget2");
		assertSame(widget2, result);
		verify(baseSiteService).getCurrentBaseSite();
		verify(baseSite).getAvailableDashboardWidgets();
	}

	@Test
	public void shouldReturnNullWhenNoWidgetAvailableByCode() {
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getAvailableDashboardWidgets()).thenReturn(List.of(widget1, widget2));
		when(widget1.getCode()).thenReturn("widget1");
		when(widget2.getCode()).thenReturn("widget2");

		CxDashboardWidgetModel result = systemUnderTest.getDashboardWidgetByCode("other widget");
		assertNull(result);
		verify(baseSiteService).getCurrentBaseSite();
		verify(baseSite).getAvailableDashboardWidgets();
	}

	@Test
	public void shouldCreateNewDashboardWidgetConfig() {
		when(modelService.create(CxDashboardWidgetConfigModel.class)).thenReturn(widgetConfig1);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getAvailableDashboardWidgets()).thenReturn(List.of(widget1));
		when(widget1.getCode()).thenReturn("widget1");
		when(widget1.isMultipleOccurrenceAllowed()).thenReturn(false);
		when(widget1.getContentComponent()).thenReturn(flexComponent);
		when(flexComponent.getUid()).thenReturn("widgetComponent");
		when(widgetConfig1.getWidget()).thenReturn(widget1);

		CxDashboardWidgetConfigModel result = systemUnderTest.createNewDashboardWidgetConfig("widget1", "config-1");
		assertNotNull(result);
		verify(widgetConfig1).setWidget(widget1);
		verify(widgetConfig1).setCode(anyString());
	}

	@Test
	public void shouldCopyWidgetConfig() {
		when(widgetConfig1.getWidget()).thenReturn(widget1);
		when(widget1.getCode()).thenReturn("widget1");
		when(modelService.create(CxDashboardWidgetConfigModel.class)).thenReturn(widgetConfig2);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getAvailableDashboardWidgets()).thenReturn(List.of(widget1));
		when(widget1.getCode()).thenReturn("widget1");
		when(widgetConfig2.getWidget()).thenReturn(widget1);
		when(widget1.isMultipleOccurrenceAllowed()).thenReturn(false);
		when(widget1.getContentComponent()).thenReturn(flexComponent);
		when(flexComponent.getUid()).thenReturn("widgetComponent");
		when(widgetConfig1.getPosition()).thenReturn(0);
		when(widgetConfig1.getRowSpan()).thenReturn(1);
		when(widgetConfig1.getColumnSpan()).thenReturn(2);

		CxDashboardWidgetConfigModel result = systemUnderTest.copyWidgetConfig(widgetConfig1, "config-1");
		assertSame(widgetConfig2, result);
		verify(widgetConfig2).setWidget(widget1);
		verify(widgetConfig2).setPosition(0);
		verify(widgetConfig2).setRowSpan(1);
		verify(widgetConfig2).setColumnSpan(2);
		verify(widgetConfig2).setCode(anyString());
	}

	@Test
	void shouldCreateDashboardConfigFromDefault() {
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getUid()).thenReturn("cust01");
		when(modelService.create(CxDashboardConfigModel.class)).thenReturn(dashboardConfig);
		when(modelService.create(CxDashboardWidgetConfigModel.class)).thenReturn(widgetConfig2);
		when(dashboardDao.getAllDashboardConfigsByUser(customer)).thenReturn(List.of());
		when(dashboardDao.getDefaultDashboardConfig()).thenReturn(otherConfig);
		when(otherConfig.getName()).thenReturn("Default Dashboard");
		when(otherConfig.getWidgetConfigs()).thenReturn(List.of(widgetConfig1));
		when(widgetConfig1.getWidget()).thenReturn(widget1);
		when(widgetConfig1.getPosition()).thenReturn(0);
		when(widgetConfig1.getRowSpan()).thenReturn(1);
		when(widgetConfig1.getColumnSpan()).thenReturn(2);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getAvailableDashboardWidgets()).thenReturn(List.of(widget1));
		when(widget1.getCode()).thenReturn("widget1");
		when(widget1.isMultipleOccurrenceAllowed()).thenReturn(false);
		when(widget1.getContentComponent()).thenReturn(flexComponent);
		when(flexComponent.getUid()).thenReturn("widgetComponent");
		when(widgetConfig2.getWidget()).thenReturn(widget1);

		CxDashboardConfigModel result = systemUnderTest.createDashboardConfigFromDefault();

		assertSame(dashboardConfig, result);
		verify(dashboardConfig).setCustomer(customer);
		verify(dashboardConfig).setCode(anyString());
		verify(dashboardConfig).setPosition(0);
		verify(dashboardConfig).setName("Default Dashboard");
		verify(dashboardConfig).setActive(true);
		verify(dashboardConfig).setWidgetConfigs(List.of(widgetConfig2));
		verify(widgetConfig2).setRowSpan(1);
		verify(widgetConfig2).setColumnSpan(2);
		verify(widgetConfig2).setWidget(widget1);
		verify(widgetConfig2).setPosition(0);
		verify(widgetConfig2).setCode(anyString());
		verify(modelService).save(dashboardConfig);
	}

	@Test
	public void shouldCreateCodeForWidgetConfigFromComponentUidAndConfigCode() {
		when(widgetConfig1.getWidget()).thenReturn(widget1);
		when(widget1.getContentComponent()).thenReturn(flexComponent);
		when(flexComponent.getUid()).thenReturn("widgetComponent");
		when(widget1.isMultipleOccurrenceAllowed()).thenReturn(false);

		final String result = systemUnderTest.createCodeForWidgetConfig(widgetConfig1, "dashboardConfig");
		assertEquals("widgetComponent_dashboardConfig", result);
	}

	@Test
	public void shouldCreateCodeForWidgetConfigFromComponentUidAndToStringAndConfigCode() {
		when(widgetConfig1.getWidget()).thenReturn(widget1);
		when(widget1.getContentComponent()).thenReturn(flexComponent);
		when(flexComponent.getUid()).thenReturn("widgetComponent");
		when(widget1.isMultipleOccurrenceAllowed()).thenReturn(true);
		when(widgetConfig1.toString()).thenReturn("storageAddress");

		final String result = systemUnderTest.createCodeForWidgetConfig(widgetConfig1, "dashboardConfig");
		assertEquals("widgetComponent_storageAddress_dashboardConfig", result);
	}

	@Test
	void shouldReturnNullFromCreateDashboardConfigFromDefaultWhenUserIsNotCustomer() {
		when(userService.getCurrentUser()).thenReturn(InMemoryModelFactory.createTestableItemModel(UserModel.class));

		assertNull(systemUnderTest.createDashboardConfigFromDefault());

		verifyNoInteractions(dashboardDao);
		verifyNoInteractions(modelService);
	}

	@Test
	public void shouldDeleteAllWidgetConfigsFromDashboardConfig() {
		when(dashboardConfig.getWidgetConfigs()).thenReturn(List.of(widgetConfig1, widgetConfig2));

		systemUnderTest.removeWidgetConfigs(dashboardConfig);

		verify(modelService).remove(widgetConfig1);
		verify(modelService).remove(widgetConfig2);
	}

	@Test
	public void shouldRemoveOrphanedWidgetSettings() {
		when(dashboardConfig.getWidgetConfigs()).thenReturn(List.of(widgetConfig1));
		CxDashboardWidgetSettingModel setting1 = mock(CxDashboardWidgetSettingModel.class);
		CxDashboardWidgetSettingModel setting2 = mock(CxDashboardWidgetSettingModel.class);
		when(widgetConfig1.getSettings()).thenReturn(List.of(setting1, setting2));

		systemUnderTest.removeWidgetConfigs(dashboardConfig);

		verify(modelService).remove(widgetConfig1);
		verify(modelService).remove(setting1);
		verify(modelService).remove(setting2);
	}

	@Test
	void shouldDeleteDashboardConfigForCurrentUser() {
		when(userService.getCurrentUser()).thenReturn(customer);
		when(dashboardDao.getDashboardConfigByCodeAndUser("config-1", customer)).thenReturn(dashboardConfig);
		when(dashboardDao.getAllDashboardConfigsByUser(customer)).thenReturn(List.of(dashboardConfig, otherConfig));

		systemUnderTest.deleteDashboardConfigForCurrentUser("config-1");

		verify(modelService).remove(dashboardConfig);
	}

	@Test
	void shouldNotDeleteLastDashboardConfigForCurrentUser() {
		when(userService.getCurrentUser()).thenReturn(customer);
		when(dashboardDao.getDashboardConfigByCodeAndUser("config-1", customer)).thenReturn(dashboardConfig);
		when(dashboardDao.getAllDashboardConfigsByUser(customer)).thenReturn(List.of(dashboardConfig));

		assertThrows(IllegalArgumentException.class, () -> systemUnderTest.deleteDashboardConfigForCurrentUser("config-1"));

		verify(modelService, never()).remove(dashboardConfig);
	}

	@Test
	void shouldDoNothingWhenDashboardConfigToDeleteDoesNotExist() {
		when(userService.getCurrentUser()).thenReturn(customer);
		when(dashboardDao.getDashboardConfigByCodeAndUser("missing", customer)).thenReturn(null);

		systemUnderTest.deleteDashboardConfigForCurrentUser("missing");

		verifyNoInteractions(modelService);
	}
}
