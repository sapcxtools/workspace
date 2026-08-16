package sap.commerce.cx.occ.constants;

import org.junit.jupiter.api.Test;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;

class CxOccExtensionTests {
	@Test
	void verify_requiredExtensions_areLoaded() {
		InstalledExtensionVerifier.verifier()
				.requires("commercewebservices")
				.requires("cxcore")
				.verify();
	}
}
