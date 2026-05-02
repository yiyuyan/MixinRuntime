package cn.ksmcbrigade.mr.transformers;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ModLauncherClassTrackerTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className != null && classfileBuffer != null &&
                className.startsWith("org/spongepowered/asm/service/modlauncher/ModLauncherClassTracker")) {

            System.out.println("[MixinRuntimeAgent] Fixing " + className);

            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

                ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                     String signature, String[] exceptions) {
                        if (name.equals("handlesClass") &&
                                descriptor.equals("(Lorg/objectweb/asm/Type;ZLjava/lang/String;)Ljava/util/EnumSet;")) {

                            System.out.println("[MixinRuntimeAgent] Replacing handlesClass method");

                            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
                            mv.visitCode();
                            mv.visitFieldInsn(Opcodes.GETSTATIC,
                                    "org/spongepowered/asm/launch/Phases",
                                    "AFTER_ONLY",
                                    "Ljava/util/EnumSet;");
                            mv.visitInsn(Opcodes.ARETURN);
                            mv.visitMaxs(1, 4);
                            mv.visitEnd();

                            return null;
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                };

                reader.accept(cv, ClassReader.EXPAND_FRAMES);
                return writer.toByteArray();

            } catch (Throwable e) {
                System.err.println("[MixinRuntimeAgent] Error transforming class:");
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}