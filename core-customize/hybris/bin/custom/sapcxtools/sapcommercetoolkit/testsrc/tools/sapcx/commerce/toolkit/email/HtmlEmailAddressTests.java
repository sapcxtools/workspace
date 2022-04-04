package tools.sapcx.commerce.toolkit.email;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class HtmlEmailAddressTests {
	private HtmlEmailAddress address;
	private HtmlEmailAddress otherAddress;
	private HtmlEmailAddress sameAddress;

	@Before
	public void setUp() {
		address = HtmlEmailAddress.address("name", "email@localhost");
		otherAddress = HtmlEmailAddress.address("other", "other@localhost");
		sameAddress = HtmlEmailAddress.address("name", "email@localhost");
	}

	@Test
	public void testToString() {
		assertThat(address.toString()).isEqualTo(ToStringBuilder.reflectionToString(address));
		assertThat(otherAddress.toString()).isEqualTo(ToStringBuilder.reflectionToString(otherAddress));
		assertThat(sameAddress.toString()).isEqualTo(ToStringBuilder.reflectionToString(sameAddress));
	}

	@Test
	public void testHashCode() {
		assertThat(address.hashCode()).isEqualTo(HashCodeBuilder.reflectionHashCode(address));
		assertThat(otherAddress.hashCode()).isEqualTo(HashCodeBuilder.reflectionHashCode(otherAddress));
		assertThat(sameAddress.hashCode()).isEqualTo(HashCodeBuilder.reflectionHashCode(sameAddress));
	}

	@Test
	public void testEquals() {
		assertThat(address.equals(null)).isFalse();
		assertThat(address.equals(address)).isTrue();
		assertThat(address.equals(otherAddress)).isFalse();
		assertThat(address.equals(sameAddress)).isTrue();
	}
}
