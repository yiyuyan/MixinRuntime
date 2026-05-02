package cn.ksmcbrigade.mr.utils;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import cn.ksmcbrigade.mr.Constants;

import java.util.ArrayList;
import java.util.List;

public class InstUtils {

    public static List<ClassFileTransformerInfo> getTransformersInfo(Instrumentation inst, boolean retransformable){
        ArrayList<ClassFileTransformerInfo> transformerArrayList = new ArrayList<>();
        Object transformerManager;
        if(retransformable){
            transformerManager = UnsafeUtils.getFieldValue(inst,"mRetransfomableTransformerManager", Object.class);
            if(transformerManager==null){
                Constants.LOGGER.warn("The retransformable transformer manager of {} is null.",inst);
                return transformerArrayList;
            }
        }
        else{
            transformerManager = UnsafeUtils.getFieldValue(inst,"mTransformerManager", Object.class);
        }

        Object[] transformerInfo = UnsafeUtils.getFieldValue(transformerManager,"mTransformerList", Object[].class);
        if(transformerInfo==null){
            Constants.LOGGER.warn("The transformerInfo of {} is null.Retransformable: {}",inst,retransformable);
            return transformerArrayList;
        }

        for (Object o : transformerInfo) {
            transformerArrayList.add(
                    new ClassFileTransformerInfo(
                            UnsafeUtils.getFieldValue(o,"mTransformer", ClassFileTransformer.class),
                            UnsafeUtils.getFieldValue(o,"mPrefix", String.class)
                    )
            );
        }

        return transformerArrayList;
    }

    public static List<ClassFileTransformer> getTransformers(Instrumentation inst, boolean retransformable){
        return getTransformersInfo(inst,retransformable).stream().map((info)->info.classFileTransformer).toList();
    }

    public record ClassFileTransformerInfo(ClassFileTransformer classFileTransformer,String prefix){};
}