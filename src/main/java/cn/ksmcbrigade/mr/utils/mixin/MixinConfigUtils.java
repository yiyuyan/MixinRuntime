package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;

import java.util.List;
import java.util.Map;
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
}
