package org.yangxc.core.context;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.ast.AstParse;
import org.yangxc.core.ast.tree.Ast;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionContext {

    private final OperatorFunction operatorFunction;
    private final ExecutableElement element;
    private Ast ast;

    public FunctionContext(ExecutableElement element) {
        operatorFunction = element.getAnnotation(OperatorFunction.class);
        this.element = element;
    }

    public void init(AstParse astParse) {
        ast = astParse.parse(operatorFunction.value());
    }

    public Set<TypeMirror> getUseClasses() {
        return Stream.of(
                Stream.of(element.getReturnType()),
                element.getParameters().stream().map(VariableElement::asType)
        ).flatMap(Function.identity()).collect(Collectors.toSet());
    }

    public String toMethod() {
        String parameters = element.getParameters()
                .stream()
                .map(e -> e.asType() + " " + e.getSimpleName())
                .collect(Collectors.joining(", "));
        String body = "return " + ast.toString() + ";";
        return "public " + element.getReturnType() + " " + element.getSimpleName() + "(" + parameters + ") {" + body + "}\n";
    }

}
