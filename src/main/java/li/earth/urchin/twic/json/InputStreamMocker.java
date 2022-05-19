package li.earth.urchin.twic.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class InputStreamMocker implements InvocationHandler<InputStream, IOException> {

    private record Expectation(String method, Object returnValue, byte[] data, Object[] args) {}

    private final Queue<Expectation> expectations = new ArrayDeque<>();

    public void expect(String method, Object returnValue, byte[] data, Object... args) {
        expectations.add(new Expectation(method, returnValue, data, args));
    }

    @Override
    public Object invoke(String name, Object... args) throws IOException {
        Expectation expectation = expectations.remove();

        assertEquals(expectation.method, name);

        assertEquals(expectation.args.length, args.length);
        for (int i = 0; i < args.length; i++) {
            Object expectedArg = expectation.args[i];
            Object actualArg = args[i];
            if (expectedArg instanceof byte[] expectedBuf
                && actualArg instanceof byte[] actualBuf) {
                assertEquals(expectedBuf.length, actualBuf.length);
            } else {
                assertEquals(expectedArg, actualArg);
            }
        }

        if (expectation.data != null) {
            if (args.length == 1
                && args[0] instanceof byte[] buf) {
                assertEquals(expectation.data.length, buf.length);
                System.arraycopy(expectation.data, 0, buf, 0, expectation.data.length);
            } else if (args.length == 3
                       && args[0] instanceof byte[] buf
                       && args[1] instanceof Integer off
                       && args[2] instanceof Integer len) {
                assertEquals(expectation.data.length, len);
                try {
                    System.arraycopy(expectation.data, 0, buf, off, expectation.data.length);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw e;
                }
            } else {
                throw new IllegalStateException("there is data to copy, but nowhere to copy it to!");
            }
        }

        return expectation.returnValue;
    }

    private void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(actual + " != " + expected);
        }
    }

}
