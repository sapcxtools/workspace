package sap.commerce.cx.occ.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CxOccConstantsTests {
	@Test
	public void testCxOccConstants() {
		assertThat(CxOccConstants.EXTENSIONNAME).isEqualTo("cxocc");
	}
}
