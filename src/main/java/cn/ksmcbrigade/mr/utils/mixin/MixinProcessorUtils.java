package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.tools.agent.MixinAgent;

import java.util.List;

public class MixinProcessorUtils {

    public static Object getProcessor(MixinAgent agent){
        return getProcessor(MixinAgentUtils.getTransformer(agent));
    }

    public static Object getProcessor(IMixinTransformer iTransformer){
        return UnsafeUtils.getFieldValue(iTransformer,"processor",Object.class);
    }

    //List<MixinConfig>
    public static List<Object> getConfigs(Object processor){
        return UnsafeUtils.getFieldValue(processor,"configs",List.class);
    }

    //List<MixinConfig>
    public static List<Object> getPendingConfigs(Object processor){
        return UnsafeUtils.getFieldValue(processor,"pendingConfigs",List.class);
    }

    //List<MixinConfig>
    public static void setConfigs(Object processor, List<Object> configs){
        UnsafeUtils.setFieldValue(processor,"configs",configs);
    }

    //List<MixinConfig>
        public static void setPendingConfigs(Object processor,List<Object> configs){
        UnsafeUtils.setFieldValue(processor,"pendingConfigs",configs);
    }

    public static void addIntoProcessor(Object processor,IMixinConfig config){
        List<Object> configs = getConfigs(processor);
        if (configs != null) {
            configs.add(config);
        }
        setPendingConfigs(processor,configs);
        setConfigs(processor,configs);
    }
}
