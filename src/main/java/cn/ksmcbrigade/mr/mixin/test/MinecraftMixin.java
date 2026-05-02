package cn.ksmcbrigade.mr.mixin.test;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ModCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    /**
     * @author KSMc_brigade
     * @reason test
     */
    @Overwrite
    public static ModCheck checkModStatus() {
        return new ModCheck(ModCheck.Confidence.PROBABLY_NOT,"Probably not.");
    }
}
