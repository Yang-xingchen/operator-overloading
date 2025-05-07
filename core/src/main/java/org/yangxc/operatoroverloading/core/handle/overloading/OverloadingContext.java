package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.annotation.Cast;
import org.yangxc.operatoroverloading.core.annotation.Operator;
import org.yangxc.operatoroverloading.core.annotation.OperatorType;
import org.yangxc.operatoroverloading.core.constant.CastMethodType;
import org.yangxc.operatoroverloading.core.constant.ClassOverloading;
import org.yangxc.operatoroverloading.core.constant.OperatorMethodType;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.service.BaseAnnotationValueVisitor;

import javax.lang.model.element.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
            String paramType = parameters != null && !parameters.isEmpty() ? parameters.getFirst().asType().toString() : null;
            String resultType = element.getReturnType().toString();
            AtomicBoolean addOperator = new AtomicBoolean(false);
            if (element.getKind() == ElementKind.METHOD) {
                element.getAnnotationMirrors()
                        .stream()
                        .filter(annotationMirror -> Operator.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                        .findAny()
                        .ifPresent(annotation -> {
                            annotation.getElementValues().forEach((executableElement, annotationValue) -> {
                                try {
                                    String name = executableElement.getSimpleName().toString();
                                    if ("value".equals(name)) {
                                        OperatorType operatorType = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                                            @Override
                                            public OperatorType visitEnumConstant(VariableElement c, Object object) {
                                                return OperatorType.valueOf(c.getSimpleName().toString());
                                            }
                                        }, null);
                                        if (isStatic) {
                                            put(new OperatorOverloadingContext(operatorType, OperatorMethodType.STATIC_METHOD, typeName + "." + functionName, paramType, resultType));
                                        } else {
                                            put(new OperatorOverloadingContext(operatorType, OperatorMethodType.METHOD, functionName, paramType, resultType));
                                        }
                                        addOperator.set(true);
                                    }
                                } catch (ElementException e) {
                                    throw e;
                                } catch (Throwable e) {
                                    throw new ElementException(e, executableElement);
                                }
                            });
                        });
            }
            AtomicBoolean addCast = new AtomicBoolean(false);
            element.getAnnotationMirrors()
                    .stream()
                    .filter(annotationMirror -> Cast.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                    .findAny()
                    .ifPresent(annotation -> {
                        if (element.getKind() == ElementKind.CONSTRUCTOR) {
                            put(new CastContext(paramType, CastMethodType.NEW, "", typeName));
                        } else if (isStatic) {
                            put(new CastContext(paramType, CastMethodType.STATIC_METHOD, typeName + "." + functionName, resultType));
                        } else {
                            put(new CastContext(typeName, CastMethodType.METHOD, functionName, resultType));
                        }
                        addCast.set(true);
                    });
            if (!addOperator.get()) {
                addCast.get();
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    public ClassOverloadingContext get(String type) {
        return map.get(type);
    }

    public void put(OperatorOverloadingContext context) {
        map.computeIfAbsent(context.paramType(), ClassOverloadingContext::new).set(context);
    }

    public void put(CastContext context) {
        map.computeIfAbsent(context.from(), ClassOverloadingContext::new).set(context);
    }

    public Set<String> typeSet() {
        return map.keySet();
    }

}
