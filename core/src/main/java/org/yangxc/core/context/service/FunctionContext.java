package org.yangxc.core.context.service;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.context.overloading.OverloadingContext;
import org.yangxc.core.exception.ElementException;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yangxc.core.context.service.ServiceContext.TAB;

public class FunctionContext {

    private final ExecutableElement element;

    private ExecutableElement valueElement;
    private String value;
    private ExecutableElement statementsElement;

    private Ast ast;
    private OverloadingContext overloadingContext;
    private Map<String, VariableContext> variableContexts;
    private List<StatementContext> statementContexts;

    public FunctionContext(ExecutableElement element) {
        this.element = element;
        AnnotationMirror operatorFunctionMirror = element.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> OperatorFunction.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .orElseThrow(() -> new ElementException("@OperatorFunction not found", element));
        operatorFunctionMirror.getElementValues().forEach((executableElement, annotationValue) -> {
            String name = executableElement.getSimpleName().toString();
            if ("value".equals(name)) {
                valueElement = executableElement;
                value = annotationValue.accept(new DefaultAnnotationValueVisitor<>() {
                    @Override
                    public String visitString(String s, Object object) {
                        return s;
                    }
                }, null);
            } else if ("statements".equals(name)) {
                statementsElement = executableElement;
                statementContexts = annotationValue.accept(new DefaultAnnotationValueVisitor<>() {
                    @Override
                    public List<StatementContext> visitArray(List<? extends AnnotationValue> vals, Object o) {
                        List<StatementContext> list = new ArrayList<>();
                        for (int i = 0; i < vals.size(); i++) {
                            AnnotationValue values = vals.get(i);
                            int index = i;
                            StatementContext statementContext = values.accept(new DefaultAnnotationValueVisitor<>() {
                                @Override
                                public StatementContext visitAnnotation(AnnotationMirror a, Object object) {
                                    return new StatementContext(a, index);
                                }
                            }, null);
                            list.add(statementContext);
                        }
                        return list;
                    }
                }, null);
            }
        });
        if (value == null) {
            throw new ElementException("OperatorFunction#value not found", element);
        }
        statementContexts = statementContexts != null ? statementContexts : List.of();
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext) {
        this.overloadingContext = overloadingContext;
        variableContexts = element.getParameters().stream()
                .map(element -> new VariableContext(element.getSimpleName().toString(), element.asType().toString(), 0))
                .collect(Collectors.toMap(VariableContext::name, Function.identity(), (c1, c2) -> c1));
        try {
            ast = astParse.parse(value);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, valueElement);
        }
        try {
            for (StatementContext statementContext : statementContexts) {
                statementContext.setup(astParse, overloadingContext, variableContexts);
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, statementsElement);
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
                statementContexts.stream()
                        .map(StatementContext::getType)
                        .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                        .map(TypeMirror::toString),
                Stream.of(ClassName.BIG_DECIMAL)
        ).flatMap(Function.identity()).collect(Collectors.toSet());
    }

    public String toMethod() {
        try {
            String parameters = element.getParameters()
                    .stream()
                    .map(e -> e.asType() + " " + e.getSimpleName())
                    .collect(Collectors.joining(", "));
            StringBuilder body = new StringBuilder();
            for (StatementContext statementContext : statementContexts) {
                body.append(TAB).append(TAB).append(statementContext.write());
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
        ExpVisitor.ExpContext expContext = new ExpVisitor.ExpContext(overloadingContext, variableContexts);
        ExpVisitor.ExpResult result = ast.accept(ExpVisitor.INSTANCE, expContext);
        if (Objects.equals(result.getType(), resType.toString())) {
            return expContext.append(";\n").toString();
        }
        if (resType.getKind().isPrimitive()) {
            switch (resType.getKind()) {
                case BYTE -> expContext.append(".").append(result.getOverloadingContext().getToByte().name()).append("()");
                case SHORT -> expContext.append(".").append(result.getOverloadingContext().getToShort().name()).append("()");
                case INT -> expContext.append(".").append(result.getOverloadingContext().getToInt().name()).append("()");
                case LONG -> expContext.append(".").append(result.getOverloadingContext().getToLong().name()).append("()");
                case FLOAT -> expContext.append(".").append(result.getOverloadingContext().getToFloat().name()).append("()");
                case DOUBLE -> expContext.append(".").append(result.getOverloadingContext().getToDouble().name()).append("()");
                default -> throw new ElementException("未知转换: " + resType, element);
            }
            return expContext.append(";\n").toString();
        }
        throw new ElementException("未知转换: " + resType, element);
    }

}
