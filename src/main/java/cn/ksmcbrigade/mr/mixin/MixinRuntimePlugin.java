package cn.ksmcbrigade.mr.mixin;

import cn.ksmcbrigade.mr.Constants;
import cn.ksmcbrigade.mr.utils.ModuleUtils;
import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import cn.ksmcbrigade.mr.utils.InstUtils;
import cn.ksmcbrigade.mr.utils.mixin.MixinAgentUtils;
import cn.ksmcbrigade.mr.utils.mixin.MixinUtils;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MixinRuntimePlugin implements IMixinConfigPlugin {

    public static boolean loaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        if(loaded) return;

        Constants.LOGGER.info("{} Loading...", MixinRuntimePlugin.class.getSimpleName());

        if(MixinRuntimePlugin.class.getClassLoader() instanceof TransformingClassLoader loader)
            System.getProperties().put("transforming_class_loader",loader);

        if(FMLLoader.isProduction()){
            UnsafeUtils.loadAgent(UnsafeUtils.getJarPath(MixinRuntimePlugin.class));
        }
        else{
            UnsafeUtils.loadAgent(
                    new File(System.getProperty("user.dir"))
                            .getParentFile()
                            .toPath()
                            .resolve("build")
                            .resolve("libs")
                            .resolve(Constants.MOD_FILE_NAME)
                            .toAbsolutePath()
                            .toString()
            );
        }

        Constants.LOGGER.info("Opening modules...");
        ModuleUtils.fixLwjglMixinAccess();
        ModuleUtils.openAllModules();

        try {
            MixinAgentUtils.initAndEnableMixinAgent();
            MixinUtils.fixClassLoader(Objects.requireNonNull(MixinAgentUtils.getInst()));

            System.out.println(Arrays.toString(InstUtils.getTransformers(MixinAgentUtils.getInst(),true).toArray()));
        } catch (Throwable e) {
           e.printStackTrace();
        }

        loaded = true;
        Constants.LOGGER.info("{} Loaded.", MixinRuntimePlugin.class.getSimpleName());
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        System.out.println("[MixinPlugin] shouldApplyMixin: " + mixinClassName + " -> " + targetClassName);
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
