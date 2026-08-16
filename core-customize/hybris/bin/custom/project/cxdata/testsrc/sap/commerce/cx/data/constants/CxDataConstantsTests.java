package sap.commerce.cx.data.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CxDataConstantsTests {
	@Test
	public void testCxDataConstants() {
		assertThat(CxDataConstants.EXTENSIONNAME).isEqualTo("cxdata");
	}
}
