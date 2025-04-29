package org.yangxc.core;

import org.yangxc.core.processor.ServiceProcessor;

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
        // 基础
        String baseCode = """
                import org.yangxc.core.annotation.OperatorFunction;
                import org.yangxc.core.annotation.OperatorService;
                import org.yangxc.core.annotation.Statement;
                
                import java.math.BigDecimal;
                
                @OperatorService
                public interface BaseService {
                
                    @OperatorFunction(
                            statements = {
                                    @Statement(type = BigDecimal.class, varName = "a", exp = "1*2"),
                                    @Statement(type = BigDecimal.class, varName = "b", exp = "1*2")
                            },
                            value = "a+b"
                    )
                    BigDecimal var();
                
                }
                """;
        // 执行
        compile(baseCode, new ServiceProcessor());

        // 清除编译结果
        Files.delete(Paths.get("BaseService.class"));
    }

    private static void compile(String code, Processor processor) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> listener = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(listener, Locale.CHINA, StandardCharsets.UTF_8)) {
            List<StringJavaObject> compilationUnits = List.of(new StringJavaObject("BaseService", code));
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, listener, null, null, compilationUnits);
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
