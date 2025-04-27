package org.yangxc.core.ast;

import org.yangxc.core.ast.tree.*;

public interface AstVisitor<T, R> {

    default R visit(Token ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(VariableAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(NumberAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(PlusAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(SubtractAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(MultiplyAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(DivideAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R visit(RemainderAst ast, T t) {
        return defaultVisit(ast, t);
    }

    default R defaultVisit(Ast ast, T t) {
        return null;
    }

}
