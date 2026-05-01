package cn.ksmcbrigade.mr.mixin;

import cn.ksmcbrigade.mr.Constants;
import cn.ksmcbrigade.mr.MixinRuntimeMod;
import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import cn.ksmcbrigade.mr.utils.mixin.MixinAgentUtils;
import cpw.mods.modlauncher.Launcher;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        Constants.LOGGER.info("{} Loading...", MixinRuntimeMod.class.getSimpleName());

        System.getProperties().put("launcher",Launcher.INSTANCE);
        if(FMLLoader.isProduction()){
            UnsafeUtils.loadAgent(UnsafeUtils.getJarPath(MixinPlugin.class));
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

        MixinAgentUtils.attachAgent();
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return false;
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
