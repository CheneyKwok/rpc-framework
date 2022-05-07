package github.cheneykwok.extension;

import github.cheneykwok.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;

    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        //noinspection unchecked
        return  (ExtensionLoader<S>) EXTENSION_LOADERS.computeIfAbsent(type, k -> new ExtensionLoader<S>(type));
    }

    public T getExtension(String name) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("Extension name should not be null or empty");
        }
        Holder<Object> holder = cachedInstances.computeIfAbsent(name, k -> new Holder<>());
        Object instance = holder.get();
        // double check
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new IllegalArgumentException("no such of extension of name:" + name);
        }
        //noinspection unchecked
        return (T) EXTENSION_INSTANCES.computeIfAbsent(clazz, k -> {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        });
    }

    private Map<String, Class<?>> getExtensionClasses() {
        // get the loaded extensions class from the cache
        Map<String, Class<?>> classes = cachedClasses.get();
        // double check
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * load all extension from our extension directory
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadResource(extensionClasses, classLoader, url);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int ci = line.indexOf("#");
                if (ci > 0) {
                    // string after # is comment so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    final int ei = line.indexOf("=");
                    String name = line.substring(0, ei).trim();
                    String clazzName = line.substring(ei + 1).trim();
                    // our SPI use key-value pair so both of them must be not empty
                    if (name.length() > 0 && clazzName.length() > 0) {
                        Class<?> clazz = classLoader.loadClass(clazzName);
                        extensionClasses.put(name, clazz);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
