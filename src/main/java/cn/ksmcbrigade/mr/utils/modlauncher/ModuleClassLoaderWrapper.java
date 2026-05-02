package cn.ksmcbrigade.mr.utils.modlauncher;

import cn.ksmcbrigade.mr.Constants;
import cpw.mods.modlauncher.api.ITransformerActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModuleClassLoaderWrapper {

    private static Object cachedLoader = null;

    private static Object getTransformingClassLoader() {
        if (cachedLoader != null) {
            return cachedLoader;
        }

        Object loader = System.getProperties().getOrDefault("transforming_class_loader", null);

        if (loader != null) {
            cachedLoader = loader;
            return cachedLoader;
        }

        return cachedLoader;
    }

    //ModuleClassLoaderTransformer
    public static byte[] maybeTransformClassBytes(final byte[] bytes, final String name, final String context) {
        Object transformingClassLoader = getTransformingClassLoader();

        if (transformingClassLoader != null) {
            try {

                Method method = transformingClassLoader.getClass().getDeclaredMethod(
                        "maybeTransformClassBytes", byte[].class, String.class, String.class);
                method.setAccessible(true);

                return (byte[]) method.invoke(
                        transformingClassLoader,
                        bytes,
                        name,
                        context != null ? context : ITransformerActivity.CLASSLOADING_REASON
                );
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Constants.LOGGER.warn("error in wrapper class loader for class: {}", name, e);
                return bytes;
            }
        } else {
            Constants.LOGGER.warn("Cannot find TransformingClassLoader, class: {}", name);
            return bytes;
        }
    }
}