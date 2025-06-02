package org.yangxc.operatoroverloading.core.ast.tree;

import org.yangxc.operatoroverloading.core.ast.AstVisitor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VariableAst implements Ast {

    private final List<Token> var;
    private final String qualifiedName;

    public VariableAst(Token var) {
        this.var = Stream.of(var).collect(Collectors.toList());
        this.qualifiedName = var.getValue();
    }

    public VariableAst(List<Token> var, String qualifiedName) {
        this.var = var;
        this.qualifiedName = qualifiedName;
    }

    public String qualifiedName() {
        return qualifiedName;
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariableAst that = (VariableAst) o;
        return Objects.equals(var, that.var);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(var);
    }

}
