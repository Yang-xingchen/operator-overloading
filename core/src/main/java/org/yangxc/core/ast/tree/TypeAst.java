package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

import java.util.List;
import java.util.Objects;

public class TypeAst implements Ast {

    private final List<Token> tokens;
    private final String sourceType;

    public TypeAst(List<Token> tokens, String sourceType) {
        this.tokens = tokens;
        this.sourceType = sourceType;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getSourceType() {
        return sourceType;
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return sourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeAst that = (TypeAst) o;
        return Objects.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tokens);
    }

}
