package sap.commerce.cx.facades.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CxFacadesConstantsTests {
	@Test
	public void testCxfacades() {
		assertThat(CxFacadesConstants.EXTENSIONNAME).isEqualTo("cxfacades");
	}
}
