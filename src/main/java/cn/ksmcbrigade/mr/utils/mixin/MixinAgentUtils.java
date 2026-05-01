package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import cpw.mods.modlauncher.Launcher;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.service.modlauncher.Blackboard;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;
import org.spongepowered.tools.agent.MixinAgent;

import javax.annotation.Nullable;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MixinAgentUtils {
    public static List<MixinAgent> getAgents(){
        Object agents = UnsafeUtils.getFieldValue(MixinAgent.class,"agents",Object.class);
        if(agents instanceof List){
            return (List<MixinAgent>) agents;
        }
        else{
            return new ArrayList<>();
        }
    }

    public static void setAgents(List<MixinAgent> agents) throws IllegalAccessException {
        if(agents!=null){
            for (Field declaredField : MixinAgent.class.getDeclaredFields()) {
                if(declaredField.getType().equals(List.class)){
                    declaredField.setAccessible(true);
                    declaredField.set(null,agents);
                    break;
                }
            }
        }
    }

    public static void setInst(Instrumentation inst) throws IllegalAccessException {
        for (Field declaredField : MixinAgent.class.getDeclaredFields()) {
            if(declaredField.getType().equals(Instrumentation.class)){
                declaredField.setAccessible(true);
                declaredField.set(null,inst);
                break;
            }
        }
    }

    @Nullable
    public static Instrumentation getInst(){
        return UnsafeUtils.getFieldValue(MixinAgent.class,"instrumentation",Instrumentation.class);
    }

    @Nullable
    public static IMixinTransformer getTransformer(MixinAgent agent){
        return UnsafeUtils.getFieldValue(agent,"classTransformer", IMixinTransformer.class);
    }

    public static void initAgent(MixinAgent agent) throws Throwable {
        Method method = MixinAgent.class.getDeclaredMethod("initTransformer");
        method.setAccessible(true);
        method.invoke(agent);
    }

    public static MixinAgent create() throws Throwable{
        Object mixinTransformer;
        if(MixinEnvironment.getCurrentEnvironment().getActiveTransformer() instanceof IMixinTransformer iMixinTransformer){
            mixinTransformer = iMixinTransformer;
        }
        else{
            Class<?> mixinTransformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
            Constructor<?> constructor = mixinTransformerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            mixinTransformer = constructor.newInstance();
        }

        Constructor<?> mixinAgentConstructor = MixinAgent.class.getDeclaredConstructor(IMixinTransformer.class);
        mixinAgentConstructor.setAccessible(true);
        return (MixinAgent) mixinAgentConstructor.newInstance(mixinTransformer);
    }

    public static void setSystemConfigs(){
        System.setProperty("mixin.hotSwap", "true");
        System.setProperty("mixin.debug.hotswap","true");
        System.setProperty("mixin.debug.export", "true");
        System.setProperty("mixin.dumpTargetOnFailure", "true");
        System.setProperty("mixin.debug","true");
        System.setProperty("mixin.debug.export.decompile","true");
        System.setProperty("mixin.debug.export.path=","./mixin_debug");
        System.setProperty("mixin.logging.level","DEBUG");
        System.setProperty("mixin.debug.verbose","true");
    }

    public static void attachAgent(){

        setSystemConfigs();

        try {
            List<MixinAgent> agentList = MixinAgentUtils.getAgents();

            MixinService service = MixinServiceUtils.getMixinService();
            if(MixinServiceUtils.getServiceDirect(service)==null){
                MixinServiceUtils.setService(service,new MixinServiceModLauncher());
            }
            if(MixinServiceUtils.getGlobalServiceDirect(service)==null){
                if(Launcher.INSTANCE==null) Launcher.main(ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]));
                MixinServiceUtils.setGlobalService(service,new Blackboard());
            }

            if(MixinAgentUtils.getInst()==null){
                try {
                    MixinAgentUtils.setInst((Instrumentation) System.getProperties().get("inst"));
                } catch (Exception e) {
                    throw e;
                }
            }

            if(agentList.isEmpty()){
                agentList.add(MixinAgentUtils.create());
                MixinAgentUtils.setAgents(agentList);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to init and apply the MixinAgent.");
        }
    }
}
