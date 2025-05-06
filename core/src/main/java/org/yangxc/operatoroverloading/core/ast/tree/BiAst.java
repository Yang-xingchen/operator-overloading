package org.yangxc.operatoroverloading.core.ast.tree;

import org.yangxc.operatoroverloading.core.ast.AstVisitor;

public abstract class BiAst implements Ast{

    protected final Ast left;
    protected final Ast right;

    protected BiAst(Ast left, Ast right) {
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

}
