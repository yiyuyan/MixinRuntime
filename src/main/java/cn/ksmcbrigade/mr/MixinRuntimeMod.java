package cn.ksmcbrigade.mr;

import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod("mr")
public class MixinRuntimeMod {

    public MixinRuntimeMod() throws IOException, InterruptedException {
        System.out.println("MixinRuntimeMod Loaded.");
    }
}
