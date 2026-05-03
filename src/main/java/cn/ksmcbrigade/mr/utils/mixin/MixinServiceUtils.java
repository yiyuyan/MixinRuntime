package cn.ksmcbrigade.mr.utils.mixin;

import cn.ksmcbrigade.mr.utils.UnsafeUtils;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MixinServiceUtils {

    @Nullable
    public static MixinService getMixinServiceDirect(){
        return UnsafeUtils.getFieldValue(MixinService.class,"instance", MixinService.class);
    }

    public static MixinService getMixinService() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MixinService.class.getDeclaredMethod("getInstance");
        method.setAccessible(true);
        return (MixinService) method.invoke(null);
    }

    @Nullable
    public static IMixinService getServiceDirect(MixinService service){
        return UnsafeUtils.getFieldValue(service,"service",IMixinService.class);
    }

    @Nullable
    public static IGlobalPropertyService getGlobalServiceDirect(MixinService service){
        return UnsafeUtils.getFieldValue(service,"propertyService",IGlobalPropertyService.class);
    }

    public static void setGlobalService(MixinService service,IGlobalPropertyService globalPropertyService){
        UnsafeUtils.setFieldValue(service,"propertyService",globalPropertyService);
    }

    public static void setService(MixinService service,IMixinService iMixinService){
        UnsafeUtils.setFieldValue(service,"service",iMixinService);
    }
}
