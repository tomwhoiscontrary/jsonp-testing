package li.earth.urchin.twic.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public class ProxyInputStream extends InputStream {

    private final InvocationHandler<InputStream, IOException> handler;

    public ProxyInputStream(InvocationHandler<InputStream, IOException> handler) {
        this.handler = handler;
    }

    @Override
    public int read() throws IOException {
        return (Integer) handler.invoke("read");
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return (Integer) handler.invoke("read", buf);
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        return (Integer) handler.invoke("read", buf, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return (byte[]) handler.invoke("readAllBytes");
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return (byte[]) handler.invoke("readNBytes", len);
    }

    @Override
    public int readNBytes(byte[] buf, int off, int len) throws IOException {
        return (Integer) handler.invoke("readNBytes", buf, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return (Long) handler.invoke("skip", n);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        handler.invoke("skipNBytes", n);
    }

    @Override
    public int available() throws IOException {
        return (Integer) handler.invoke("available");
    }

    @Override
    public void close() throws IOException {
        handler.invoke("close");
    }

    @Override
    public void mark(int readlimit) {
        try {
            handler.invoke("mark", readlimit);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void reset() throws IOException {
        handler.invoke("reset");
    }

    @Override
    public boolean markSupported() {
        try {
            return (Boolean) handler.invoke("markSupported");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        // to accurately capture this, we should audit the sequence of calls to the output stream - possible but laborious
        throw new UnsupportedOperationException();
    }

}
