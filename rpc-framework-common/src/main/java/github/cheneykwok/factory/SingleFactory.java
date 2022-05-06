package github.cheneykwok.factory;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingleFactory {

    private static final Map<String, Object> INSTANCE_MAP = new ConcurrentHashMap<>();

    private SingleFactory() {

    }

    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        String key = clazz.toString();
        if (INSTANCE_MAP.containsKey(key)) {
            return clazz.cast(INSTANCE_MAP.get(key));
        } else {
            return clazz.cast(INSTANCE_MAP.computeIfAbsent(key, k -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }
}
