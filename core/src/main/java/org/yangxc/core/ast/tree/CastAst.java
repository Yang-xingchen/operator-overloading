package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

import java.util.List;
import java.util.Objects;

public class CastAst implements Ast {

    private final TypeAst typeAst;
    private final Ast ast;

    public CastAst(TypeAst typeAst, Ast ast) {
        this.typeAst = typeAst;
        this.ast = ast;
    }

    public String getSourceType() {
        return typeAst.getSourceType();
    }

    public List<Token> getTypeTokens() {
        return typeAst.getTokens();
    }

    public Ast getAst() {
        return ast;
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return "(" + typeAst + ")" + ast;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CastAst castAst = (CastAst) object;
        return Objects.equals(typeAst, castAst.typeAst) && Objects.equals(ast, castAst.ast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeAst, ast);
    }

}
