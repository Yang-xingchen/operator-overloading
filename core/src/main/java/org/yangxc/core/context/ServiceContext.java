package org.yangxc.core.context;

import org.yangxc.core.ast.AstParse;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceContext {

    private final AstParse astParse;
    private final TypeElement typeElement;
    private List<FunctionContext> functionContexts;

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

    public void init() {
        for (FunctionContext functionContext : functionContexts) {
            functionContext.init(astParse);
        }
    }

    public String getPackage() {
        String qualifiedName = typeElement.getQualifiedName().toString();
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        return qualifiedName.substring(0, i);
    }

    public String getClassName() {
        return typeElement.getSimpleName() + "Impl";
    }

    public String getQualifiedName() {
        return typeElement.getQualifiedName() + "Impl";
    }

    public String write() {
        String pack = getPackage();
        pack = pack != null ? ("package " + pack + ";\n") : "";
        String imports = functionContexts.stream()
                .flatMap(functionContext -> functionContext.getUseClasses().stream())
                .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                .distinct()
                .sorted()
                .map(typeMirror -> "import " + typeMirror + ";")
                .collect(Collectors.joining("\n"));
        String className = "public class " + getClassName() +" implements " + typeElement.getSimpleName() + "{\n";
        String methods = functionContexts.stream()
                .map(FunctionContext::toMethod)
                .map(s -> "\n@Override\n" + s)
                .collect(Collectors.joining());
        return pack + "\n" + imports + "\n" + className + methods + "}";
    }

}
