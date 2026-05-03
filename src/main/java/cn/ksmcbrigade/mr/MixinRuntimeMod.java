package cn.ksmcbrigade.mr;

import cn.ksmcbrigade.mr.utils.mixin.MixinUtils;
import net.neoforged.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod("mr")
public class MixinRuntimeMod {

    public MixinRuntimeMod() {

        try {
            MixinUtils.getMixins("mr.mixins.json");
            GL11.glCullFace(-111);
        } catch (Throwable e) {
            e.printStackTrace();
        }


        Constants.LOGGER.info("{} Loaded.",MixinRuntimeMod.class.getSimpleName());
    }
}
