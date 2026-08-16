package sap.commerce.cx.data.constants;

import org.junit.jupiter.api.Test;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;

class CxDataExtensionTests {
	@Test
	void verify_requiredExtensions_areLoaded() {
		InstalledExtensionVerifier.verifier()
				.requires("cxcore")
				.verify();
	}
}
