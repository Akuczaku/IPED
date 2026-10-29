package iped.parsers.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.tika.metadata.TikaCoreProperties;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import iped.parsers.standard.StandardParser;

public class OutlookDBXParser extends AbstractParser {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(OutlookDBXParser.class);

    private static final AtomicBoolean logged = new AtomicBoolean();

    public static final String DBX_PARSER_IMPL_SYS_PROP = "dbxParserImpl"; //$NON-NLS-1$

    private static final String DEFAULT_DBX_PARSER_IMPL = "dpf.sp.gpinf.indexer.parsers.OutlookDBXParserImpl"; //$NON-NLS-1$

    private Parser parserImpl;

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {

        try {
            String implName = System.getProperty(DBX_PARSER_IMPL_SYS_PROP, DEFAULT_DBX_PARSER_IMPL);
            parserImpl = (Parser) Class.forName(implName).getDeclaredConstructor().newInstance();
            return parserImpl.getSupportedTypes(context);

        } catch (Throwable e) {
            if (!logged.getAndSet(true)) {
                String msg = "DBX parser not found, DBX mailboxes will NOT be expanded."; //$NON-NLS-1$
                // are we in analysis app?
                if (System.getProperty("iped.javaVersionChecked") != null)
                    LOGGER.warn(msg, e);
                else
                    LOGGER.error(msg, e);
            }
            return Collections.emptySet();
        }
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {

        EmbeddedDocumentExtractor extractor = context.get(EmbeddedDocumentExtractor.class,
                new ParsingEmbeddedDocumentExtractor(context));
        EmbeddedDocumentExtractorDecorator eded = new EmbeddedDocumentExtractorDecorator(extractor);

        context.set(EmbeddedDocumentExtractor.class, eded);
        parserImpl.parse(stream, handler, metadata, context);
        context.set(EmbeddedDocumentExtractor.class, extractor);

    }

    // Extract only the top-level MIME Subject, never grep arbitrary DBX bytes.
    // Capped at 128 KiB to avoid unbounded memory consumption.
    private static String readMimeSubject(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int prev = -1, prev2 = -1, prev3 = -1;
        for (int i = 0; i < 131068; i++) {
            int b = stream.read();
            if (b < 0) return null;
            out.write(b);
            if ((prev3 == 13 && prev2 == 10 && prev == 13 && b == 10)
                    || (prev == 10 && b == 10)) break;
            prev3 = prev2; prev2 = prev; prev = b;
        }
        String[] lines = new String(out.toByteArray(), StandardCharsets.ISO_8859_1).split("\\r?\\n");
        StringBuilder value = null;
        for (String line : lines) {
            if (line.isEmpty()) break;
            if (line.startsWith(" ") || line.startsWith("\t")) {
                if (value != null) value.append(' ').append(line.trim());
            } else if (value != null) {
                break;
            } else if (line.regionMatches(true, 0, "Subject:", 0, 8)) {
                value = new StringBuilder(line.substring(8).trim());
            }
        }
        if (value == null) return null;
        String decoded = DecoderUtil.decodeEncodedWords(value.toString(), DecodeMonitor.SILENT);
        return decoded == null ? null : decoded.trim();
    }

    private static boolean safeMatch(String indexed, String mime) {
        if (indexed == null || !indexed.contains("??") || mime == null || mime.contains("??")
                || mime.contains("\ufffd") || mime.contains("=?")) return false;
        String a = normalize(indexed.replace("??", ""));
        String b = normalize(mime);
        return !a.isEmpty() && a.equals(b);
    }

    private static String normalize(String value) {
        return value.replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase(Locale.ROOT);
    }

    private class EmbeddedDocumentExtractorDecorator implements EmbeddedDocumentExtractor {

        private EmbeddedDocumentExtractor extractor;

        public EmbeddedDocumentExtractorDecorator(EmbeddedDocumentExtractor extractor) {
            this.extractor = extractor;
        }

        @Override
        public boolean shouldParseEmbedded(Metadata metadata) {
            return extractor.shouldParseEmbedded(metadata);
        }

        @Override
        public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml)
                throws SAXException, IOException {

            if ("message/rfc822".equals(metadata.get(Metadata.CONTENT_TYPE))) //$NON-NLS-1$
                metadata.set(StandardParser.INDEXER_CONTENT_TYPE, "message/rfc822"); //$NON-NLS-1$

            // FIX4.1: inspect only the RFC822 headers of this individual embedded email.
            // No DBX data bytes are changed; the wrapped stream is reset before parsing.
            if ("message/rfc822".equals(metadata.get(Metadata.CONTENT_TYPE))) {
                BufferedInputStream buffered = new BufferedInputStream(stream);
                buffered.mark(131073);
                String original = metadata.get("resourceName");
                if (original == null) original = metadata.get(TikaCoreProperties.TITLE);
                String mime = readMimeSubject(buffered);
                buffered.reset();
                if (original != null) metadata.set("dbx:originalSubject", original);
                if (mime != null && !mime.isEmpty()) metadata.set("dbx:mimeSubject", mime);
                if (safeMatch(original, mime)) {
                    metadata.set("resourceName", mime);
                    metadata.set(TikaCoreProperties.TITLE, mime);
                }
                extractor.parseEmbedded(buffered, handler, metadata, outputHtml);
            } else {
                extractor.parseEmbedded(stream, handler, metadata, outputHtml);
            }

        }

    }

}
