package org.yangxc.operatoroverloading.core;

import org.yangxc.operatoroverloading.core.processor.ServiceProcessor;

import javax.annotation.processing.Processor;
import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class ProcessorDebug {

    public static void main(String[] args) throws Exception {
        // 代码
        String baseCode = """
                import org.yangxc.operatoroverloading.core.annotation.*;
                import java.math.BigDecimal;
                
                @OperatorService(imports={BigDecimal.class})
                public interface BaseService {
                

                    @OperatorFunction("a*Complex.I")
                    Complex rotation90(Complex a);
                
                }
                """;
        // 执行
        compile(baseCode, new ServiceProcessor());

        // 清除编译结果
        Files.delete(Paths.get("BaseService.class"));
        Files.delete(Paths.get("BaseServiceImpl.java"));
        Files.delete(Paths.get("BaseServiceImpl.class"));
    }

    private static void compile(String code, Processor processor) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> listener = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(listener, Locale.CHINA, StandardCharsets.UTF_8)) {
            List<StringJavaObject> compilationUnits = List.of(new StringJavaObject("BaseService", code));
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, listener, List.of("-AOperatorOverloadingLog=debug"), null, compilationUnits);
            task.setProcessors(List.of(processor));
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
