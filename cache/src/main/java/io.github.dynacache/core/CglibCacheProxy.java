package io.github.dynacache.core;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Getting Cache Proxy Objects through cglib Dynamic Proxy
 *
 * @author longqiang
 */
public class CglibCacheProxy {

    @SuppressWarnings("unchecked")
    public static <T> T proxy(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> intercept(obj, method, args, proxy));
        return  (T) enhancer.create();
    }

    private static Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
        Cache cache = method.getAnnotation(Cache.class);
        CglibKeyAndArgs keyAndArgs = CglibKeyAndArgs.newBuilder()
                                                    .setArgs(args)
                                                    .setTarget(obj)
                                                    .setProxy(proxy)
                                                    .buildWithAnnotation(cache);
        return CaffeineManager.get(keyAndArgs);
    }

}
