package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.ServiceField;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.writer.FunctionWriterContext;
import org.yangxc.operatoroverloading.core.handle.writer.Param;
import org.yangxc.operatoroverloading.core.util.GetAnnotationValueVisitor;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldHandle {

    private final ExecutableElement element;
    private final boolean isDefault;

    private ExecutableElement namesElement;
    private List<String> names;

    private List<VariableContext> variableContext;
    private Map<String, VariableElement> variableElementMap = new HashMap<>();
    private List<String> docLines;

    public FieldHandle(ExecutableElement element, Element rootElement) {
        this.element = element;
        this.isDefault = element.isDefault();
        if (element.getReturnType().getKind() != TypeKind.VOID && !Objects.equals(element.getReturnType(), rootElement.asType())) {
            throw new ElementException("return type must void or " + rootElement.asType(), element);
        }
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters.isEmpty()) {
            throw new ElementException("param size is empty", element);
        }
        element.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> ServiceField.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .orElseThrow(() -> new ElementException("@ServiceField not found", element))
                .getElementValues()
                .forEach((executableElement, annotationValue) -> {
                    try {
                        String name = executableElement.getSimpleName().toString();
                        if ("value".equals(name)) {
                            namesElement = executableElement;
                            names = annotationValue.accept(GetAnnotationValueVisitor.visitArrayEach(GetAnnotationValueVisitor.visitString()), null);
                        }
                    } catch (ElementException e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new ElementException(e, executableElement);
                    }
                });
        if (names == null || names.isEmpty()) {
            variableContext = new ArrayList<>(element.getParameters().size());
            for (VariableElement parameter : element.getParameters()) {
                VariableContext var = VariableContext.createByThis(parameter.asType().toString(), parameter.getSimpleName().toString());
                variableContext.add(var);
                variableElementMap.put(var.getQualifiedName(), parameter);
            }
        } else if (names.size() == parameters.size()) {
            variableContext = new ArrayList<>(element.getParameters().size());
            List<? extends VariableElement> elementParameters = element.getParameters();
            for (int i = 0; i < elementParameters.size(); i++) {
                VariableElement parameter = elementParameters.get(i);
                VariableContext var = VariableContext.createByThis(parameter.asType().toString(), names.get(i));
                variableContext.add(var);
                variableElementMap.put(var.getQualifiedName(), parameter);
            }
        } else {
            throw new ElementException("param size is not match @ServiceField#value", element);
        }
    }

    public void setup(VariableSetContext variableSetContext, Elements elementUtils) {
        setupDoc(elementUtils);
        setupVar(variableSetContext);
    }

    private void setupDoc(Elements elementUtils) {
        try {
            String docComment = elementUtils.getDocComment(element);
            docLines = docComment != null ? Arrays.stream(docComment.split("\n")).collect(Collectors.toList()) : null;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    private void setupVar(VariableSetContext variableSetContext) {
        try {
            for (VariableContext variableContext : variableContext) {
                if (variableSetContext.contains(variableContext.getQualifiedName())) {
                    throw new ElementException("service field define repeat: " + variableContext.getName(), variableElementMap.get(variableContext.getQualifiedName()));
                }
                variableSetContext.add(variableContext);
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    public Stream<Param> writeParams() {
        return variableContext.stream()
                .map(context -> new Param(context.getType(), context.getName()));
    }

    public FunctionWriterContext writeFunction(ImportContext importContext) {
        if (isDefault) {
            return null;
        }
        try {
            FunctionWriterContext write = new FunctionWriterContext();
            write.setDocLines(docLines);
            String returnType = element.getReturnType().toString();
            write.setReturnType(importContext.getSimpleName(returnType));
            write.setName(element.getSimpleName().toString());
            List<Param> params = variableElementMap.values().stream().map(variableElement -> {
                try {
                    String type = variableElement.asType().toString();
                    return new Param(importContext.getSimpleName(type), variableElement.getSimpleName().toString());
                } catch (ElementException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new ElementException(e, variableElement);
                }
            }).collect(Collectors.toList());
            write.setParams(params);
            write.setThrowList(element.getThrownTypes().stream()
                    .map(TypeMirror::toString)
                    .map(importContext::getSimpleName)
                    .collect(Collectors.toList()));
            List<String> lines = new ArrayList<>();
            for (VariableContext context : variableContext) {
                VariableElement variableElement = variableElementMap.get(context.getQualifiedName());
                try {
                    lines.add("this." + context.getName() + " = " + variableElement.getSimpleName() + ";");
                } catch (ElementException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new ElementException(e, variableElement);
                }
            }
            if (element.getReturnType().getKind() != TypeKind.VOID) {
                lines.add("return this;");
            }
            write.setBodyLines(lines);
            return write;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

}
