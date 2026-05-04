package cn.ksmcbrigade.mr.transformers.debug;

import cn.ksmcbrigade.mr.Constants;
import cn.ksmcbrigade.mr.utils.mixin.MixinAgentUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Objects;

public class HotMixinTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if(className==null || classfileBuffer==null) return null;

        boolean shouldTransform = className.endsWith("GL11") || className.endsWith("GL30");
        if(!shouldTransform) return null;

        Constants.LOGGER.info("Redefining class {} with loader: {}", className, loader);

        try {
            byte[] bytes = Objects.requireNonNull(MixinAgentUtils.getTransformer(MixinAgentUtils.getFirstAgent()))
                    .transformClassBytes(className, className, classfileBuffer);

            if (bytes.length != classfileBuffer.length) {
                Constants.LOGGER.info("GL11 transformed successfully! Length: {} -> {}",
                        classfileBuffer.length, bytes.length);
            } else {
                Constants.LOGGER.warn("GL11 was NOT transformed! Length unchanged: {}", bytes.length);
            }

            try {
                File debugFile = new File("agent_"+className.replace('/', '.') + ".class");
                FileUtils.writeByteArrayToFile(debugFile, bytes);
            } catch (Exception e) {
                Constants.LOGGER.error("Failed to save file", e);
            }

            return bytes;

        } catch (Throwable th) {
            Constants.LOGGER.error("Error while re-transforming class {}", className, th);

            // 打印完整堆栈
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            th.printStackTrace(pw);
            Constants.LOGGER.error("Full stack trace:\n{}", sw.toString());

            return null; // 返回 null 保持原始类
        }
    }
}