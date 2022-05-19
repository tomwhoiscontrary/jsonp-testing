package li.earth.urchin.twic.json;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class InputStreamAuditor implements InvocationHandler<InputStream, IOException> {

    private static final Invoker<InputStream, IOException> INVOKER = new Invoker<>(InputStream.class, IOException.class);

    private static final Map<Character, Character> CHARACTERS_TO_ESCAPE = Map.of('\\', '\\',
                                                                                 '\t', 't',
                                                                                 '\b', 'b',
                                                                                 '\n', 'n',
                                                                                 '\r', 'r',
                                                                                 '\f', 'f',
                                                                                 '\'', '\'',
                                                                                 '\"', '\"');

    private final InputStream in;
    private final BufferedWriter out;

    public InputStreamAuditor(InputStream in, BufferedWriter out) {
        this.in = in;
        this.out = out;
    }

    public Object invoke(String name, Object[] args) throws IOException {
        Object returnValue = INVOKER.invoke(in, name, args);

        byte[] copied;
        if (args.length == 1
            && args[0] instanceof byte[] buf) {
            copied = Arrays.copyOfRange(buf, 0, (int) returnValue);
        } else if (args.length == 3
                   && args[0] instanceof byte[] buf
                   && args[1] instanceof Integer off
                   && args[2] instanceof Integer len) {
            copied = Arrays.copyOfRange(buf, off, off + (int) returnValue);
        } else {
            copied = null;
        }

        audit(name, returnValue, copied, args);

        return returnValue;
    }

    private void audit(String method, Object returnValue, byte[] copied, Object... args) throws IOException {
        out.write("in.expect(\"");
        out.write(method);
        out.write("\", ");
        out.write(toJava(returnValue, true));
        out.write(", ");
        out.write(toJava(copied, false));
        for (Object parameter : args) {
            out.write(", ");
            out.write(toJava(parameter, true));
        }
        out.write(");");
        out.newLine();
        out.flush();
    }

    private String toJava(Object value, boolean includeBufferContents) {
        if (value == null) {
            return nullToJava();
        } else if (value instanceof Integer i) {
            return intToJava(i);
        } else if (value instanceof byte[] buf) {
            if (includeBufferContents) {
                return bufferToJava(buf);
            } else {
                return blankBufferToJava(buf);
            }
        } else {
            throw new IllegalArgumentException(value.getClass().getSimpleName());
        }
    }

    private String nullToJava() {
        return "null";
    }

    private String intToJava(Integer i) {
        return Integer.toString(i);
    }

    private String bufferToJava(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");

        try (Reader in = new InputStreamReader(new ByteArrayInputStream(buf), StandardCharsets.UTF_8)) {
            for (int ch; (ch = in.read()) != -1; ) {
                Character escape = CHARACTERS_TO_ESCAPE.get((char) ch);
                if (escape != null) {
                    sb.append('\\');
                    sb.append(escape);
                } else {
                    sb.append((char) ch);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        sb.append("\".getBytes(StandardCharsets.UTF_8)");

        return sb.toString();
    }

    private String blankBufferToJava(byte[] buf) {
        return "new byte[" + buf.length + "]";
    }

}
