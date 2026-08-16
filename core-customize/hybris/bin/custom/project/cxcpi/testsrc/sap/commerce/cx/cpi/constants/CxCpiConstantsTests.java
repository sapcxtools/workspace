package sap.commerce.cx.cpi.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CxCpiConstantsTests {
	@Test
	public void testCxCpiConstants() {
		assertThat(CxCpiConstants.EXTENSIONNAME).isEqualTo("cxcpi");
	}
}
