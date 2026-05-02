package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;
import org.spongepowered.tools.agent.MixinAgent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MixinConfigUtils {
    public static Map<String,Object> getDecorations(IMixinConfig iMixinConfig){
        return UnsafeUtils.getFieldValue(iMixinConfig,"decorations",Map.class);
    }

    public static List<String> getMixins(IMixinConfig iMixinConfig){
        return (List<String>) UnsafeUtils.getFieldValue(iMixinConfig,"mixinClasses", List.class);
    }

    public static List<String> getMixinsClient(IMixinConfig iMixinConfig){
        return (List<String>) UnsafeUtils.getFieldValue(iMixinConfig,"mixinClassesClient", List.class);
    }

    public static List<String> getMixinsServer(IMixinConfig iMixinConfig){
        return (List<String>) UnsafeUtils.getFieldValue(iMixinConfig,"mixinClassesServer", List.class);
    }

    public static Set<String> getUnhandledTargets(IMixinConfig iMixinConfig){
        return UnsafeUtils.getFieldValue(iMixinConfig,"unhandledTargets", Set.class);
    }

    public static Set<String> getGlobalMixinList(IMixinConfig iMixinConfig){
        return UnsafeUtils.getFieldValue(iMixinConfig,"globalMixinList", Set.class);
    }

    public static void onSelect(IMixinConfig config) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = config.getClass().getDeclaredMethod("onSelect");
        method.setAccessible(true);
        method.invoke(config);
    }

    public static void prepare(IMixinConfig config, MixinAgent agent) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        prepare(config, Objects.requireNonNull(MixinAgentUtils.getTransformer(agent)));
    }

    public static void prepare(IMixinConfig config, IMixinTransformer transformer) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        prepare(config,transformer.getExtensions());
    }

    public static void prepare(IMixinConfig config, IExtensionRegistry extensions) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = config.getClass().getDeclaredMethod("prepare", Extensions.class);
        method.setAccessible(true);
        method.invoke(config,extensions);
    }
}
