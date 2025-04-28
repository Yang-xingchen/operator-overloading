package org.yangxc.core.processor;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.context.FunctionContext;
import org.yangxc.core.context.OverloadingContext;
import org.yangxc.core.context.ServiceContext;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("org.yangxc.core.annotation.OperatorService")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ServiceProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<ServiceContext> contexts = getContexts(roundEnv);
        // TODO
        OverloadingContext overloadingContext = new OverloadingContext();
        contexts.forEach(context -> context.setup(overloadingContext));
        contexts.forEach(this::write);
        return !contexts.isEmpty();
    }

    private List<ServiceContext> getContexts(RoundEnvironment roundEnv) {
        List<ServiceContext> contexts = new ArrayList<>();
        for (Element rootElement : roundEnv.getRootElements()) {
            if (rootElement.getKind() != ElementKind.INTERFACE) {
                continue;
            }
            ServiceContext context = new ServiceContext((TypeElement) rootElement);
            List<FunctionContext> functionContexts = rootElement.getEnclosedElements()
                    .stream()
                    .filter(element -> element.getKind() == ElementKind.METHOD)
                    .map(ExecutableElement.class::cast)
                    .filter(executableElement -> !executableElement.isDefault())
                    .filter(executableElement -> executableElement.getAnnotation(OperatorFunction.class) != null)
                    .map(FunctionContext::new)
                    .toList();
            context.setFunctionContexts(functionContexts);
            contexts.add(context);
        }
        return contexts;
    }

    private void write(ServiceContext context) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(context.getQualifiedName());
            try (OutputStream outputStream = sourceFile.openOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
                writer.write(context.write());
            }
            processingEnv.getMessager().printNote(context.write(), context.getTypeElement());
        } catch (Exception e) {
            processingEnv.getMessager().printError("失败: " + e.getMessage(), context.getTypeElement());
        }
    }

}
