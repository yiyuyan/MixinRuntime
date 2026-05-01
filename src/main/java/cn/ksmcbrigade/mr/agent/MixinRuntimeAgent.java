package cn.ksmcbrigade.mr.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.instrument.Instrumentation;

public class MixinRuntimeAgent {

    public static Logger LOGGER = LogManager.getLogger(MixinRuntimeAgent.class);

    public static void agentmain(String args, Instrumentation inst){
        LOGGER.info("{} Loading...",MixinRuntimeAgent.class.getSimpleName());

        System.getProperties().put("inst",inst);

        LOGGER.info("{} Loaded.",MixinRuntimeAgent.class.getSimpleName());
    }

    public static void premain(String args,Instrumentation inst){
        agentmain(args, inst);
    }
}
