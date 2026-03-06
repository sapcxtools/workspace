package sap.commerce.project.core.extension;

import static org.assertj.core.api.Assertions.assertThat;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;
import org.junit.jupiter.api.Test;
import sap.commerce.project.core.constants.MyCoreConstants;

class MyCoreExtensionTests {
	@Test
	void testConstants() {
		assertThat(MyCoreConstants.EXTENSIONNAME).isEqualTo("mycore");
	}

	@Test
	void testExtensionDependencies() {
		InstalledExtensionVerifier.verifier()
				.requires("cxdevtoolkit")
				.verify();
	}
}
