package cn.ksmcbrigade.mr.transformers;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class FMLClassTrackerTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className != null && classfileBuffer != null &&
                className.startsWith("net/neoforged/fml/loading/mixin/FMLClassTracker")) {

            System.out.println("[MixinRuntimeAgent] Fixing " + className);

            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

                ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                     String signature, String[] exceptions) {
                        if (name.equals("isInvalidClass") || name.equals("isClassLoaded")) {

                            System.out.println("[MixinRuntimeAgent] Replacing "+ name + " method");

                            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
                            mv.visitCode();
                            mv.visitInsn(Opcodes.ICONST_0);
                            mv.visitInsn(Opcodes.IRETURN);
                            mv.visitMaxs(1, 1);
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