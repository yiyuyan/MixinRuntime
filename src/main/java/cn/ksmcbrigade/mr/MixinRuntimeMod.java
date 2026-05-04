package cn.ksmcbrigade.mr;

import net.neoforged.fml.common.Mod;

@Mod("mr")
public class MixinRuntimeMod {

    public MixinRuntimeMod() {
        Constants.LOGGER.info("{} Loaded.",MixinRuntimeMod.class.getSimpleName());
    }
}
