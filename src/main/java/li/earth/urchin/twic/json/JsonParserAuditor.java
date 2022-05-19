package li.earth.urchin.twic.json;

import jakarta.json.JsonException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

import java.io.BufferedWriter;
import java.io.IOException;

public class JsonParserAuditor implements InvocationHandler<JsonParser, JsonParsingException> {

    private static final Invoker<JsonParser, JsonParsingException> INVOKER = new Invoker<>(JsonParser.class, JsonParsingException.class);

    private final JsonParser parser;
    private final BufferedWriter out;

    public JsonParserAuditor(JsonParser parser, BufferedWriter out) {
        this.parser = parser;
        this.out = out;
    }

    @Override
    public Object invoke(String name, Object... args) {
        try {
            return INVOKER.invoke(parser, name, args);
        } finally {
            audit(name);
        }
    }

    private void audit(String method) {
        try {
            out.write("parser." + method + "();");
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new JsonException("error writing audit trail", e);
        }
    }

}
