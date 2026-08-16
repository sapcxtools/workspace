package sap.commerce.cx.fake.invoices;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.invoice.strategies.InvoiceStrategy;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class CxInvoiceStrategyFake implements InvoiceStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(CxInvoiceStrategyFake.class);
	private static final String FAKE_INVOICE_FILE = "/cxfake/static/cx_example_invoice.pdf";
	private static final String FAKE_INVOICE_ID = "CX-1002345";

	private final CommonI18NService i18nService;
	private final String externalSystemId;

	public CxInvoiceStrategyFake(CommonI18NService i18nService, String externalSystemId) {
		this.i18nService = i18nService;
		this.externalSystemId = externalSystemId;
	}

	@Override
	public List<SAPInvoiceData> getInvoices(OrderModel orderModel) {
		SAPInvoiceData fakeInvoice = new SAPInvoiceData();
		fakeInvoice.setExternalSystemId(externalSystemId);
		fakeInvoice.setInvoiceDate(orderModel.getDate() != null ? orderModel.getDate() : new Date());
		fakeInvoice.setInvoiceId(FAKE_INVOICE_ID);

		PriceData totalPrice = new PriceData();
		totalPrice.setValue(BigDecimal.valueOf(orderModel.getTotalPrice()));
		totalPrice.setCurrencyIso(orderModel.getCurrency().getIsocode());
		totalPrice.setPriceType(PriceDataType.BUY);
		totalPrice.setFormattedValue(i18nService.getCurrentCurrency().getSymbol() + " " + totalPrice.getValue().toString());
		fakeInvoice.setTotalAmount(totalPrice);

		PriceData netPrice = new PriceData();
		netPrice.setValue(BigDecimal.valueOf(orderModel.getTotalPrice() - orderModel.getTotalTax()));
		netPrice.setCurrencyIso(orderModel.getCurrency().getIsocode());
		netPrice.setPriceType(PriceDataType.BUY);
		netPrice.setFormattedValue(i18nService.getCurrentCurrency().getSymbol() + " " + netPrice.getValue().toString());
		fakeInvoice.setNetAmount(netPrice);

		return List.of(fakeInvoice);
	}

	@Override
	public byte[] getInvoiceBinary(OrderModel orderModel, String invoiceId) {
		try (InputStream resourceAsStream = this.getClass().getResourceAsStream(FAKE_INVOICE_FILE)) {
			if (resourceAsStream != null) {
				return resourceAsStream.readAllBytes();
			}
		} catch (IOException e) {
			LOG.error("Could not load fake invoice file", e);
			return new byte[0];
		}
		return new byte[0];
	}
}
