package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.Constants;
import cn.ksmcbrigade.mr.transformers.MixinProcessorTransformer;
import cn.ksmcbrigade.mr.transformers.ModLauncherClassTrackerTransformer;
import cpw.mods.cl.ModuleClassLoader;
import cpw.mods.modlauncher.TransformingClassLoader;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.service.modlauncher.ModLauncherClassTracker;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MixinUtils {

    public static void fixClassLoader(Instrumentation inst) throws Exception {

        if(MixinUtils.class.getClassLoader() instanceof TransformingClassLoader loader)
            System.getProperties().put("transforming_class_loader",loader);

        //inst.addTransformer(new ModuleClassLoaderTransformer(),true);

        inst.addTransformer(new ModLauncherClassTrackerTransformer(),true);
        inst.addTransformer(new MixinProcessorTransformer(),true);

        //if(!FMLLoader.isProduction())inst.addTransformer(new HotMixinTransformer(),true);

        inst.retransformClasses(ModuleClassLoader.class);
        inst.retransformClasses(ModLauncherClassTracker.class);
        inst.retransformClasses(Class.forName("org.spongepowered.asm.mixin.transformer.MixinProcessor"));
    }

    public static Config toConfig(String configFile){return Config.create(configFile, MixinEnvironment.getCurrentEnvironment());}

    public static void getMixins(String configFile) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Config config = toConfig(configFile);
        IMixinConfig iMixinConfig = config.getConfig();

        MixinConfigUtils.onSelect(iMixinConfig);
        MixinConfigUtils.prepare(iMixinConfig,MixinAgentUtils.getFirstAgent());
        MixinProcessorUtils.addIntoProcessor(MixinProcessorUtils.getProcessor(MixinAgentUtils.getFirstAgent()),iMixinConfig);

        for (String s : MixinConfigUtils.getGlobalMixinList(iMixinConfig)) {
            try {
                for (Class<?> targetClass : getTargetClasses(Class.forName(s))) {
                    Constants.LOGGER.info("mixin utils redefining {}",targetClass);
                    byte[] bytes = MixinTransformerUtils.transform(targetClass);
                    //FileUtils.writeByteArrayToFile(new File(targetClass.getName()+".class"),bytes);
                    Objects.requireNonNull(MixinAgentUtils.getInst()).redefineClasses(
                            new ClassDefinition(
                                    targetClass,
                                    bytes
                            )
                    );
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void reapply(Class<?> mixinClass){
        for (Class<?> targetClass : getTargetClasses(mixinClass)) {
            try {
                Objects.requireNonNull(MixinAgentUtils.getInst()).redefineClasses(
                        new ClassDefinition(
                                targetClass,
                                MixinTransformerUtils.transform(targetClass)
                        )
                );
            } catch (ClassNotFoundException | UnmodifiableClassException e) {
                Constants.LOGGER.error("Failed to redefine class: {}",targetClass);
            }
        }
    }

    public static List<Class<?>> getTargetClasses(Class<?> mixinClass) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (String string : getMixinTargetsByASM(mixinClass)) {
            try {
                classes.add(Class.forName(string));
            }
            catch (Throwable e){
                Constants.LOGGER.error(" Can't get the target class for the mixin class {} : {}", mixinClass, string,e);
            }
        }
        return classes;
    }

    public static List<String> getMixinTargetsByASM(Class<?> mixinClass) {
        List<String> targets = new ArrayList<>();

        try {
            ClassReader reader = new ClassReader(mixinClass.getName());
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            if (classNode.invisibleAnnotations != null) {
                for (AnnotationNode annotation : classNode.invisibleAnnotations) {
                    if (annotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                        if (annotation.values != null) {
                            for (int i = 0; i < annotation.values.size(); i += 2) {
                                String key = (String) annotation.values.get(i);
                                Object value = annotation.values.get(i + 1);

                                if ("value".equals(key)) {
                                    List<?> typeArray = (List<?>) value;
                                    for (Object typeObj : typeArray) {
                                        if(typeObj instanceof Type type){
                                            targets.add(type.getClassName());
                                        }
                                    }
                                } else if ("targets".equals(key)) {
                                    List<?> stringArray = (List<?>) value;
                                    for (Object strObj : stringArray) {
                                        if(strObj instanceof String s){
                                            targets.add(s);
                                        }
                                        if(strObj instanceof Type type){
                                            targets.add(type.getClassName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable e){
            e.printStackTrace();
        }

        return targets;
    }

}
