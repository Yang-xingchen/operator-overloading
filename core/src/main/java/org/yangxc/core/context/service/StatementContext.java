package org.yangxc.core.context.service;

import org.yangxc.core.ast.AstParse;
import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.context.overloading.OverloadingContext;
import org.yangxc.core.exception.ElementException;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

public class StatementContext {

    private final int index;

    private ExecutableElement typeElement;
    private TypeMirror type;
    private ExecutableElement varNameElement;
    private String varName;
    private ExecutableElement expElement;
    private String exp;

    private Ast ast;
    private OverloadingContext overloadingContext;
    private Map<String, VariableContext> variableContexts;

    public StatementContext(AnnotationMirror annotationMirror, int index) {
        this.index = index;
        annotationMirror.getElementValues().forEach((executableElement, annotationValue) -> {
            String name = executableElement.getSimpleName().toString();
            if ("type".equals(name)) {
                typeElement = executableElement;
                type = annotationValue.accept(new DefaultAnnotationValueVisitor<>() {
                    @Override
                    public TypeMirror visitType(TypeMirror t, Object o) {
                        return t;
                    }
                }, null);
            } else if ("varName".equals(name)) {
                varNameElement = executableElement;
                varName = annotationValue.accept(new DefaultAnnotationValueVisitor<>() {
                    @Override
                    public String visitString(String s, Object o) {
                        return s;
                    }
                }, null);
            } else if ("exp".equals(name)) {
                expElement = executableElement;
                exp = annotationValue.accept(new DefaultAnnotationValueVisitor<>() {
                    @Override
                    public String visitString(String s, Object o) {
                        return s;
                    }
                }, null);
            }
        });
    }

    public TypeMirror getType() {
        return type;
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext, Map<String, VariableContext> variableContexts) {
        this.overloadingContext = overloadingContext;
        this.variableContexts = variableContexts;
        try {
            ast = astParse.parse(exp);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, expElement);
        }
        variableContexts.put(varName, new VariableContext(varName, type.toString(), index));
    }

    public String write() {
        String type = this.type.toString();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (!this.type.getKind().isPrimitive()) {
                int i = type.lastIndexOf(".");
                stringBuilder.append(i == -1 ? type : type.substring(i + 1));
            } else {
                stringBuilder.append(type);
            }
            stringBuilder
                    .append(" ")
                    .append(varName)
                    .append(" = ");
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, varNameElement);
        }
        ExpVisitor.ExpContext expContext;
        ExpVisitor.ExpResult result;
        try {
            expContext = new ExpVisitor.ExpContext(stringBuilder, overloadingContext, variableContexts);
            result = ast.accept(ExpVisitor.INSTANCE, expContext);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, expElement);
        }
        try {
            if (type.equals(result.getType())) {
                return expContext.append(";\n").toString();
            }
            if (this.type.getKind().isPrimitive()) {
                switch (this.type.getKind()) {
                    case BYTE -> expContext.append(".").append(result.getOverloadingContext().getToByte().name()).append("()");
                    case SHORT -> expContext.append(".").append(result.getOverloadingContext().getToShort().name()).append("()");
                    case INT -> expContext.append(".").append(result.getOverloadingContext().getToInt().name()).append("()");
                    case LONG -> expContext.append(".").append(result.getOverloadingContext().getToLong().name()).append("()");
                    case FLOAT -> expContext.append(".").append(result.getOverloadingContext().getToFloat().name()).append("()");
                    case DOUBLE -> expContext.append(".").append(result.getOverloadingContext().getToDouble().name()).append("()");
                    default -> throw new ElementException("未知转换: " + this.type.toString(), typeElement);
                }
                return expContext.append(";\n").toString();
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
        throw new ElementException("未知转换: " + this.type.toString(), typeElement);
    }


}
