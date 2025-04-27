package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

public class MultiplyAst implements Ast {

    private final Ast left;
    private final Ast right;

    public MultiplyAst(Ast left, Ast right) {
        this.left = left;
        this.right = right;
    }

    public Ast getLeft() {
        return left;
    }

    public Ast getRight() {
        return right;
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return "(" + left + "*" + right + ")";
    }

}
