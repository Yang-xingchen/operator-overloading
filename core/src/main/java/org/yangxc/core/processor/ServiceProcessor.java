package org.yangxc.core.processor;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.handle.service.FunctionHandle;
import org.yangxc.core.handle.overloading.OverloadingContext;
import org.yangxc.core.handle.service.ServiceHandle;
import org.yangxc.core.exception.ElementException;

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
        List<ServiceHandle> contexts = getContexts(roundEnv);
        // TODO
        OverloadingContext overloadingContext = new OverloadingContext();
        setup(contexts, overloadingContext);
        contexts.forEach(this::write);
        return !contexts.isEmpty();
    }

    private void setup(List<ServiceHandle> contexts, OverloadingContext overloadingContext) {
        for (ServiceHandle context : contexts) {
            try {
                context.setup(overloadingContext);
            } catch (ElementException e) {
                processingEnv.getMessager().printError("setup error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printError("setup error: " + e.getMessage(), context.getTypeElement());
            }
        }
    }

    private List<ServiceHandle> getContexts(RoundEnvironment roundEnv) {
        List<ServiceHandle> contexts = new ArrayList<>();
        for (Element rootElement : roundEnv.getRootElements()) {
            try {
                if (rootElement.getKind() != ElementKind.INTERFACE) {
                    continue;
                }
                ServiceHandle context = new ServiceHandle((TypeElement) rootElement);
                List<FunctionHandle> functionHandles = rootElement.getEnclosedElements()
                        .stream()
                        .filter(element -> element.getKind() == ElementKind.METHOD)
                        .map(ExecutableElement.class::cast)
                        .filter(executableElement -> !executableElement.isDefault())
                        .filter(executableElement -> executableElement.getAnnotation(OperatorFunction.class) != null)
                        .map(FunctionHandle::new)
                        .toList();
                context.setFunctionContexts(functionHandles);
                contexts.add(context);
            } catch (ElementException e) {
                processingEnv.getMessager().printError("init error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printError("init error: " + e.getMessage(), rootElement);
            }
        }
        return contexts;
    }

    private void write(ServiceHandle context) {
        try {
            processingEnv.getMessager().printNote(context.write(), context.getTypeElement());
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(context.getQualifiedName());
            try (OutputStream outputStream = sourceFile.openOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
                writer.write(context.write());
            }
        } catch (ElementException e) {
            processingEnv.getMessager().printError("write error: " + e.getMessage(), e.getElement());
        } catch (Exception e) {
            processingEnv.getMessager().printError("write error: " + e.getMessage(), context.getTypeElement());
        }
    }

}
