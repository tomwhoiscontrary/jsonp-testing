package li.earth.urchin.twic.json;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

public class ProxyJsonParser implements JsonParser {

    private final InvocationHandler<JsonParser, JsonParsingException> handler;

    public ProxyJsonParser(InvocationHandler<JsonParser, JsonParsingException> handler) {
        this.handler = handler;
    }

    @Override
    public boolean hasNext() {
        return (boolean) handler.invoke("hasNext");
    }

    @Override
    public Event next() {
        return (Event) handler.invoke("next");
    }

    @Override
    public String getString() {
        return (String) handler.invoke("getString");
    }

    @Override
    public boolean isIntegralNumber() {
        return (boolean) handler.invoke("isIntegralNumber");
    }

    @Override
    public int getInt() {
        return (int) handler.invoke("getInt");
    }

    @Override
    public long getLong() {
        return (long) handler.invoke("getLong");
    }

    @Override
    public BigDecimal getBigDecimal() {
        return (BigDecimal) handler.invoke("getBigDecimal");
    }

    @Override
    public JsonLocation getLocation() {
        return (JsonLocation) handler.invoke("getLocation");
    }

    @Override
    public JsonObject getObject() {
        return (JsonObject) handler.invoke("getObject");
    }

    @Override
    public JsonValue getValue() {
        return (JsonValue) handler.invoke("getValue");
    }

    @Override
    public JsonArray getArray() {
        return (JsonArray) handler.invoke("getArray");
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        @SuppressWarnings("unchecked")
        Stream<JsonValue> stream = (Stream<JsonValue>) handler.invoke("getArrayStream");
        return stream;
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        @SuppressWarnings("unchecked")
        Stream<Map.Entry<String, JsonValue>> stream = (Stream<Map.Entry<String, JsonValue>>) handler.invoke("getObjectStream");
        return stream;
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        @SuppressWarnings("unchecked")
        Stream<JsonValue> stream = (Stream<JsonValue>) handler.invoke("getValueStream");
        return stream;
    }

    @Override
    public void skipArray() {
        handler.invoke("skipArray");
    }

    @Override
    public void skipObject() {
        handler.invoke("skipObject");
    }

    @Override
    public void close() {
        handler.invoke("close");
    }

}
