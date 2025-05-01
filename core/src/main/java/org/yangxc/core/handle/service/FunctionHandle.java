package org.yangxc.core.handle.service;

import org.yangxc.core.annotation.NumberType;
import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.handle.overloading.CastContext;
import org.yangxc.core.handle.overloading.OverloadingContext;
import org.yangxc.core.exception.ElementException;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yangxc.core.handle.service.ServiceHandle.TAB;

public class FunctionHandle {

    private final ExecutableElement element;

    private ExecutableElement valueElement;
    private String value;
    private ExecutableElement statementsElement;
    private List<StatementHandle> statementHandles;
    private ExecutableElement numberTypeElement;
    private NumberType numberType;

    private Ast ast;
    private OverloadingContext overloadingContext;
    private SymbolContext symbolContext;

    public FunctionHandle(ExecutableElement element) {
        this.element = element;
        element.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> OperatorFunction.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .orElseThrow(() -> new ElementException("@OperatorFunction not found", element))
                .getElementValues()
                .forEach((executableElement, annotationValue) -> {
                    String name = executableElement.getSimpleName().toString();
                    if ("value".equals(name)) {
                        valueElement = executableElement;
                        value = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                            @Override
                            public String visitString(String s, Object object) {
                                return s;
                            }
                        }, null);
                    } else if ("statements".equals(name)) {
                        statementsElement = executableElement;
                        statementHandles = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                            @Override
                            public List<StatementHandle> visitArray(List<? extends AnnotationValue> vals, Object o) {
                                List<StatementHandle> list = new ArrayList<>();
                                for (int i = 0; i < vals.size(); i++) {
                                    AnnotationValue values = vals.get(i);
                                    int index = i;
                                    StatementHandle statementHandle = values.accept(new BaseAnnotationValueVisitor<>() {
                                        @Override
                                        public StatementHandle visitAnnotation(AnnotationMirror a, Object object) {
                                            return new StatementHandle(a, index);
                                        }
                                    }, null);
                                    list.add(statementHandle);
                                }
                                return list;
                            }
                        }, null);
                    } else if ("numberType".equals(name)) {
                        numberTypeElement = executableElement;
                        numberType = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                            @Override
                            public NumberType visitEnumConstant(VariableElement c, Object object) {
                                return NumberType.valueOf(c.getSimpleName().toString());
                            }
                        }, null);
                    }
                });
        if (value == null) {
            throw new ElementException("OperatorFunction#value not found", element);
        }
        numberType = numberType != null ? numberType : NumberType.INHERIT;
        statementHandles = statementHandles != null ? statementHandles : List.of();
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext, NumberType numberType, List<String> imports) {
        this.numberType = this.numberType != NumberType.INHERIT ? this.numberType : numberType;
        this.overloadingContext = overloadingContext;
        List<VariableContext> variableContexts = element.getParameters().stream()
                .map(element -> new VariableContext(element.getSimpleName().toString(), element.asType().toString(), -1))
                .toList();
        symbolContext = new SymbolContext(variableContexts, imports);
        try {
            for (StatementHandle statementHandle : statementHandles) {
                statementHandle.setup(astParse, overloadingContext, symbolContext, this.numberType);
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, statementsElement);
        }
        try {
            ast = astParse.parse(value, symbolContext);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, valueElement);
        }
    }

    public Set<String> getUseClasses() {
        return Stream.of(
                Stream.of(element.getReturnType())
                        .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                        .map(TypeMirror::toString),
                element.getParameters().stream()
                        .map(VariableElement::asType)
                        .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                        .map(TypeMirror::toString),
                statementHandles.stream()
                        .map(StatementHandle::getType)
                        .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                        .map(TypeMirror::toString),
                numberType == NumberType.BIG_DECIMAL ? Stream.of(ClassName.BIG_DECIMAL) : Stream.<String>empty(),
                numberType == NumberType.BIG_INTEGER ? Stream.of(ClassName.BIG_INTEGER) : Stream.<String>empty()
        ).flatMap(Function.identity()).collect(Collectors.toSet());
    }

    public String toMethod() {
        try {
            String parameters = element.getParameters()
                    .stream()
                    .map(e -> e.asType() + " " + e.getSimpleName())
                    .collect(Collectors.joining(", "));
            StringBuilder body = new StringBuilder();
            for (StatementHandle statementHandle : statementHandles) {
                body.append(TAB).append(TAB).append(statementHandle.write());
            }
            TypeMirror returnType = element.getReturnType();
            if (returnType.getKind() != TypeKind.VOID) {
                body.append(TAB).append(TAB).append("return ").append(getExpString(ast, returnType));
            }
            return TAB + "public " + returnType + " " + element.getSimpleName() + "(" + parameters + ") {\n" +
                    body +
                    TAB + "}\n";
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    private String getExpString(Ast ast, TypeMirror resType) {
        ExpVisitor.ExpContext expContext = new ExpVisitor.ExpContext(overloadingContext, symbolContext, numberType);
        ExpVisitor.ExpResult result = ast.accept(ExpVisitor.INSTANCE, expContext);
        String type = resType.toString();
        if (Objects.equals(result.getType(), type)) {
            return expContext.append(";\n").toString();
        }
        CastContext cast = result.cast(type);
        String simpleType = ClassName.getSimpleName(type);
        return switch (cast.type()) {
            case CAST -> "(" + simpleType + ")" + expContext + ";\n";
            case NEW -> "new " + simpleType + "(" + expContext + ");\n";
            case METHOD -> expContext.append(".").append(cast.name()).append("();\n").toString();
            case STATIC_METHOD -> cast.name() + "(" + expContext + ");\n";
        };
    }

}
