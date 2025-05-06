package org.yangxc.operatoroverloading.core.ast.tree;

import org.yangxc.operatoroverloading.core.ast.AstVisitor;

public interface Ast {

    <T, R> R accept(AstVisitor<T, R> visitor, T t);

}
