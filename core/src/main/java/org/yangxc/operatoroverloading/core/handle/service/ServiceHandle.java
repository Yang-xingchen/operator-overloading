package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.DocType;
import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.annotation.OperatorService;
import org.yangxc.operatoroverloading.core.ast.AstParse;
import org.yangxc.operatoroverloading.core.exception.ElementException;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;
import org.yangxc.operatoroverloading.core.util.GetAnnotationValueVisitor;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceHandle {

    private final AstParse astParse;
    private final TypeElement typeElement;

    private ExecutableElement classNameElement;
    private String className;
    private ExecutableElement numberTypeElement;
    private NumberType numberType;
    private ExecutableElement importsElement;
    private List<String> imports;
    private List<FieldHandle> fieldHandles;
    private List<FunctionHandle> functionHandles;
    private DocType docType;
    private ExecutableElement docElement;
    private List<String> docLines;
    private VariableSetContext variableContexts;

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
                    try {
                        String name = executableElement.getSimpleName().toString();
                        if ("value".equals(name)) {
                            classNameElement = executableElement;
                            className = annotationValue.accept(GetAnnotationValueVisitor.visitString(), null);
                        } else if ("numberType".equals(name)) {
                            numberTypeElement = executableElement;
                            numberType = annotationValue.accept(GetAnnotationValueVisitor.visitEnum(NumberType.class), null);
                        } else if ("imports".equals(name)) {
                            importsElement = executableElement;
                            imports = annotationValue.accept(GetAnnotationValueVisitor.visitArrayEach(GetAnnotationValueVisitor.visitType(TypeMirror::toString)), null);
                        } else if ("doc".equals(name)) {
                            docElement = executableElement;
                            docType = annotationValue.accept(GetAnnotationValueVisitor.visitEnum(DocType.class), null);
                        }
                    } catch (ElementException e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new ElementException(e, executableElement);
                    }
                });
        className = className != null && !className.trim().isEmpty() ? className.trim() : (typeElement.getSimpleName() + "Impl");
        numberType = numberType != null && numberType != NumberType.INHERIT ? numberType : NumberType.BIG_DECIMAL;
        docType = docType != null && docType != DocType.INHERIT ? docType : DocType.DOC;
        imports = imports != null ? imports : new ArrayList<>();
    }

    public void setServiceFieldHandles(List<FieldHandle> fieldHandles) {
        this.fieldHandles = fieldHandles;
    }

    public void setFunctionContexts(List<FunctionHandle> functionHandles) {
        this.functionHandles = functionHandles;
    }

    public void setup(OverloadingContext overloadingContext, Elements elementUtils, VariableSetContext variableContexts) {
        setupDoc(elementUtils);
        setupField(variableContexts, elementUtils);
        setupFunction(overloadingContext, elementUtils);
    }

    private void setupDoc(Elements elementUtils) {
        try {
            if (docType == DocType.DOC || docType == DocType.DOC_EXP) {
                String docComment = elementUtils.getDocComment(typeElement);
                docLines = docComment != null ? Arrays.stream(docComment.split("\n")).collect(Collectors.toList()) : null;
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, docElement);
        }
    }

    private void setupField(VariableSetContext variableContexts, Elements elementUtils) {
        try {
            fieldHandles.forEach(fieldHandle -> fieldHandle.setup(variableContexts, elementUtils));
            this.variableContexts = variableContexts;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }

    private void setupFunction(OverloadingContext overloadingContext, Elements elementUtils) {
        try {
            for (FunctionHandle functionHandle : functionHandles) {
                functionHandle.setup(astParse, overloadingContext, variableContexts.copy(), numberType, docType, elementUtils, imports);
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

    public String getPackage() {
        String qualifiedName = typeElement.getQualifiedName().toString();
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        return qualifiedName.substring(0, i);
    }

    public String getInterfaceName() {
        return typeElement.getSimpleName().toString();
    }

    public String getImplClassName() {
        return className;
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
            return getImplClassName();
        }
        return aPackage + "." + getImplClassName();
    }

    public ServiceWriterContext writerContext() {
        try {
            ServiceWriterContext context = new ServiceWriterContext();
            context.setDocLines(docLines);
            context.setPack(getPackage());
            context.setClassName(getImplClassName());
            context.setInterfaceName(getInterfaceName());
            Set<String> imports = Stream.concat(
                    functionHandles.stream().flatMap(FunctionHandle::getUseClasses),
                    this.imports.stream()
            ).collect(Collectors.toSet());
            ImportContext importContext = context.handelImport(imports);
            context.setFieldList(fieldHandles.stream().flatMap(FieldHandle::writeParams).collect(Collectors.toList()));
            context.setFunctionWrites(Stream.concat(
                    fieldHandles.stream().map(fieldHandle -> fieldHandle.writeFunction(importContext)).filter(Objects::nonNull),
                    functionHandles.stream().map(functionHandle -> functionHandle.writerContext(importContext))
            ).collect(Collectors.toList()));
            return context;
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }

}
