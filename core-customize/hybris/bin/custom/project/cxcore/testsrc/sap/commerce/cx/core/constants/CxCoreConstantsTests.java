package sap.commerce.cx.core.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CxCoreConstantsTests {
	@Test
	public void testCxCoreConstants() {
		assertThat(CxCoreConstants.EXTENSIONNAME).isEqualTo("cxcore");
	}
}
