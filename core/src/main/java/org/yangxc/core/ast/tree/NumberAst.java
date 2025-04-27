package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

import java.util.Objects;

public class NumberAst implements Ast {

    private final Token sign;
    private final Token integer;
    private final Token decimal;

    public NumberAst(Token sign, Token integer, Token decimal) {
        this.sign = sign;
        this.integer = Objects.requireNonNull(integer);
        this.decimal = decimal;
    }

    public Token getSign() {
        return sign;
    }

    public Token getInteger() {
        return integer;
    }

    public Token getDecimal() {
        return decimal;
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        if (sign != null) {
            return "(" + sign.getValue() + integer.getValue() + (decimal != null ? ("." + decimal.getValue()) : "") + ")";
        }
        return integer.getValue() + (decimal != null ? ("." + decimal.getValue()) : "");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NumberAst numberAst = (NumberAst) o;
        return Objects.equals(sign, numberAst.sign) && Objects.equals(integer, numberAst.integer) && Objects.equals(decimal, numberAst.decimal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign, integer, decimal);
    }

}
