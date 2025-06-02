package org.yangxc.operatoroverloading.core;

import org.yangxc.operatoroverloading.core.processor.MainProcessor;

import javax.annotation.processing.Processor;
import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessorDebug {

    public static void main(String[] args) throws Exception {
        // 代码
        String baseCode = "import org.yangxc.operatoroverloading.core.annotation.*;\n" +
                "import java.math.BigDecimal;\n" +
                "\n" +
                "@OperatorService(imports={BigDecimal.class})\n" +
                "public interface BaseService {\n" +
                "\n" +
                "    @ServiceFunction(value = \"s\")\n" +
                "    double multiCast(String s);\n" +
                "\n" +
                "}";
        // 执行
        compile(baseCode, new MainProcessor());

        // 清除编译结果
        Files.delete(Paths.get("BaseService.class"));
        Files.delete(Paths.get("BaseServiceImpl.java"));
        Files.delete(Paths.get("BaseServiceImpl.class"));
    }

    private static void compile(String code, Processor processor) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> listener = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(listener, Locale.CHINA, StandardCharsets.UTF_8)) {
            List<StringJavaObject> compilationUnits = Stream.of(new StringJavaObject("BaseService", code)).collect(Collectors.toList());
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, listener, Stream.of("-AOperatorOverloadingLog=debug").collect(Collectors.toList()), null, compilationUnits);
            task.setProcessors(Stream.of(processor).collect(Collectors.toList()));
            task.call();
            System.out.println("=== Diagnostics ===");
            listener.getDiagnostics().forEach(System.out::println);
        }
    }

    public static class StringJavaObject extends SimpleJavaFileObject {

        private final String code;

        public StringJavaObject(String name, String code) {
            super(URI.create("string:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return code;
        }

    }
}
