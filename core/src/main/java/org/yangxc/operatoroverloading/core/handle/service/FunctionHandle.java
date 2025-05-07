package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.DocType;
import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.annotation.OperatorFunction;
import org.yangxc.operatoroverloading.core.ast.AstParse;
import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.constant.ClassName;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.writer.FunctionWriterContext;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionHandle {

    private final ExecutableElement element;

    private ExecutableElement valueElement;
    private String value;
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
    private List<VariableContext> variableContexts;

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
                    } else if ("doc".equals(name)) {
                        docElement = executableElement;
                        docType = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                            @Override
                            public DocType visitEnumConstant(VariableElement c, Object object) {
                                return DocType.valueOf(c.getSimpleName().toString());
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
        if (value == null || value.isBlank()) {
            throw new ElementException("OperatorFunction#value is blank", element);
        }
        numberType = numberType != null ? numberType : NumberType.INHERIT;
        docType = docType != null ? docType : DocType.INHERIT;
        statementHandles = statementHandles != null ? statementHandles : List.of();
        parse = parse != null ? parse : true;
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext, NumberType numberType, DocType docType, Elements elementUtils, List<String> imports) {
        this.numberType = this.numberType != NumberType.INHERIT ? this.numberType : numberType;
        this.overloadingContext = overloadingContext;
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
                exp.add("   return " + value);
                exp.add(" </pre>");
                if (this.docType == DocType.EXP) {
                    docLines = exp;
                    return;
                }
            }
            String docComment = elementUtils.getDocComment(element);
            List<String> docs = docComment != null ? Arrays.stream(docComment.split("\n")).toList() : null;
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
            variableContexts = new ArrayList<>(element.getParameters().stream().map(element -> {
                try {
                    return new VariableContext(element.getSimpleName().toString(), element.asType().toString(), -1);
                } catch (ElementException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new ElementException(e, element);
                }
            }).toList());
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    private void setupStatement(AstParse astParse, OverloadingContext overloadingContext, List<String> imports) {
        try {
            for (StatementHandle statementHandle : statementHandles) {
                VariableContext variableContext = statementHandle.setup(astParse, overloadingContext, variableContexts, imports, this.numberType);
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
            ast = astParse.parse(value, new SymbolContext(variableContexts, imports));
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, valueElement);
        }
    }

    public ExecutableElement getElement() {
        return element;
    }

    public String getExp() {
        return value;
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public DocType getDocType() {
        return docType;
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

    public FunctionWriterContext writerContext(Map<String, String> importMap) {
        try {
            FunctionWriterContext write = new FunctionWriterContext();
            write.setDocLines(docLines);
            String returnType = element.getReturnType().toString();
            write.setReturnType(importMap.getOrDefault(returnType, returnType));
            write.setName(element.getSimpleName().toString());
            List<FunctionWriterContext.Param> params = element.getParameters().stream().map(variableElement -> {
                try {
                    String type = variableElement.asType().toString();
                    return new FunctionWriterContext.Param(importMap.getOrDefault(type, type), variableElement.getSimpleName().toString());
                } catch (ElementException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new ElementException(e, variableElement);
                }
            }).toList();
            write.setParams(params);
            write.setThrowList(element.getThrownTypes().stream().map(TypeMirror::toString).map(throwType -> importMap.getOrDefault(throwType, throwType)).toList());
            List<String> statementLine = statementHandles.stream().map(statementHandle -> statementHandle.write(importMap)).toList();
            StringBuilder retLine = new StringBuilder();
            if (element.getReturnType().getKind() != TypeKind.VOID) {
                retLine.append("return ");
            }
            retLine.append(getExpString(ast, importMap, element.getReturnType()));
            write.setBodyLines(Stream.concat(statementLine.stream(), Stream.of(retLine.toString())).toList());
            return write;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, element);
        }
    }

    private String getExpString(Ast ast, Map<String, String> importMap, TypeMirror resType) {
        if (!parse) {
            return value + ";";
        }
        Map<String, VariableContext> variableContextMap = variableContexts.stream().collect(Collectors.toMap(VariableContext::name, Function.identity()));
        ExpVisitor.ExpContext expContext = new ExpVisitor.ExpContext(overloadingContext, variableContextMap, importMap, numberType);
        ExpVisitor.ExpResult result = ast.accept(ExpVisitor.INSTANCE, expContext);
        String type = resType.toString();
        if (Objects.equals(result.getType(), type)) {
            return expContext.append(";").toString();
        }
        CastContext cast = result.cast(type);
        String simpleType = importMap.getOrDefault(type, type);
        return switch (cast.type()) {
            case CAST -> "(" + simpleType + ")" + expContext + ";";
            case NEW -> "new " + simpleType + "(" + expContext + ");";
            case METHOD -> expContext.append(".").append(cast.name()).append("();").toString();
            case STATIC_METHOD -> cast.name() + "(" + expContext + ");";
        };
    }

}
