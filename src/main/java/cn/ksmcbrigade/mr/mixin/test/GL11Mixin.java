package cn.ksmcbrigade.mr.mixin.test;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.NativeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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

    /**
     * @author KSMc_brigade
     * @reason re
     */
    @Overwrite(remap = false)
    public static void glDepthFunc(@NativeType("GLenum") int func) {
        System.out.println("RuntimeInject successfully2222222222222!!!!");
        GL11C.glDepthFunc(func);
    }
}
