package sap.commerce.cx.cpi.constants;

import org.junit.jupiter.api.Test;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;

class CxCpiExtensionTests {
	@Test
	void verify_requiredExtensions_areLoaded() {
		InstalledExtensionVerifier.verifier()
				.requires("cxcore")
				.verify();
	}
}
