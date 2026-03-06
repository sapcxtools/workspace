package sap.commerce.project.occ.extension;

import static org.assertj.core.api.Assertions.assertThat;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;
import org.junit.jupiter.api.Test;
import sap.commerce.project.occ.constants.MyOccConstants;

class MyOccExtensionTests {
	@Test
	void testMyOccConstants() {
		assertThat(MyOccConstants.EXTENSIONNAME).isEqualTo("myocc");
	}

	@Test
	void testExtensionDependencies() {
		InstalledExtensionVerifier.verifier()
				.requires("commercewebservices")
				.requires("myfacades")
				.verify();
	}
}
