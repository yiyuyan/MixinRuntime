package cn.ksmcbrigade.mr.transformers;

import cn.ksmcbrigade.mr.Constants;
import cn.ksmcbrigade.mr.utils.mixin.MixinAgentUtils;
import org.apache.commons.io.FileUtils;
import org.spongepowered.tools.agent.MixinAgent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class HotMixinTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if(className==null || classfileBuffer==null || !className.endsWith("GL11")) return null;
            Constants.LOGGER.info("Redefining class {}", className);
            byte[] bytes = MixinAgentUtils.getTransformer(MixinAgentUtils.getFirstAgent()).transformClassBytes(className, className, classfileBuffer);
            FileUtils.writeByteArrayToFile(new File(className+".class"),bytes);
            return bytes;
        } catch (Throwable th) {
            Constants.LOGGER.error("Error while re-transforming class {}", className, th);
            return MixinAgent.ERROR_BYTECODE;
        }
    }
}
