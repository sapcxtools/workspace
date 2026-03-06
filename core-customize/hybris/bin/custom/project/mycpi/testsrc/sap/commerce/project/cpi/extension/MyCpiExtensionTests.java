package sap.commerce.project.cpi.extension;

import static org.assertj.core.api.Assertions.assertThat;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;
import org.junit.jupiter.api.Test;
import sap.commerce.project.cpi.constants.MyCpiConstants;

class MyCpiExtensionTests {
	@Test
	void testConstants() {
		assertThat(MyCpiConstants.EXTENSIONNAME).isEqualTo("mycpi");
	}

	@Test
	void testExtensionDependencies() {
		InstalledExtensionVerifier.verifier()
				.requires("mycore")
				.verify();
	}
}
