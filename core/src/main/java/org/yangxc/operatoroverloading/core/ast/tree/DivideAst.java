package org.yangxc.operatoroverloading.core.ast.tree;

import org.yangxc.operatoroverloading.core.ast.AstVisitor;

public class DivideAst extends BiAst implements Ast {

    public DivideAst(Ast left, Ast right) {
        super(left, right);
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return "(" + left + "/" + right + ")";
    }

}
