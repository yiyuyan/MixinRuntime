package cn.ksmcbrigade.mr.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;

public class VMUtils {

    public static void restartJVM0(String... addArgs) throws IOException, InterruptedException {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        ProcessBuilder builder = new ProcessBuilder(
                "wmic","process",
                String.valueOf(runtimeMXBean.getPid()),
                "get","commandline");
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            String[] args = output.substring(11).strip().split(" ");
            String[] newArgs = new String[addArgs.length+args.length];
            newArgs[0]=args[0];
            System.arraycopy(addArgs, 0, newArgs, 1, addArgs.length);
            System.arraycopy(args, 1, newArgs, 1 + addArgs.length, args.length - 1);
            File file = new File(RandomStringUtils.randomNumeric(8)+".cmd");
            file.deleteOnExit();
            FileUtils.writeStringToFile(file, "@echo off\n" + toStringArgs(newArgs), Charset.defaultCharset());
            ProcessBuilder processBuilder = new ProcessBuilder(file.getAbsolutePath());
            processBuilder.inheritIO();
            processBuilder.start().waitFor();
            System.exit(0);
        }

        process.waitFor();
    }

    private static String toStringArgs(String[] arguments) {
        StringBuilder builder = new StringBuilder();
        for (String inputArgument : arguments) {
            if(inputArgument.isEmpty()) continue;
            if(!builder.isEmpty()) builder.append(" ");
            builder.append(inputArgument);
        }
        return builder.toString();
    }

    public static void restartJVM(String... args) throws IOException, InterruptedException {
        if(System.getProperty("restartedJVM")!=null) return;
        restartJVM0(args);
    }
}
