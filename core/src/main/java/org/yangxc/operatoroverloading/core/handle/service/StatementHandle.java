package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.ast.AstParse;
import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.constant.CastMethodType;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.util.GetAnnotationValueVisitor;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
    private VariableSetContext variableContexts;

    public StatementHandle(AnnotationMirror annotationMirror, int index) {
        this.index = index;
        annotationMirror.getElementValues().forEach((executableElement, annotationValue) -> {
            try {
                String name = executableElement.getSimpleName().toString();
                if ("type".equals(name)) {
                    typeElement = executableElement;
                    type = annotationValue.accept(GetAnnotationValueVisitor.visitType(), null);
                } else if ("varName".equals(name)) {
                    varNameElement = executableElement;
                    varName = annotationValue.accept(GetAnnotationValueVisitor.visitString(), null);
                } else if ("exp".equals(name)) {
                    expElement = executableElement;
                    exp = annotationValue.accept(GetAnnotationValueVisitor.visitString(), null);
                } else if ("numberType".equals(name)) {
                    numberTypeElement = executableElement;
                    numberType = annotationValue.accept(GetAnnotationValueVisitor.visitEnum(NumberType.class), null);
                } else if ("pares".equals(name)) {
                    parseElement = executableElement;
                    parse = annotationValue.accept(GetAnnotationValueVisitor.visitBoolean(), null);
                }
            } catch (ElementException e) {
                throw e;
            } catch (Throwable e) {
                throw new ElementException(e, executableElement);
            }
        });
        numberType = numberType != null ? numberType : NumberType.INHERIT;
        parse = parse != null ? parse : true;
        if (exp == null || exp.trim().isEmpty()) {
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

    public VariableContext setup(AstParse astParse, OverloadingContext overloadingContext, VariableSetContext variableContexts, List<String> imports, NumberType numberType) {
        this.numberType = this.numberType != NumberType.INHERIT ? this.numberType : numberType;
        this.overloadingContext = overloadingContext;
        this.variableContexts = variableContexts;
        setupAst(astParse, imports);
        return type.getKind() != TypeKind.VOID ? VariableContext.createByLocal(type.toString(), varName, index) : null;
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
        Stream<String> parseImport = Stream.empty();
        if (parse) {
            GetImportVisitor.ExpResult result = ast.accept(GetImportVisitor.INSTANCE, new GetImportVisitor.ExpContext(overloadingContext, variableContexts, numberType));
            if (Objects.equals(result.getType(), type.toString())) {
                parseImport = result.stream();
            } else {
                CastContext cast = result.cast(type.toString());
                Stream<String> returnCast = cast.getType() == CastMethodType.STATIC_METHOD ? Stream.of(cast.getClassName()) : Stream.empty();
                parseImport = Stream.concat(result.stream(), returnCast);
            }
        }
        return Stream.of(
                Stream.of(type).map(TypeMirror::toString),
                parseImport
        ).flatMap(Function.identity());
    }

    public String write(ImportContext importContext) {
        String type;
        String simpleType;
        try {
            type = this.type.toString();
            simpleType = importContext.getSimpleName(type);
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
            expContext = new ExpVisitor.ExpContext(stringBuilder, overloadingContext, variableContexts, importContext, numberType);
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
            if (cast.getType() == CastMethodType.CAST) {
                return "(" + simpleType + ")" + expContext + ";";
            } else if (cast.getType() == CastMethodType.NEW) {
                return "new " + simpleType + "(" + expContext + ");";
            } else if (cast.getType() == CastMethodType.METHOD) {
                return expContext.append(".").append(cast.getName()).append("()'").toString();
            } else if (cast.getType() == CastMethodType.STATIC_METHOD) {
                return importContext.getSimpleName(cast.getClassName()) + "." + cast.getName() +
                        "(" + expContext + ");";
            }
            throw new IllegalArgumentException();
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }


}
