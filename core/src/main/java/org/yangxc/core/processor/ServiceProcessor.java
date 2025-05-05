package org.yangxc.core.processor;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.handle.service.FunctionHandle;
import org.yangxc.core.handle.overloading.OverloadingContext;
import org.yangxc.core.handle.service.ServiceHandle;
import org.yangxc.core.exception.ElementException;
import org.yangxc.core.handle.writer.ServiceWriter;

import javax.annotation.processing.*;
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

    private LogHandle logHandle;
    private String msgLevel;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logHandle = new LogHandle(processingEnv.getMessager(), processingEnv.getOptions().get("OperatorOverloadingLog"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<ServiceHandle> serviceHandles = getContexts(roundEnv);
        OverloadingContext overloadingContext = getOverloading(roundEnv);
        setup(serviceHandles, overloadingContext);
        serviceHandles.forEach(this::write);
        return !serviceHandles.isEmpty();
    }

    private List<ServiceHandle> getContexts(RoundEnvironment roundEnv) {
        List<ServiceHandle> handles = new ArrayList<>();
        for (Element rootElement : roundEnv.getRootElements()) {
            try {
                if (rootElement.getKind() != ElementKind.INTERFACE) {
                    continue;
                }
                ServiceHandle handle = new ServiceHandle((TypeElement) rootElement);
                List<FunctionHandle> functionHandles = rootElement.getEnclosedElements()
                        .stream()
                        .filter(element -> element.getKind() == ElementKind.METHOD)
                        .map(ExecutableElement.class::cast)
                        .filter(executableElement -> !executableElement.isDefault())
                        .filter(executableElement -> executableElement.getAnnotation(OperatorFunction.class) != null)
                        .map(FunctionHandle::new)
                        .toList();
                handle.setFunctionContexts(functionHandles);
                handles.add(handle);
                logHandle.postInit(handle);
            } catch (ElementException e) {
                processingEnv.getMessager().printError("init error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printError("init error: " + e.getMessage(), rootElement);
            }
        }
        logHandle.postAllInit(handles);
        return handles;
    }

    public OverloadingContext getOverloading(RoundEnvironment roundEnv) {
        // TODO
        OverloadingContext context = new OverloadingContext();

        logHandle.postAllOverloading(context);
        return context;
    }

    private void setup(List<ServiceHandle> handles, OverloadingContext overloadingContext) {
        for (ServiceHandle context : handles) {
            try {
                context.setup(overloadingContext);
            } catch (ElementException e) {
                processingEnv.getMessager().printError("setup error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printError("setup error: " + e.getMessage(), context.getTypeElement());
            }
        }
    }

    private void write(ServiceHandle context) {
        try {
            ServiceWriter serviceWriter = context.write1();
            logHandle.preWrite(context, serviceWriter);
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(context.getQualifiedName());
            try (OutputStream outputStream = sourceFile.openOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
                writer.write(serviceWriter.code());
            }
            logHandle.postWrite(context, serviceWriter);
        } catch (ElementException e) {
            processingEnv.getMessager().printError("write error: " + e.getMessage(), e.getElement());
        } catch (Exception e) {
            processingEnv.getMessager().printError("write error: " + e.getMessage(), context.getTypeElement());
        }
    }

}
