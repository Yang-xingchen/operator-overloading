package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.annotation.spring.Scope;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;
import org.yangxc.operatoroverloading.core.util.GetAnnotationValueVisitor;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext.TAB;

public class SpringBootHandle implements ProcessorHandle {

    public static final String SPRING_BOOT_TYPE = "OperatorOverloadingSpringBootType";
    public static final String SPRING_BOOT_CONFIG_NAME = "OperatorOverloadingSpringBootConfigName";
    private static final String AUTO = "auto";
    private static final String NONE = "none";
    private static final String CONFIG = "config";

    private static final String DEFAULT_CONFIG_CLASS_NAME = "OperatorOverloadingConfiguration";
    private static final String MAIN_ANNOTATION = "org.springframework.boot.autoconfigure.SpringBootApplication";
    private static final String CONFIG_ANNOTATION_QUALIFIED_NAME = "org.springframework.boot.SpringBootConfiguration";
    private static final String CONFIG_ANNOTATION_NAME = "@SpringBootConfiguration";
    private static final String BEAN_ANNOTATION_QUALIFIED_NAME = "org.springframework.context.annotation.Bean";
    private static final String BEAN_ANNOTATION_NAME = "@Bean";
    private static final String SCOPE_ANNOTATION_QUALIFIED_NAME = "org.springframework.context.annotation.Scope";
    private static final String SCOPE_ANNOTATION_NAME = "@Scope";

    private final ProcessingEnvironment processingEnv;
    private final String handle;
    private TypeElement mainClass;
    private String pack;
    private final String configName;

    static Stream<String> helps() {
        return Stream.of(
                "使用 -A" + SPRING_BOOT_TYPE + "=auto 自动判断是否包含SpringBoot(通过启动类判断)",
                "使用 -A" + SPRING_BOOT_TYPE + "=none 不使用spring自动注入",
                "使用 -A" + SPRING_BOOT_TYPE + "=config 使用@Configuration注解",
                "使用 -A" + SPRING_BOOT_CONFIG_NAME + "=xxx.Xxx 配置@Configuration注解类的全限定名"
        );
    }

    public SpringBootHandle(ProcessingEnvironment processingEnv, String type, String name) {
        this.processingEnv = processingEnv;
        type = type != null ? type : AUTO;
        if (Stream.of(AUTO, NONE, CONFIG).noneMatch(type::equals)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "unknown " + SPRING_BOOT_TYPE + ": " + type);
            type = name != null ? CONFIG : AUTO;
        }
        this.handle = type;
        if (name != null) {
            int i = name.lastIndexOf('.');
            this.pack = i != -1 ? name.substring(0, i) : "";
            this.configName = i != -1 ? name.substring(i + 1) : name;
        } else {
            this.configName = DEFAULT_CONFIG_CLASS_NAME;
        }
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
        if (mainClass != null && pack == null) {
            String mainClassName = mainClass.getQualifiedName().toString();
            int index = mainClassName.lastIndexOf('.');
            pack = index != -1 ? mainClassName.substring(0, index) : "";
        }
        if (mainClass == null) {
            if (CONFIG.equals(handle)) {
                LogHandle.info(Diagnostic.Kind.WARNING, "not find spring boot main class");
            }
        } else {
            LogHandle.debug(Diagnostic.Kind.NOTE, "find spring boot main class", mainClass);
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
        if (serviceHandles.isEmpty()) {
            return;
        }
        try {
            write(serviceHandles);
        } catch (ElementException e) {
            LogHandle.debug(Diagnostic.Kind.ERROR, "write error: " + e.getMessage(), e.getElement());
        } catch (Exception e) {
            LogHandle.debug(Diagnostic.Kind.ERROR, "write error: " + e.getMessage(), mainClass);
        }
    }

    private void write(List<ServiceHandle> serviceHandles) throws IOException {
        String qualifiedName = "".equals(pack) ? configName : (pack + "." + configName);
        String code = getCode(serviceHandles);

        if (LogHandle.DEBUG.equals(LogHandle.getGobleLevel())) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "write spring boot config: \n" + code);
        }
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(qualifiedName);
        try (OutputStream outputStream = sourceFile.openOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(code);
        }
        if (LogHandle.INFO.equals(LogHandle.getGobleLevel())) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "write spring boot config complete: " + qualifiedName);
        }
    }

    private String getCode(List<ServiceHandle> serviceHandles) {
        String packCode = "".equals(pack) ? "" : ("package " + pack + ";\n");
        Map<ServiceHandle, String> scopeMap = serviceHandles.stream().collect(Collectors.toMap(Function.identity(), this::getScope));
        ImportContext importContext = new ImportContext(Stream.concat(
                serviceHandles.stream().map(ServiceHandle::getQualifiedName),
                Stream.of(
                        BEAN_ANNOTATION_QUALIFIED_NAME,
                        CONFIG_ANNOTATION_QUALIFIED_NAME,
                        scopeMap.values().stream().anyMatch(s -> !"".equals(s)) ? SCOPE_ANNOTATION_QUALIFIED_NAME : null,
                        Generated.class.getTypeName()
                ).filter(Objects::nonNull)
        ).collect(Collectors.toList()));
        String generated = "@" + importContext.getSimpleName(Generated.class.getTypeName()) + "(" +
                "value=\"" + MainProcessor.class.getTypeName() + "\", " +
                "date=\"" + LocalDateTime.now().withNano(0) + "\", " +
                "comments=\"created by OperatorOverloading. This is SpringBoot's config.\")";
        String className = "public class " + configName;
        StringBuilder methods = new StringBuilder();
        for (ServiceHandle serviceHandle : serviceHandles) {
            methods.append(TAB).append(BEAN_ANNOTATION_NAME).append("\n");
            String scope = scopeMap.get(serviceHandle);
            if (!"".equals(scope)) {
                methods.append(TAB).append(SCOPE_ANNOTATION_NAME).append("(\"").append(scope).append("\")\n");
            }
            String interfaceName = serviceHandle.getInterfaceName();
            String methodName = Character.toLowerCase(interfaceName.charAt(0)) + interfaceName.substring(1);
            methods.append(TAB).append("public ").append(interfaceName).append(" ").append(methodName).append("() {\n");
            methods.append(TAB).append(TAB).append("return new ").append(serviceHandle.getImplClassName()).append("();\n");
            methods.append(TAB).append("}\n\n");
        }
        return packCode + "\n" +
                importContext.write() + "\n" +
                CONFIG_ANNOTATION_NAME + "\n" +
                generated + "\n" +
                className + " {\n\n" +
                methods +
                "}";
    }

    private String getScope(ServiceHandle serviceHandle) {
        return serviceHandle.getTypeElement().getAnnotationMirrors()
                .stream()
                .filter(mirror -> Scope.class.getTypeName().equals(mirror.getAnnotationType().toString()))
                .map(annotationMirror -> annotationMirror.getElementValues()
                        .keySet()
                        .stream()
                        .filter(e -> "value".equals(e.getSimpleName().toString()))
                        .findAny()
                        .map(annotationMirror.getElementValues()::get)
                        .map(annotationValue -> annotationValue.accept(GetAnnotationValueVisitor.visitString(), null))
                        .orElse(null))
                .filter(Objects::nonNull)
                .findAny()
                .orElse("");
    }

}
