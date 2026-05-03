package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.utils.InstUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;
import org.spongepowered.tools.agent.MixinAgent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MixinTransformerUtils {

    public static IMixinTransformer getTransformer(MixinAgent agent){
        return MixinAgentUtils.getTransformer(agent);
    }

    public static IMixinTransformer getTransformer() {
        return getTransformer(MixinAgentUtils.getFirstAgent());
    }

    public static byte[] transform(Class<?> clazz) throws UnmodifiableClassException {
        return transform(MixinAgentUtils.getFirstAgent(),clazz);
    }

    public static byte[] transform(MixinAgent agent,Class<?> clazz) throws UnmodifiableClassException {
        return transform(getTransformer(agent),MixinAgentUtils.getInst(),clazz);
    }

    public static byte[] transform(IMixinTransformer transformer,Instrumentation inst,Class<?> clazz) throws UnmodifiableClassException {
        return transformer.transformClassBytes(clazz.getName(),clazz.getName(), InstUtils.getClassBytes(inst,clazz));
    }

    public static byte[] transformDirect(Class<?> clazz) throws UnmodifiableClassException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return transformDirect(MixinAgentUtils.getFirstAgent(),clazz);
    }

    public static byte[] transformDirect(MixinAgent agent,Class<?> clazz) throws UnmodifiableClassException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return transformDirect(MixinAgentUtils.getInst(),MixinAgentUtils.getTransformer(agent),clazz);
    }

    public static byte[] transformDirect(Instrumentation inst,IMixinTransformer transformer, Class<?> clazz) throws UnmodifiableClassException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String name = clazz.getName().replace(".","/");
        return transformClassBytesDirect(transformer,name,name, InstUtils.getClassBytes(inst,clazz));
    }

    public static byte[] transformClassBytesDirect(IMixinTransformer transformer,
                                                   String name, String transformedName, byte[] basicClass) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (transformedName == null) {
            return basicClass;
        }

        MixinEnvironment environment = MixinEnvironment.getCurrentEnvironment();

        if (basicClass == null) {
            return transformer.generateClass(environment, transformedName);
        }

        return transformClassBytesDirect(transformer, transformedName, basicClass);
    }

    public static byte[] transformClassBytesDirect(IMixinTransformer transformer,
                                                   String name, byte[] classBytes) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ClassNode classNode = readClass(transformer,name, classBytes);
        return writeClass(transformer,classNode);
    }

    public static ClassNode readClass(IMixinTransformer transformer,String name,byte[] classBytes) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = TreeTransformer.class.getDeclaredMethod("readClass", String.class, byte[].class);
        method.setAccessible(true);
        return (ClassNode) method.invoke(transformer,name,classBytes);
    }

    public static byte[] writeClass(IMixinTransformer transformer,ClassNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = TreeTransformer.class.getDeclaredMethod("writeClass", ClassNode.class);
        method.setAccessible(true);
        return (byte[]) method.invoke(transformer,node);
    }
}