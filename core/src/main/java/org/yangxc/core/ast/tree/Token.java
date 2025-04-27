package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

import java.util.Objects;

public class Token implements Ast {

    private final String value;

    public static final Token DOT = new Token(".");
    public static final Token PLUS = new Token("+");
    public static final Token SUBTRACT = new Token("-");
    public static final Token MULTIPLY = new Token("*");
    public static final Token DIVIDE = new Token("/");
    public static final Token REMAINDER = new Token("%");
    public static final Token LEFT_PARENTHESIS = new Token("(");
    public static final Token RIGHT_PARENTHESIS = new Token(")");

    public Token(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isDigit() {
        return value.chars().allMatch(Character::isDigit);
    }

    public boolean isVariable() {
        return Character.isAlphabetic(value.charAt(0));
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        return Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
