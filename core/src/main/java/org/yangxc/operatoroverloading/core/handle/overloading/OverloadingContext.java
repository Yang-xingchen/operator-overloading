package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.annotation.Cast;
import org.yangxc.operatoroverloading.core.annotation.Operator;
import org.yangxc.operatoroverloading.core.annotation.OperatorType;
import org.yangxc.operatoroverloading.core.constant.CastMethodType;
import org.yangxc.operatoroverloading.core.constant.ClassOverloading;
import org.yangxc.operatoroverloading.core.constant.OperatorMethodType;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.util.GetAnnotationValueVisitor;

import javax.lang.model.element.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OverloadingContext {

    private final Map<String, ClassOverloadingContext> map;

    public OverloadingContext() {
        map = new HashMap<>(ClassOverloading.DEFAULT_CLASS_OVERLOADING);
    }

    public void setup(ExecutableElement element, TypeElement typeElement) {
        try {
            String typeName = typeElement.getQualifiedName().toString();
            boolean isStatic = element.getModifiers().contains(Modifier.STATIC);
            String functionName = element.getSimpleName().toString();
            List<? extends VariableElement> parameters = element.getParameters();
            String paramType = parameters != null && !parameters.isEmpty() ? parameters.get(0).asType().toString() : null;
            String resultType = element.getReturnType().toString();

            setupOperator(element, isStatic, typeName, functionName, paramType, resultType);
            setupCast(element, paramType, typeName, isStatic, functionName, resultType);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    /**
     * setup {@link Operator}
     */
    private void setupOperator(ExecutableElement element, boolean isStatic, String typeName, String functionName, String paramType, String resultType) {
        if (element.getKind() != ElementKind.METHOD) {
            return;
        }
        element.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> Operator.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .ifPresent(annotation -> {
                    annotation.getElementValues().forEach((executableElement, annotationValue) -> {
                        try {
                            String name = executableElement.getSimpleName().toString();
                            if ("value".equals(name)) {
                                OperatorType operatorType = annotationValue.accept(GetAnnotationValueVisitor.visitEnum(OperatorType.class), null);
                                if (isStatic) {
                                    put(new OperatorOverloadingContext(operatorType, OperatorMethodType.STATIC_METHOD, typeName, functionName, paramType, resultType));
                                } else {
                                    put(new OperatorOverloadingContext(operatorType, OperatorMethodType.METHOD, null, functionName, paramType, resultType));
                                }
                            }
                        } catch (ElementException e) {
                            throw e;
                        } catch (Throwable e) {
                            throw new ElementException(e, executableElement);
                        }
                    });
                });
    }

    /**
     * setup {@link Cast}
     */
    private void setupCast(ExecutableElement element, String paramType, String typeName, boolean isStatic, String functionName, String resultType) {
        element.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> Cast.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .ifPresent(annotation -> {
                    if (element.getKind() == ElementKind.CONSTRUCTOR) {
                        put(new CastContext(paramType, CastMethodType.NEW, null, "", typeName));
                    } else if (isStatic) {
                        put(new CastContext(paramType, CastMethodType.STATIC_METHOD, typeName, functionName, resultType));
                    } else {
                        put(new CastContext(typeName, CastMethodType.METHOD, null, functionName, resultType));
                    }
                });
    }

    private void put(OperatorOverloadingContext context) {
        map.computeIfAbsent(context.getParamType(), ClassOverloadingContext::new).set(context);
    }

    private void put(CastContext context) {
        map.computeIfAbsent(context.getFrom(), ClassOverloadingContext::new).set(context);
    }

    public ClassOverloadingContext get(String type) {
        return map.get(type);
    }

    public Set<String> typeSet() {
        return map.keySet();
    }

}
