package tools.sapcx.commerce.toolkit.email;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.assertj.core.util.VisibleForTesting;

public class StoreInLocalDirectoryHtmlEmailService implements HtmlEmailService {
    private static final Logger LOG = Logger.getLogger(StoreInLocalDirectoryHtmlEmailService.class);

    private String directory;
    private String filenamePattern = "{timestamp}_{subject}.{extension}";
    private String extension = "eml";

    public StoreInLocalDirectoryHtmlEmailService(String directory) {
        this.directory = directory;
    }

    public StoreInLocalDirectoryHtmlEmailService(String directory, String filenamePattern, String extension) {
        this(directory);
        this.filenamePattern = filenamePattern;
        this.extension = extension;
    }

    @Override
    public String sendEmail(HtmlEmail email) throws EmailException {
        // first let the email generate the mime message, which also sets sent date
        buildHtmlEmail(email);

        File outputFile = getOutputDirectory().resolve(getFilename(email)).toFile();
        try (FileOutputStream out = new FileOutputStream(outputFile)){
            MimeMessage mimeMessage = email.getMimeMessage();
            mimeMessage.writeTo(out);
            LOG.info("New email successfully stored at: " + outputFile.getAbsolutePath());
            return mimeMessage.getMessageID();
        } catch (IOException | MessagingException e) {
            LOG.error("Email could not persisted to local storage: " + e.getLocalizedMessage(), e);
            return null;
        }
    }

    private void buildHtmlEmail(HtmlEmail email) throws EmailException {
        if (StringUtils.isBlank(email.getHostName())) {
            // fix hostname to localhost to avoid exceptions during build of mime message
            email.setHostName("localhost");
        }
        email.buildMimeMessage();
    }

    private Path getOutputDirectory() {
        Path outputDir = Paths.get(directory);
        if (outputDir.toFile().mkdirs()) {
            LOG.info("Output directory of local email storage was created at: " + outputDir.toAbsolutePath().toString());
        }
        return outputDir;
    }

    @VisibleForTesting
    String getFilename(HtmlEmail email) {
        Date sentDate = email.getSentDate() != null ? email.getSentDate() : new Date();
        Map<String, String> replacements = Map.of(
                "{timestamp}", Long.toString(sentDate.getTime()),
                "{datetime}", new SimpleDateFormat("YYYYMMdd-HHmmssS").format(sentDate),
                "{subject}", email.getSubject() != null ? email.getSubject() : "",
                "{from}", escapeAddress(email.getFromAddress()),
                "{to}", escapeAddress(emptyIfNull(email.getToAddresses()).stream().findAny().orElse(null)),
                "{extension}", this.extension
        );

        String newFilename = filenamePattern;
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            newFilename = newFilename.replace(replacement.getKey(), replacement.getValue());
        }
        return newFilename;
    }

    private String escapeAddress(InternetAddress address) {
        return (address != null) ? address.getAddress().toLowerCase(Locale.ROOT).replaceAll("@", "__at__") : "";
    }
}
