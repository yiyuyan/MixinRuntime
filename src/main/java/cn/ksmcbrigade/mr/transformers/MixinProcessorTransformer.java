package cn.ksmcbrigade.mr.transformers;

import org.objectweb.asm.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class MixinProcessorTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className != null && classfileBuffer != null &&
                className.equals("org/spongepowered/asm/mixin/transformer/MixinProcessor")) {

            System.out.println("[MixinRuntimeAgent] Fixing " + className);

            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

                ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                     String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                        if (name.equals("applyMixins")) {
                            return new MethodVisitor(Opcodes.ASM9, mv) {
                                private boolean nopMode = false;

                                @Override
                                public void visitTypeInsn(int opcode, String type) {
                                    if (opcode == Opcodes.NEW &&
                                            type.equals("org/spongepowered/asm/mixin/transformer/throwables/IllegalClassLoadError")) {
                                        nopMode = true;
                                    }
                                    if (nopMode) {
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitTypeInsn(opcode, type);
                                }

                                @Override
                                public void visitMethodInsn(int opcode, String owner, String name,
                                                            String descriptor, boolean isInterface) {
                                    if (nopMode) {
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                }

                                @Override
                                public void visitInsn(int opcode) {
                                    if (nopMode) {
                                        if (opcode == Opcodes.ATHROW) {
                                            nopMode = false;
                                        }
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitInsn(opcode);
                                }

                                @Override
                                public void visitVarInsn(int opcode, int var) {
                                    if (nopMode) {
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitVarInsn(opcode, var);
                                }

                                @Override
                                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                    if (nopMode) {
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitFieldInsn(opcode, owner, name, descriptor);
                                }

                                @Override
                                public void visitLdcInsn(Object value) {
                                    if (nopMode) {
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitLdcInsn(value);
                                }

                                @Override
                                public void visitJumpInsn(int opcode, Label label) {
                                    if (nopMode) {
                                        mv.visitInsn(Opcodes.NOP);
                                        return;
                                    }
                                    super.visitJumpInsn(opcode, label);
                                }

                                @Override
                                public void visitLabel(Label label) {
                                    super.visitLabel(label);
                                }

                                @Override
                                public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
                                    super.visitFrame(type, numLocal, local, numStack, stack);
                                }

                                @Override
                                public void visitMaxs(int maxStack, int maxLocals) {
                                    super.visitMaxs(maxStack, maxLocals);
                                }
                            };
                        }
                        return mv;
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