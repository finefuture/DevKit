package io.github.finefuture.devKit.core.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@see <a href="https://github.com/alibaba/Sentinel">Sentinel</a>}
 *
 * @author Eric Zhao
 */
public final class SpiLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiLoader.class);

    private static final Map<String, ServiceLoader> SERVICE_LOADER_MAP = new ConcurrentHashMap<>();

    public static <T> T loadFirstInstance(Class<T> clazz) {
        try {
            String key = clazz.getName();
            // Not thread-safe, as it's expected to be resolved in a thread-safe context.
            ServiceLoader<T> serviceLoader = SERVICE_LOADER_MAP.computeIfAbsent(key, loaderKey -> ServiceLoader.load(clazz));
            Iterator<T> iterator = serviceLoader.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            } else {
                return null;
            }
        } catch (Exception t) {
            LOGGER.error("[SpiLoader] ERROR: loadFirstInstance failed, exception:{}", t);
            return null;
        }
    }

    /**
     * Load the SPI instance with highest priority.
     *
     * @param clazz class of the SPI
     * @param <T>   SPI type
     * @return the SPI instance with highest priority if exists, or else false
     */
    public static <T> T loadHighestPriorityInstance(Class<T> clazz) {
        try {
            String key = clazz.getName();
            // Not thread-safe, as it's expected to be resolved in a thread-safe context.
            ServiceLoader<T> serviceLoader = SERVICE_LOADER_MAP.computeIfAbsent(key, loaderKey -> ServiceLoader.load(clazz));
            SpiOrderWrapper<T> w = null;
            for (T spi : serviceLoader) {
                int order = SpiOrderResolver.resolveOrder(spi);
                LOGGER.info("[SpiLoader] Found {} SPI: {} with order {} ", order, clazz.getSimpleName(), spi.getClass().getCanonicalName());
                if (w == null || order < w.order) {
                    w = new SpiOrderWrapper<>(order, spi);
                }
            }
            return w == null ? null : w.spi;
        } catch (Exception e) {
            LOGGER.error("[SpiLoader] ERROR: loadHighestPriorityInstance failed, exception:{}", e);
            return null;
        }
    }

    /**
     * Load the SPI instance list for provided SPI interface.
     *
     * @param clazz class of the SPI
     * @param <T>   SPI type
     * @return sorted SPI instance list
     */
    public static <T> List<T> loadInstanceList(Class<T> clazz) {
        try {
            String key = clazz.getName();
            // Not thread-safe, as it's expected to be resolved in a thread-safe context.
            ServiceLoader<T> serviceLoader = SERVICE_LOADER_MAP.computeIfAbsent(key, loaderKey -> ServiceLoader.load(clazz));
            List<T> list = new ArrayList<>();
            for (T spi : serviceLoader) {
                list.add(spi);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("[SpiLoader] ERROR: loadInstanceListSorted failed, exception:{}", e);
            return new ArrayList<>();
        }
    }

    /**
     * Load the sorted SPI instance list for provided SPI interface.
     *
     * @param clazz class of the SPI
     * @param <T>   SPI type
     * @return sorted SPI instance list
     */
    public static <T> List<T> loadInstanceListSorted(Class<T> clazz) {
        try {
            String key = clazz.getName();
            // Not thread-safe, as it's expected to be resolved in a thread-safe context.
            ServiceLoader<T> serviceLoader = SERVICE_LOADER_MAP.computeIfAbsent(key, loaderKey -> ServiceLoader.load(clazz));
            List<SpiOrderWrapper<T>> orderWrappers = new ArrayList<>();
            for (T spi : serviceLoader) {
                int order = SpiOrderResolver.resolveOrder(spi);
                // Since SPI is lazy initialized in ServiceLoader, we use online sort algorithm here.
                SpiOrderResolver.insertSorted(orderWrappers, spi, order);
                LOGGER.info("[SpiLoader] Found {} SPI: {} with order {}", order, clazz.getSimpleName(), spi.getClass().getCanonicalName());
            }
            List<T> list = new ArrayList<>();
            for (int i = 0; i < orderWrappers.size(); i++) {
                list.add(i, orderWrappers.get(i).spi);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("[SpiLoader] ERROR: loadInstanceListSorted failed, exception:{}", e);
            return new ArrayList<>();
        }
    }

    private static class SpiOrderResolver {
        private static <T> void insertSorted(List<SpiOrderWrapper<T>> list, T spi, int order) {
            int idx = 0;
            for (; idx < list.size(); idx++) {
                if (list.get(idx).getOrder() > order) {
                    break;
                }
            }
            list.add(idx, new SpiOrderWrapper<>(order, spi));
        }

        private static <T> int resolveOrder(T spi) {
            if (!spi.getClass().isAnnotationPresent(SpiOrder.class)) {
                // Lowest precedence by default.
                return SpiOrder.LOWEST_PRECEDENCE;
            } else {
                return spi.getClass().getAnnotation(SpiOrder.class).value();
            }
        }
    }

    private static class SpiOrderWrapper<T> {
        private final int order;
        private final T spi;

        SpiOrderWrapper(int order, T spi) {
            this.order = order;
            this.spi = spi;
        }

        int getOrder() {
            return order;
        }

        T getSpi() {
            return spi;
        }
    }

    private SpiLoader() {}

}
