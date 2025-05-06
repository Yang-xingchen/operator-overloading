package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.DocType;
import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.annotation.OperatorService;
import org.yangxc.operatoroverloading.core.ast.AstParse;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ServiceHandle {

    private final AstParse astParse;
    private final TypeElement typeElement;

    private ExecutableElement valueElement;
    private String value;
    private ExecutableElement numberTypeElement;
    private NumberType numberType;
    private ExecutableElement importsElement;
    private List<String> imports;
    private List<FunctionHandle> functionHandles;
    private DocType docType;
    private ExecutableElement docElement;
    private List<String> docLines;

    public static final String TAB = "    ";

    public ServiceHandle(TypeElement typeElement) {
        this(AstParse.DEFAULT, typeElement);
    }

    public ServiceHandle(AstParse astParse, TypeElement typeElement) {
        this.astParse = astParse;
        this.typeElement = typeElement;
        typeElement.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> OperatorService.class.getTypeName().equals(annotationMirror.getAnnotationType().toString()))
                .findAny()
                .orElseThrow(() -> new ElementException("@OperatorService not found", typeElement))
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
                    } else if ("numberType".equals(name)) {
                        numberTypeElement = executableElement;
                        numberType = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                            @Override
                            public NumberType visitEnumConstant(VariableElement c, Object object) {
                                return NumberType.valueOf(c.getSimpleName().toString());
                            }
                        }, null);
                    } else if ("imports".equals(name)) {
                        importsElement = executableElement;
                        imports = annotationValue.accept(new BaseAnnotationValueVisitor<>() {
                            @Override
                            public List<String> visitArray(List<? extends AnnotationValue> vals, Object object) {
                                return vals.stream().map(val -> val.accept(new BaseAnnotationValueVisitor<String, Object>() {
                                    @Override
                                    public String visitType(TypeMirror t, Object object) {
                                        return t.toString();
                                    }
                                }, null)).toList();
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
                    }
                });
        value = value != null && value.trim().isEmpty() ? value.trim() : (typeElement.getSimpleName() + "Impl");
        numberType = numberType != null && numberType != NumberType.INHERIT ? numberType : NumberType.BIG_DECIMAL;
        docType = docType != null && docType != DocType.INHERIT ? docType : DocType.DOC;
        imports = imports != null ? imports : List.of();
    }

    public void setFunctionContexts(List<FunctionHandle> functionHandles) {
        this.functionHandles = functionHandles;
    }

    public void setup(OverloadingContext overloadingContext, Elements elementUtils) {
        setupDoc(elementUtils);
        setupFunction(overloadingContext, elementUtils);
    }

    private void setupDoc(Elements elementUtils) {
        try {
            if (docType == DocType.DOC || docType == DocType.DOC_EXP) {
                String docComment = elementUtils.getDocComment(typeElement);
                docLines = docComment != null ? Arrays.stream(docComment.split("\n")).toList() : null;
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, docElement);
        }
    }

    private void setupFunction(OverloadingContext overloadingContext, Elements elementUtils) {
        try {
            for (FunctionHandle functionHandle : functionHandles) {
                functionHandle.setup(astParse, overloadingContext, numberType, docType, elementUtils, imports);
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    private String getPackage() {
        String qualifiedName = typeElement.getQualifiedName().toString();
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        return qualifiedName.substring(0, i);
    }

    private String getClassName() {
        return value;
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public DocType getDocType() {
        return docType;
    }

    public List<FunctionHandle> getFunctionHandles() {
        return functionHandles;
    }

    public String getQualifiedName() {
        String aPackage = getPackage();
        if (aPackage == null) {
            return getClassName();
        }
        return aPackage + "." + getClassName();
    }

    public ServiceWriterContext writerContext() {
        try {
            ServiceWriterContext context = new ServiceWriterContext();
            context.setDocLines(docLines);
            context.setPack(getPackage());
            context.setClassName(getClassName());
            context.setInterfaceName(typeElement.getSimpleName().toString());
            List<String> imports = Stream.concat(
                    functionHandles.stream().flatMap(functionHandle -> functionHandle.getUseClasses().stream()),
                    this.imports.stream()
            ).toList();
            Map<String, String> importMap = context.handelImport(imports);
            context.setFunctionWrites(functionHandles.stream().map(functionHandle -> functionHandle.writerContext(importMap)).toList());
            return context;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }

}
