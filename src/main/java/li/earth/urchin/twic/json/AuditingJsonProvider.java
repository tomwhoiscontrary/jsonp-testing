package li.earth.urchin.twic.json;

import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class AuditingJsonProvider {

    public static Path createTempDirectory() {
        try {
            return Files.createTempDirectory(AuditingJsonProvider.class.getSimpleName() + ".");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final JsonProvider json;
    private final Path auditDir;
    private final Consumer<Path> auditFileListener;

    public AuditingJsonProvider(JsonProvider json, Path auditDir, Consumer<Path> auditFileListener) {
        this.json = json;
        this.auditDir = auditDir;
        this.auditFileListener = auditFileListener;
    }

    public AuditingJsonProvider(JsonProvider json, Consumer<Path> auditFileListener) {
        this(json, createTempDirectory(), auditFileListener);
    }

    public JsonParser createParser(InputStream in) {
        BufferedWriter out = openAuditStream();

        ProxyInputStream proxyIn = new ProxyInputStream(new InputStreamAuditor(in, out));
        JsonParser parser = json.createParser(proxyIn);
        ProxyJsonParser proxyParser = new ProxyJsonParser(new JsonParserAuditor(parser, out));

        return proxyParser;
    }

    private BufferedWriter openAuditStream() {
        try {
            Path auditFile = Files.createTempFile(auditDir, OffsetDateTime.now() + ".", ".txt");
            auditFileListener.accept(auditFile);
            return Files.newBufferedWriter(auditFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
