package org.yangxc.core.handle.service;

import org.yangxc.core.annotation.NumberType;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.handle.overloading.CastContext;
import org.yangxc.core.handle.overloading.OverloadingContext;
import org.yangxc.core.exception.ElementException;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

public class StatementHandle {

    private final int index;

    private ExecutableElement typeElement;
    private TypeMirror type;
    private ExecutableElement varNameElement;
    private String varName;
    private ExecutableElement expElement;
    private String exp;
    private ExecutableElement numberTypeElement;
    private NumberType numberType;

    private Ast ast;
    private OverloadingContext overloadingContext;
    private Map<String, VariableContext> variableContexts;

    public StatementHandle(AnnotationMirror annotationMirror, int index) {
        this.index = index;
        annotationMirror.getElementValues().forEach((executableElement, annotationValue) -> {
            String name = executableElement.getSimpleName().toString();
            if ("type".equals(name)) {
                typeElement = executableElement;
                type = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                    @Override
                    public TypeMirror visitType(TypeMirror t, Object o) {
                        return t;
                    }
                }, null);
            } else if ("varName".equals(name)) {
                varNameElement = executableElement;
                varName = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                    @Override
                    public String visitString(String s, Object o) {
                        return s;
                    }
                }, null);
            } else if ("exp".equals(name)) {
                expElement = executableElement;
                exp = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                    @Override
                    public String visitString(String s, Object o) {
                        return s;
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
        numberType = numberType != null ? numberType : NumberType.INHERIT;
    }

    public TypeMirror getType() {
        return type;
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext, Map<String, VariableContext> variableContexts, NumberType numberType) {
        this.numberType = this.numberType != NumberType.INHERIT ? this.numberType : numberType;
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
        String type;
        String simpleType;
        try {
            type = this.type.toString();
            simpleType = ClassName.getSimpleName(type);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder
                    .append(simpleType)
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
            expContext = new ExpVisitor.ExpContext(stringBuilder, overloadingContext, variableContexts, numberType);
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
            CastContext cast = result.cast(type);
            return switch (cast.type()) {
                case CAST -> "(" + simpleType + ")" + expContext + ";\n";
                case NEW -> "new " + simpleType + "(" + expContext + ");\n";
                case METHOD -> expContext.append(".").append(cast.name()).append("()'\n").toString();
                case STATIC_METHOD -> cast.name() + "(" + expContext + ");\n";
            };
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }


}
