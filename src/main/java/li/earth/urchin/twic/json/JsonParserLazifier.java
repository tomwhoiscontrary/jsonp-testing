package li.earth.urchin.twic.json;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

import java.util.function.Supplier;

public class JsonParserLazifier implements InvocationHandler<JsonParser, JsonParsingException> {

    private static final Invoker<JsonParser, JsonParsingException> INVOKER = new Invoker<>(JsonParser.class, JsonParsingException.class);

    private final Supplier<JsonParser> supplier;
    private JsonParser parser;

    public JsonParserLazifier(Supplier<JsonParser> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Object invoke(String name, Object... args) throws JsonParsingException {
        if (parser == null) parser = supplier.get();
        return INVOKER.invoke(parser, name, args);
    }

}
