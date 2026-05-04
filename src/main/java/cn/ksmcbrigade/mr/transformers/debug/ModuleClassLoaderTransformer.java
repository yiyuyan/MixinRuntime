package cn.ksmcbrigade.mr.transformers.debug;

import org.objectweb.asm.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ModuleClassLoaderTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className != null && classfileBuffer != null &&
                className.equals("net/neoforged/fml/classloading/ModuleClassLoader")) {


            System.out.println("[MixinRuntimeAgent] Patching ModuleClassLoader::maybeTransformClassBytes");

            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

                ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                     String signature, String[] exceptions) {
                        if (name.equals("maybeTransformClassBytes") &&
                                descriptor.equals("([BLjava/lang/String;Ljava/lang/String;)[B")) {

                            System.out.println("[MixinRuntimeAgent] Replacing maybeTransformClassBytes method");

                            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
                            mv.visitCode();

                            mv.visitVarInsn(Opcodes.ALOAD, 1);
                            mv.visitVarInsn(Opcodes.ALOAD, 2);
                            mv.visitVarInsn(Opcodes.ALOAD, 3);

                            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "cn/ksmcbrigade/mr/utils/modlauncher/ModuleClassLoaderWrapper",
                                    "maybeTransformClassBytes",
                                    "([BLjava/lang/String;Ljava/lang/String;)[B",
                                    false);

                            mv.visitInsn(Opcodes.ARETURN);
                            mv.visitMaxs(3, 4);
                            mv.visitEnd();

                            return null;
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                };

                reader.accept(cv, ClassReader.EXPAND_FRAMES);
                byte[] transformed = writer.toByteArray();
                System.out.println("[MixinRuntimeAgent] ModuleClassLoader patched successfully");
                return transformed;

            } catch (Throwable e) {
                System.err.println("[MixinRuntimeAgent] Error patching ModuleClassLoader:");
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}