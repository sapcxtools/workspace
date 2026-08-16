package sap.commerce.cx.facades.dashboard.populators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.CMSFlexComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import me.cxdev.commerce.toolkit.testing.itemmodel.InMemoryModelFactory;
import sap.commerce.cx.core.model.CxDashboardWidgetModel;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class CxDashboardWidgetPopulatorTest {

	@Mock
	private I18NService i18NService;

	private CxDashboardWidgetPopulator populator;

	@BeforeEach
	public void setup() {
		populator = new CxDashboardWidgetPopulator(i18NService);
	}

	@Test
	public void shouldPopulateAllAttributesOnDataAndFlexTypeWhenFlexComponent() {
		// setup
		final CxDashboardWidgetModel source = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		final String code = "widgetCode";
		source.setCode(code);
		final CMSFlexComponentModel contentComponent = InMemoryModelFactory.createTestableItemModel(CMSFlexComponentModel.class);
		final String contentComponentUid = "componentUid";
		contentComponent.setUid(contentComponentUid);
		final String flexType = "flexType";
		contentComponent.setFlexType(flexType);
		source.setContentComponent(contentComponent);
		final int minColumnSpan = 2;
		source.setMinColumnSpan(minColumnSpan);
		final int minRowSpan = 3;
		source.setMinRowSpan(minRowSpan);
		source.setMultipleOccurrenceAllowed(true);
		when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
		final String displayname = "displayName";
		source.setDisplayName(displayname, Locale.ENGLISH);

		final CxDashboardWidgetData target = new CxDashboardWidgetData();

		// run
		populator.populate(source, target);

		// verify
		assertEquals(code, target.getCode());
		assertEquals(contentComponentUid, target.getContentComponentUid());
		assertEquals(flexType, target.getContentComponentType());
		assertEquals(minColumnSpan, target.getMinColumnSpan());
		assertEquals(minRowSpan, target.getMinRowSpan());
		assertEquals(displayname, target.getDisplayName());
		assertTrue(target.isMultipleOccurrenceAllowed());
	}

	@Test
	public void shouldPopulateAllAttributesOnDataAndItemTypeWhenRegularCMSComponent() {
		// setup
		final CxDashboardWidgetModel source = InMemoryModelFactory.createTestableItemModel(CxDashboardWidgetModel.class);
		final String code = "widgetCode";
		source.setCode(code);
		final SimpleCMSComponentModel contentComponent = InMemoryModelFactory.createTestableItemModel(CMSParagraphComponentModel.class);
		final String contentComponentUid = "componentUid";
		contentComponent.setUid(contentComponentUid);
		source.setContentComponent(contentComponent);
		final int minColumnSpan = 2;
		source.setMinColumnSpan(minColumnSpan);
		final int minRowSpan = 3;
		source.setMinRowSpan(minRowSpan);
		source.setMultipleOccurrenceAllowed(true);

		final CxDashboardWidgetData target = new CxDashboardWidgetData();

		// run
		populator.populate(source, target);

		// verify
		assertEquals(code, target.getCode());
		assertEquals(contentComponentUid, target.getContentComponentUid());
		assertEquals(CMSParagraphComponentModel._TYPECODE, target.getContentComponentType());
		assertEquals(minColumnSpan, target.getMinColumnSpan());
		assertEquals(minRowSpan, target.getMinRowSpan());
		assertTrue(target.isMultipleOccurrenceAllowed());
	}
}
