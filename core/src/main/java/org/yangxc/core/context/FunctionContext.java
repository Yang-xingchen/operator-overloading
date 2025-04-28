package org.yangxc.core.context;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.constany.ClassName;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionContext {

    private final OperatorFunction operatorFunction;
    private final ExecutableElement element;
    private Ast ast;
    private OverloadingContext overloadingContext;
    private Map<String, VariableContext> variableContexts;

    public FunctionContext(ExecutableElement element) {
        operatorFunction = element.getAnnotation(OperatorFunction.class);
        this.element = element;
    }

    public void setup(AstParse astParse, OverloadingContext overloadingContext) {
        this.overloadingContext = overloadingContext;
        ast = astParse.parse(operatorFunction.value());
        variableContexts = element.getParameters().stream()
                .map(element -> new VariableContext(element.getSimpleName().toString(), element.asType().toString(), 0))
                .collect(Collectors.toMap(VariableContext::name, Function.identity(), (c1, c2) -> c1));
    }

    public Set<String> getUseClasses() {
        return Stream.of(
                Stream.of(element.getReturnType())
                        .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                        .map(TypeMirror::toString),
                element.getParameters().stream().map(VariableElement::asType)
                        .filter(typeMirror -> !typeMirror.getKind().isPrimitive())
                        .map(TypeMirror::toString),
                Stream.of(ClassName.BIG_DECIMAL)
        ).flatMap(Function.identity()).collect(Collectors.toSet());
    }

    public String toMethod() {
        String parameters = element.getParameters()
                .stream()
                .map(e -> e.asType() + " " + e.getSimpleName())
                .collect(Collectors.joining(", "));
        String body = ServiceContext.TAB + ServiceContext.TAB + "return " + getExpString(ast, element.getReturnType()) + "\n";
        return ServiceContext.TAB + "public " + element.getReturnType() + " " + element.getSimpleName() + "(" + parameters + ") {\n" +
                body +
                ServiceContext.TAB + "}\n";
    }

    private String getExpString(Ast ast, TypeMirror resType) {
        if (resType.getKind() == TypeKind.VOID) {
            return "";
        }
        ExpVisitor.ExpContext expContext = new ExpVisitor.ExpContext(overloadingContext, variableContexts);
        ExpVisitor.ExpResult result = ast.accept(ExpVisitor.INSTANCE, expContext);
        if (!Objects.equals(result.getType(), resType.getKind().toString())) {
            switch (resType.getKind()) {
                case BYTE -> expContext.append(".").append(result.getOverloadingContext().getToByte().name()).append("()");
                case SHORT -> expContext.append(".").append(result.getOverloadingContext().getToShort().name()).append("()");
                case INT -> expContext.append(".").append(result.getOverloadingContext().getToInt().name()).append("()");
                case LONG -> expContext.append(".").append(result.getOverloadingContext().getToLong().name()).append("()");
                case FLOAT -> expContext.append(".").append(result.getOverloadingContext().getToFloat().name()).append("()");
                case DOUBLE -> expContext.append(".").append(result.getOverloadingContext().getToDouble().name()).append("()");
            }
        }
        return expContext.append(";").toString();
    }

}
