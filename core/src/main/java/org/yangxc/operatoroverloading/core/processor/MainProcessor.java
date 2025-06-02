package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.annotation.*;
import org.yangxc.operatoroverloading.core.handle.service.FunctionHandle;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.FieldHandle;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({MainProcessor.SERVICE_ANNOTATION, MainProcessor.OPERATOR_ANNOTATION})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({MainProcessor.OPERATOR_OVERLOADING_LOG})
public class MainProcessor extends AbstractProcessor {

    private LogHandle logHandle;

    public static final String SERVICE_ANNOTATION = "org.yangxc.operatoroverloading.core.annotation.OperatorService";
    public static final String OPERATOR_ANNOTATION = "org.yangxc.operatoroverloading.core.annotation.OperatorClass";

    public static final String OPERATOR_OVERLOADING_LOG = "OperatorOverloadingLog";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logHandle = new LogHandle(processingEnv.getMessager(), processingEnv.getOptions().get(OPERATOR_OVERLOADING_LOG));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<ServiceHandle> serviceHandles = getService(roundEnv);
        OverloadingContext overloadingContext = getOverloading(roundEnv);
        VariableSetContext variableContexts = getConst(roundEnv);
        setup(serviceHandles, overloadingContext, variableContexts);
        serviceHandles.forEach(this::write);
        return !serviceHandles.isEmpty();
    }

    private List<ServiceHandle> getService(RoundEnvironment roundEnv) {
        List<ServiceHandle> handles = new ArrayList<>();
        for (Element rootElement : roundEnv.getRootElements()) {
            try {
                if (rootElement.getKind() != ElementKind.INTERFACE) {
                    continue;
                }
                if (rootElement.getAnnotation(OperatorService.class) == null) {
                    continue;
                }
                ServiceHandle handle = new ServiceHandle((TypeElement) rootElement);
                // ServiceField
                List<FieldHandle> fieldHandles = rootElement.getEnclosedElements()
                        .stream()
                        .filter(element -> element.getKind() == ElementKind.METHOD)
                        .map(ExecutableElement.class::cast)
                        .filter(executableElement -> !executableElement.isDefault())
                        .filter(executableElement -> executableElement.getAnnotation(ServiceField.class) != null)
                        .filter(executableElement -> executableElement.getAnnotation(ServiceFunction.class) == null)
                        .map(element -> new FieldHandle(element, rootElement))
                        .collect(Collectors.toList());
                handle.setServiceFieldHandles(fieldHandles);
                // ServiceFunction
                List<FunctionHandle> functionHandles = rootElement.getEnclosedElements()
                        .stream()
                        .filter(element -> element.getKind() == ElementKind.METHOD)
                        .map(ExecutableElement.class::cast)
                        .filter(executableElement -> !executableElement.isDefault())
                        .filter(executableElement -> executableElement.getAnnotation(ServiceField.class) == null)
                        .filter(executableElement -> executableElement.getAnnotation(ServiceFunction.class) != null)
                        .map(FunctionHandle::new)
                        .collect(Collectors.toList());
                handle.setFunctionContexts(functionHandles);
                handles.add(handle);
            } catch (ElementException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "init service error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "init service error: " + e.getMessage(), rootElement);
            }
        }
        logHandle.postAllInit(handles);
        return handles;
    }

    public OverloadingContext getOverloading(RoundEnvironment roundEnv) {
        OverloadingContext context = new OverloadingContext();
        for (Element rootElement : roundEnv.getRootElements()) {
            try {
                if (rootElement.getAnnotation(OperatorClass.class) == null) {
                    continue;
                }
                if (rootElement instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) rootElement;
                    typeElement.getEnclosedElements()
                            .stream()
                            .filter(element -> element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.CONSTRUCTOR)
                            .filter(element -> element.getAnnotation(Operator.class) != null || element.getAnnotation(Cast.class) != null)
                            .map(ExecutableElement.class::cast)
                            .forEach(executableElement -> context.setup(executableElement, typeElement));
                }
            } catch (ElementException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "init overloading error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "init overloading error: " + e.getMessage(), rootElement);
            }
        }
        logHandle.postAllOverloading(context);
        return context;
    }

    private VariableSetContext getConst(RoundEnvironment roundEnv) {
        VariableSetContext context = new VariableSetContext();
        for (Element rootElement : roundEnv.getRootElements()) {
            try {
                if (rootElement.getAnnotation(OperatorClass.class) == null && rootElement.getAnnotation(OperatorService.class) == null) {
                    continue;
                }
                if (rootElement instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) rootElement;
                    typeElement.getEnclosedElements()
                            .stream()
                            .filter(element -> element.getKind() == ElementKind.FIELD)
                            .filter(element -> element.getAnnotation(OperatorClassConst.class) != null)
                            .map(VariableElement.class::cast)
                            .forEach(variableElement -> context.setup(variableElement, typeElement));
                }
            } catch (ElementException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "init overloading error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "init overloading error: " + e.getMessage(), rootElement);
            }
        }
        logHandle.postAllConst(context);
        return context;
    }

    private void setup(List<ServiceHandle> handles, OverloadingContext overloadingContext, VariableSetContext variableContexts) {
        for (ServiceHandle handle : handles) {
            try {
                handle.setup(overloadingContext, processingEnv.getElementUtils(), variableContexts.copy());
                logHandle.postSetup(handle);
            } catch (ElementException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "setup error: " + e.getMessage(), e.getElement());
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "setup error: " + e.getMessage(), handle.getTypeElement());
            }
        }
    }

    private void write(ServiceHandle context) {
        try {
            ServiceWriterContext serviceWriterContext = context.writerContext();
            logHandle.preWrite(context, serviceWriterContext);
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(context.getQualifiedName());
            try (OutputStream outputStream = sourceFile.openOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
                writer.write(serviceWriterContext.code());
            }
            logHandle.postWrite(context, serviceWriterContext);
        } catch (ElementException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "write error: " + e.getMessage(), e.getElement());
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "write error: " + e.getMessage(), context.getTypeElement());
        }
    }

}
