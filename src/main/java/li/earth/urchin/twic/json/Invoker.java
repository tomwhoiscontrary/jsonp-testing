package li.earth.urchin.twic.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Invoker<T, E extends Throwable> {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPES_BY_WRAPPER = Map.of(Byte.class, byte.class,
                                                                                     Short.class, short.class,
                                                                                     Integer.class, int.class,
                                                                                     Long.class, long.class,
                                                                                     Float.class, float.class,
                                                                                     Double.class, double.class,
                                                                                     Boolean.class, boolean.class,
                                                                                     Character.class, char.class);

    private record Signature(String name, List<Class<?>> parameterTypes) {}

    private final Class<T> type;
    private final Class<E> exceptionType;
    private final Map<Signature, Method> methods;

    public Invoker(Class<T> type, Class<E> exceptionType) {
        this.type = type;
        this.exceptionType = exceptionType;
        methods = new HashMap<>();
    }

    public Object invoke(T obj, String name, Object... args) throws E {
        try {
            return method(name, args).invoke(obj, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            } else if (exceptionType.isInstance(cause)) {
                throw exceptionType.cast(cause);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private Method method(String name, Object[] args) {
        List<Class<?>> parameterTypes = Arrays.stream(args)
                                              .map(Object::getClass)
                                              .map(this::unwrapPrimitive)
                                              .collect(Collectors.toList());
        return methods.computeIfAbsent(new Signature(name, parameterTypes), this::resolveMethod);
    }

    private Class<?> unwrapPrimitive(Class<?> type) {
        return PRIMITIVE_TYPES_BY_WRAPPER.getOrDefault(type, type);
    }

    private Method resolveMethod(Signature signature) {
        try {
            return type.getMethod(signature.name, signature.parameterTypes.toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
