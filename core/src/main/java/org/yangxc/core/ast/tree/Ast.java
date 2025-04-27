package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

public interface Ast {

    <T, R> R accept(AstVisitor<T, R> visitor, T t);

}
