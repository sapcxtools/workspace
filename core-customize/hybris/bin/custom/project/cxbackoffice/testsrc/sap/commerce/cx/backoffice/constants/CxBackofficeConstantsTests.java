package sap.commerce.cx.backoffice.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CxBackofficeConstantsTests {
	@Test
	public void testCxBackofficeConstants() {
		assertThat(CxBackofficeConstants.EXTENSIONNAME).isEqualTo("cxbackoffice");
	}
}
