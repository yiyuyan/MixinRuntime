package cn.ksmcbrigade.mr.transformers.util;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ClassByteGetter implements ClassFileTransformer {

    public static final byte[] WAITING_BYTES = new byte[]{1};

    private final Class<?> clazz;
    public byte[] bytes = WAITING_BYTES;

    public ClassByteGetter(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(clazz.equals(classBeingRedefined)){
            bytes = classfileBuffer;

            Object inst = System.getProperties().getOrDefault("inst",null);
            if(inst instanceof Instrumentation instrumentation){
                instrumentation.removeTransformer(this);
            }
        }

        return classfileBuffer;
    }
}
