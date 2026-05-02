package cn.ksmcbrigade.mr.mixin;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.NativeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GL11.class)
public class GL11Mixin {
    /**
     * @author KSmc_brigade
     * @reason test
     */
    @Overwrite(remap = false)
    public static void glCullFace(@NativeType("GLenum") int mode) {
        System.out.println("RuntimeInject successfully!!!!");
        if(mode==-111) return;
        GL11C.glCullFace(mode);
    }

    @Inject(remap = false,method = "glDepthFunc",at = @At("HEAD"))
    private static void glDepth(int func, CallbackInfo ci) {
        System.out.println("RuntimeInject successfully2222222222222!!!!");
    }
}
