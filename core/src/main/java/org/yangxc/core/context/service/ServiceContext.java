package org.yangxc.core.context.service;

import org.yangxc.core.annotation.OperatorService;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.context.overloading.OverloadingContext;
import org.yangxc.core.exception.ElementException;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceContext {

    private final AstParse astParse;
    private final TypeElement typeElement;
    private List<FunctionContext> functionContexts;
    public static final String TAB = "    ";

    public ServiceContext(TypeElement typeElement) {
        astParse = AstParse.DEFAULT;
        this.typeElement = typeElement;
    }

    public ServiceContext(AstParse astParse, TypeElement typeElement) {
        this.astParse = astParse;
        this.typeElement = typeElement;
    }

    public ServiceContext setFunctionContexts(List<FunctionContext> functionContexts) {
        this.functionContexts = functionContexts;
        return this;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setup(OverloadingContext overloadingContext) {
        try {
            for (FunctionContext functionContext : functionContexts) {
                functionContext.setup(astParse, overloadingContext);
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
            String imports = functionContexts.stream()
                    .flatMap(functionContext -> functionContext.getUseClasses().stream())
                    .distinct()
                    .sorted()
                    .reduce(new StringBuilder(),
                            (sb, type) -> sb.append("import ").append(type).append(";\n"),
                            StringBuilder::append)
                    .toString();
            String className = "public class " + getClassName() +" implements " + typeElement.getSimpleName() + " {\n";
            String methods = functionContexts.stream()
                    .map(FunctionContext::toMethod)
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
