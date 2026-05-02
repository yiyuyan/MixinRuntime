package cn.ksmcbrigade.mr;

import cn.ksmcbrigade.mr.utils.mixin.MixinUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod("mr")
public class MixinRuntimeMod {

    public MixinRuntimeMod() {

        try {
            if(!FMLLoader.isProduction())MixinUtils.getMixins("mr.mixins.json");
        } catch (Throwable e) {
            e.printStackTrace();
        }


        Constants.LOGGER.info("{}Main Loaded.",MixinRuntimeMod.class.getSimpleName());
    }
}
