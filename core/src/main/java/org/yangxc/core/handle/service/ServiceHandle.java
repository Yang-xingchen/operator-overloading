package org.yangxc.core.handle.service;

import org.yangxc.core.annotation.NumberType;
import org.yangxc.core.annotation.OperatorService;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.handle.overloading.OverloadingContext;
import org.yangxc.core.exception.ElementException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceHandle {

    private final AstParse astParse;
    private final TypeElement typeElement;

    private ExecutableElement valueElement;
    private String value;
    private ExecutableElement numberTypeElement;
    private NumberType numberType;
    private List<FunctionHandle> functionHandles;

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
                    }
                });
        value = value != null && value.trim().isEmpty() ? value.trim() : (typeElement.getSimpleName() + "Impl");
        numberType = numberType != null && numberType != NumberType.INHERIT ? numberType : NumberType.BIG_DECIMAL;
    }

    public ServiceHandle setFunctionContexts(List<FunctionHandle> functionHandles) {
        this.functionHandles = functionHandles;
        return this;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setup(OverloadingContext overloadingContext) {
        try {
            for (FunctionHandle functionHandle : functionHandles) {
                functionHandle.setup(astParse, overloadingContext, numberType);
            }
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
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
        OperatorService operatorService = typeElement.getAnnotation(OperatorService.class);
        if (operatorService == null || "".equals(operatorService.value())) {
            return typeElement.getSimpleName() + "Impl";
        }
        return operatorService.value();
    }

    public String getQualifiedName() {
        String aPackage = getPackage();
        if (aPackage == null) {
            return getClassName();
        }
        return aPackage + "." + getClassName();
    }

    public String write() {
        try {
            String pack = getPackage();
            pack = pack != null ? ("package " + pack + ";\n") : "";
            String imports = functionHandles.stream()
                    .flatMap(functionHandle -> functionHandle.getUseClasses().stream())
                    .distinct()
                    .sorted()
                    .reduce(new StringBuilder(),
                            (sb, type) -> sb.append("import ").append(type).append(";\n"),
                            StringBuilder::append)
                    .toString();
            String className = "public class " + getClassName() +" implements " + typeElement.getSimpleName() + " {\n";
            String methods = functionHandles.stream()
                    .map(FunctionHandle::toMethod)
                    .map(s -> "\n" + TAB + "@Override\n" + s)
                    .collect(Collectors.joining());
            return pack + "\n" + imports + "\n" + className + methods + "}";
        } catch (ElementException e) {
            throw e;
        } catch (Throwable e) {
            throw new ElementException(e, typeElement);
        }
    }

}
