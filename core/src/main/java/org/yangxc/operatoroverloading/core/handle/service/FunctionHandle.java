package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.DocType;
import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.annotation.ServiceFunction;
import org.yangxc.operatoroverloading.core.ast.AstParse;
import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.constant.CastMethodType;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.writer.FunctionWriterContext;
import org.yangxc.operatoroverloading.core.handle.writer.Param;
import org.yangxc.operatoroverloading.core.util.GetAnnotationValueVisitor;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionHandle {

    private final ExecutableElement element;

    private ExecutableElement expElement;
    private String exp;
    private ExecutableElement statementsElement;
    private List<StatementHandle> statementHandles;
    private ExecutableElement numberTypeElement;
    private NumberType numberType;
    private DocType docType;
    private ExecutableElement docElement;
    private Boolean parse;
    private ExecutableElement parseElement;

    private List<String> docLines;
    private Ast ast;
    private OverloadingContext overloadingContext;
    private VariableSetContext variableContexts;

    public FunctionHandle(ExecutableElement element) {
        this.element = element;
        element.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> ServiceFunction.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .orElseThrow(() -> new ElementException("@ServiceFunction not found", element))
                .getElementValues()
                .forEach((executableElement, annotationValue) -> {
                    try {
                        String name = executableElement.getSimpleName().toString();
                        if ("value".equals(name)) {
                            expElement = executableElement;
                            exp = annotationValue.accept(GetAnnotationValueVisitor.visitString(), null);
                        } else if ("statements".equals(name)) {
                            statementsElement = executableElement;
                            statementHandles = annotationValue.accept(GetAnnotationValueVisitor.visitArray(vals -> {
                                List<StatementHandle> list = new ArrayList<>();
                                for (int i = 0; i < vals.size(); i++) {
                                    AnnotationValue values = vals.get(i);
                                    int index = i;
                                    StatementHandle statementHandle = values.accept(GetAnnotationValueVisitor.visitAnnotation(a -> new StatementHandle(a, index)), null);
                                    list.add(statementHandle);
                                }
                                return list;
                            }), null);
                        } else if ("numberType".equals(name)) {
                            numberTypeElement = executableElement;
                            numberType = annotationValue.accept(GetAnnotationValueVisitor.visitEnum(NumberType.class), null);
                        } else if ("doc".equals(name)) {
                            docElement = executableElement;
                            docType = annotationValue.accept(GetAnnotationValueVisitor.visitEnum(DocType.class), null);
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
        if (exp == null || exp.trim().isEmpty()) {
            throw new ElementException("ServiceFunction#value is blank", element);
        }
        numberType = numberType != null ? numberType : NumberType.INHERIT;
        docType = docType != null ? docType : DocType.INHERIT;
        statementHandles = statementHandles != null ? statementHandles : new ArrayList<>();
        parse = parse != null ? parse : true;
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext, VariableSetContext variableContexts, NumberType numberType, DocType docType, Elements elementUtils, List<String> imports) {
        this.numberType = this.numberType != NumberType.INHERIT ? this.numberType : numberType;
        this.overloadingContext = overloadingContext;
        this.variableContexts = variableContexts;
        setupDoc(docType, elementUtils);
        setupParamVar();
        setupStatement(astParse, overloadingContext, imports);
        setupAst(astParse, imports);
    }

    private void setupDoc(DocType docType, Elements elementUtils) {
        try {
            if (this.docType == DocType.INHERIT) {
                this.docType = docType;
            }
            if (this.docType == DocType.NONE) {
                return;
            }
            List<String> exp = new ArrayList<>();
            if (this.docType == DocType.EXP || this.docType == DocType.DOC_EXP) {
                exp = new ArrayList<>();
                exp.add(" <pre>");
                statementHandles.stream().map(s -> {
                    if (s.getType().getKind() == TypeKind.VOID) {
                        return s.getExp();
                    }
                    return "   " + s.getVarName() + " = " + s.getExp();
                }).forEach(exp::add);
                exp.add("   return " + this.exp);
                exp.add(" </pre>");
                if (this.docType == DocType.EXP) {
                    docLines = exp;
                    return;
                }
            }
            String docComment = elementUtils.getDocComment(element);
            List<String> docs = docComment != null ? Arrays.stream(docComment.split("\n")).collect(Collectors.toList()) : null;
            if (this.docType == DocType.DOC) {
                docLines = docs;
                return;
            }
            if (this.docType == DocType.DOC_EXP) {
                docLines = new ArrayList<>();
                boolean addExp = false;
                if (docs == null) {
                    docLines = exp;
                    return;
                }
                for (String doc : docs) {
                    if (doc.trim().startsWith("@") && !addExp) {
                        addExp = true;
                        docLines.addAll(exp);
                        docLines.add("");
                    }
                    docLines.add(doc);
                }
                if (!addExp) {
                    docLines.addAll(exp);
                }
                return;
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, docElement);
        }
        throw new ElementException("unknown docType: " + this.docType, docElement);
    }

    private void setupParamVar() {
        try {
            for (VariableElement element : element.getParameters()) {
                try {
                    variableContexts.add(VariableContext.createByParam(element.asType().toString(), element.getSimpleName().toString()));
                } catch (ElementException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new ElementException(e, element);
                }
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    private void setupStatement(AstParse astParse, OverloadingContext overloadingContext, List<String> imports) {
        try {
            for (StatementHandle statementHandle : statementHandles) {
                VariableContext variableContext = statementHandle.setup(astParse, overloadingContext, variableContexts.copy(), imports, this.numberType);
                if (variableContext != null) {
                    variableContexts.add(variableContext);
                }
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, statementsElement);
        }
    }

    private void setupAst(AstParse astParse, List<String> imports) {
        if (!parse) {
            return;
        }
        try {
            ast = astParse.parse(exp, new SymbolContext(variableContexts, imports));
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, expElement);
        }
    }

    public ExecutableElement getElement() {
        return element;
    }

    public String getExp() {
        return exp;
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public DocType getDocType() {
        return docType;
    }

    public Stream<String> getUseClasses() {
        Stream<String> parseImport = Stream.empty();
        if (parse) {
            GetImportVisitor.ExpResult result = ast.accept(GetImportVisitor.INSTANCE, new GetImportVisitor.ExpContext(overloadingContext, variableContexts, numberType));
            if (Objects.equals(result.getType(), element.getReturnType().toString())) {
                parseImport = result.stream();
            } else {
                CastContext cast = result.cast(element.getReturnType().toString());
                Stream<String> returnCast = cast.getType() == CastMethodType.STATIC_METHOD ? Stream.of(cast.getClassName()) : Stream.empty();
                parseImport = Stream.concat(result.stream(), returnCast);
            }
        }
        return Stream.of(
                Stream.of(element.getReturnType())
                        .map(TypeMirror::toString),
                element.getParameters().stream()
                        .map(VariableElement::asType)
                        .map(TypeMirror::toString),
                parseImport,
                statementHandles.stream().flatMap(StatementHandle::getUseClasses)
        ).flatMap(Function.identity());
    }

    public FunctionWriterContext writerContext(ImportContext importContext) {
        try {
            FunctionWriterContext write = new FunctionWriterContext();
            write.setDocLines(docLines);
            String returnType = element.getReturnType().toString();
            write.setReturnType(importContext.getSimpleName(returnType));
            write.setName(element.getSimpleName().toString());
            List<Param> params = element.getParameters().stream().map(variableElement -> {
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
            write.setThrowList(element.getThrownTypes().stream().map(TypeMirror::toString).map(importContext::getSimpleName).collect(Collectors.toList()));
            List<String> statementLine = statementHandles.stream().map(statementHandle -> statementHandle.write(importContext)).collect(Collectors.toList());
            StringBuilder retLine = new StringBuilder();
            if (element.getReturnType().getKind() != TypeKind.VOID) {
                retLine.append("return ");
            }
            retLine.append(getExpString(ast, importContext, element.getReturnType()));
            write.setBodyLines(Stream.concat(statementLine.stream(), Stream.of(retLine.toString())).collect(Collectors.toList()));
            return write;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    private String getExpString(Ast ast, ImportContext importContext, TypeMirror resType) {
        if (!parse) {
            return exp + ";";
        }
        ExpVisitor.ExpContext expContext = new ExpVisitor.ExpContext(overloadingContext, variableContexts, importContext, numberType);
        ExpVisitor.ExpResult result = ast.accept(ExpVisitor.INSTANCE, expContext);
        String type = resType.toString();
        if (Objects.equals(result.getType(), type)) {
            return expContext.append(";").toString();
        }
        CastContext cast = result.cast(type);
        String simpleType = importContext.getSimpleName(type);
        if (cast.getType() == CastMethodType.CAST) {
            return "(" + simpleType + ")" + expContext + ";";
        } else if (cast.getType() == CastMethodType.NEW) {
            return "new " + simpleType + "(" + expContext + ");";
        } else if (cast.getType() == CastMethodType.METHOD) {
            return expContext.append(".").append(cast.getName()).append("();").toString();
        } else if (cast.getType() == CastMethodType.STATIC_METHOD) {
            return importContext.getSimpleName(cast.getClassName()) + "." + cast.getName() + "(" + expContext + ");";
        }
        throw new UnsupportedOperationException("unknown CastMethodType: " + cast.getType());
    }

}
