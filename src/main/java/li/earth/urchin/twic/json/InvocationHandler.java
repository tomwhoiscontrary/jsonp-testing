package li.earth.urchin.twic.json;

@FunctionalInterface
public interface InvocationHandler<T, E extends Throwable> {

    Object invoke(String name, Object... args) throws E;

}
