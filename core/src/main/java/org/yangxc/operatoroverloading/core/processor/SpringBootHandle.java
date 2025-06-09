package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext.TAB;

public class SpringBootHandle implements ProcessorHandle {

    public static final String SPRING_BOOT_TYPE = "OperatorOverloadingSpringBoot";
    private static final String AUTO = "auto";
    private static final String NONE = "none";
    private static final String CONFIG = "config";

    private static final String CONFIG_CLASS_NAME = "OperatorOverloadingConfiguration";
    private static final String MAIN_ANNOTATION = "org.springframework.boot.autoconfigure.SpringBootApplication";
    private static final String CONFIG_ANNOTATION_QUALIFIED_NAME = "org.springframework.boot.SpringBootConfiguration";
    private static final String CONFIG_ANNOTATION_NAME = "@SpringBootConfiguration";
    private static final String BEAN_ANNOTATION_QUALIFIED_NAME = "org.springframework.context.annotation.Bean";
    private static final String BEAN_ANNOTATION_NAME = "@Bean";

    private final ProcessingEnvironment processingEnv;
    private final String handle;
    private TypeElement mainClass;

    public SpringBootHandle(ProcessingEnvironment processingEnv, String handle) {
        this.processingEnv = processingEnv;
        handle = handle != null ? handle : AUTO;
        if (Stream.of(AUTO, NONE, CONFIG).noneMatch(handle::equals)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "unknown " + SPRING_BOOT_TYPE + ": " + handle);
            handle = AUTO;
        }
        this.handle = handle;
    }

    @Override
    public void startRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (NONE.equals(handle)) {
            return;
        }
        mainClass = roundEnv.getRootElements().stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .filter(this::isMainClass)
                .findAny()
                .orElse(null);
        if (mainClass == null) {
            LogHandle.print(Diagnostic.Kind.NOTE, "not find spring boot main class");
        } else {
            LogHandle.print(Diagnostic.Kind.NOTE, "find spring boot main class", mainClass);
        }
    }

    private boolean isMainClass(TypeElement typeElement) {
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            if (MAIN_ANNOTATION.equals(annotationMirror.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postAllWrite(List<ServiceHandle> serviceHandles, OverloadingContext overloadingContext, VariableSetContext variableSetContext) {
        if (mainClass == null) {
            return;
        }
        try {
            write(serviceHandles);
        } catch (ElementException e) {
            LogHandle.print(Diagnostic.Kind.ERROR, "write error: " + e.getMessage(), e.getElement());
        } catch (Exception e) {
            LogHandle.print(Diagnostic.Kind.ERROR, "write error: " + e.getMessage(), mainClass);
        }
    }

    private void write(List<ServiceHandle> serviceHandles) throws IOException {
        String mainClassName = mainClass.getQualifiedName().toString();
        int index = mainClassName.lastIndexOf('.');
        String pack = index != -1 ? mainClassName.substring(0, index) : "";
        String packCode = index != -1 ? ("package " + pack + ";\n") : "";
        String qualifiedName = index != -1 ? (pack + "." + CONFIG_CLASS_NAME) : CONFIG_CLASS_NAME;
        ImportContext importContext = new ImportContext(Stream.concat(
                serviceHandles.stream().map(ServiceHandle::getQualifiedName),
                Stream.of(BEAN_ANNOTATION_QUALIFIED_NAME,
                        CONFIG_ANNOTATION_QUALIFIED_NAME,
                        ReflectiveOperationException.class.getTypeName(),
                        Generated.class.getTypeName()
                )
        ).collect(Collectors.toList()));
        String generated = "@" + importContext.getSimpleName(Generated.class.getTypeName()) + "(" +
                "value=\"" + MainProcessor.class.getTypeName() + "\", " +
                "date=\"" + LocalDateTime.now().withNano(0) + "\", " +
                "comments=\"created by OperatorOverloading. This is SpringBoot's config.\")";
        String className = "public class " + CONFIG_CLASS_NAME;
        StringBuilder methods = new StringBuilder();
        for (ServiceHandle serviceHandle : serviceHandles) {
            methods.append(TAB).append(BEAN_ANNOTATION_NAME).append("\n");
            String serviceClassName = serviceHandle.getClassName();
            String methodName = Character.toLowerCase(serviceClassName.charAt(0)) + serviceClassName.substring(1);
            methods.append(TAB).append("public ").append(serviceClassName).append(" ").append(methodName).append("() {\n");
            methods.append(TAB).append(TAB).append("return new ").append(serviceClassName).append("();\n");
            methods.append(TAB).append("}\n\n");
        }

        String code = packCode + "\n" +
                importContext.write() + "\n" +
                CONFIG_ANNOTATION_NAME + "\n" +
                generated + "\n" +
                className + " {\n\n" +
                methods +
                "}";
        LogHandle.print(Diagnostic.Kind.NOTE, "write spring boot config: \n" + code);
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(qualifiedName);
        try (OutputStream outputStream = sourceFile.openOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(code);
        }
    }

}
