package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.ast.AstParse;
import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private Boolean parse;
    private ExecutableElement parseElement;

    private Ast ast;
    private OverloadingContext overloadingContext;
    private List<VariableContext> variableContexts;

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
            } else if ("pares".equals(name)) {
                parseElement = executableElement;
                parse = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                    @Override
                    public Boolean visitBoolean(boolean b, Object object) {
                        return b;
                    }
                }, null);
            }
        });
        numberType = numberType != null ? numberType : NumberType.INHERIT;
        parse = parse != null ? parse : true;
        if (exp == null || exp.isBlank()) {
            throw new ElementException("Statement#exp is blank", expElement);
        }
    }

    public TypeMirror getType() {
        return type;
    }

    public String getVarName() {
        return varName;
    }

    public String getExp() {
        return exp;
    }

    public VariableContext setup(AstParse astParse, OverloadingContext overloadingContext, List<VariableContext> variableContexts, List<String> imports, NumberType numberType) {
        this.numberType = this.numberType != NumberType.INHERIT ? this.numberType : numberType;
        this.overloadingContext = overloadingContext;
        this.variableContexts = new ArrayList<>(variableContexts);
        setupAst(astParse, imports);
        return type.getKind() != TypeKind.VOID ? new VariableContext(varName, type.toString(), index) : null;
    }

    private void setupAst(AstParse astParse, List<String> imports) {
        if (!parse) {
            return;
        }
        try {
            ast = astParse.parse(exp, new SymbolContext(this.variableContexts, imports));
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, expElement);
        }
    }

    public Stream<String> getUseClasses() {
        Map<String, VariableContext> variableContextMap = variableContexts.stream().collect(Collectors.toMap(VariableContext::name, Function.identity()));
        return Stream.of(
                Stream.of(type).filter(typeMirror -> !typeMirror.getKind().isPrimitive()).map(TypeMirror::toString),
                parse
                        ? ast.accept(GetImportVisitor.INSTANCE, new GetImportVisitor.ExpContext(overloadingContext, variableContextMap, numberType)).stream()
                        : Stream.<String>empty()
        ).flatMap(Function.identity());
    }

    public String write(Map<String, String> importMap) {
        String type;
        String simpleType;
        try {
            type = this.type.toString();
            simpleType = importMap.getOrDefault(type, type);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (this.type.getKind() != TypeKind.VOID) {
                stringBuilder
                        .append(simpleType)
                        .append(" ")
                        .append(varName)
                        .append(" = ");
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, varNameElement);
        }
        if (!parse) {
            return stringBuilder.append(exp).append(";").toString();
        }
        ExpVisitor.ExpContext expContext;
        ExpVisitor.ExpResult result;
        try {
            Map<String, VariableContext> variableContextMap = variableContexts.stream().collect(Collectors.toMap(VariableContext::name, Function.identity()));
            expContext = new ExpVisitor.ExpContext(stringBuilder, overloadingContext, variableContextMap, importMap, numberType);
            result = ast.accept(ExpVisitor.INSTANCE, expContext);
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, expElement);
        }
        try {
            if (type.equals(result.getType())) {
                return expContext.append(";").toString();
            }
            CastContext cast = result.cast(type);
            return switch (cast.type()) {
                case CAST -> "(" + simpleType + ")" + expContext + ";";
                case NEW -> "new " + simpleType + "(" + expContext + ");";
                case METHOD -> expContext.append(".").append(cast.name()).append("()'").toString();
                case STATIC_METHOD -> importMap.getOrDefault(cast.className(), cast.className()) + "." + cast.name() +
                        "(" + expContext + ");";
            };
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }


}
