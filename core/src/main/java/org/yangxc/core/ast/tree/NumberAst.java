package org.yangxc.core.ast.tree;

import org.yangxc.core.ast.AstVisitor;

import java.math.BigInteger;
import java.util.Objects;

public class NumberAst implements Ast {

    private final Token sign;
    private final Token integer;
    private final Token decimal;

    private final Token eSign;
    private final Token eInteger;

    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

    public NumberAst(Token sign, Token integer, Token decimal) {
        this.sign = sign;
        this.integer = Objects.requireNonNull(integer);
        this.decimal = decimal;
        this.eSign = null;
        this.eInteger = null;
    }

    public NumberAst(Token sign, Token integer, Token decimal, Token eSign, Token eInteger) {
        this.sign = sign;
        this.integer = integer;
        this.decimal = decimal;
        this.eSign = eSign;
        this.eInteger = eInteger;
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

    public Token getESign() {
        return eSign;
    }

    public Token getEInteger() {
        return eInteger;
    }

    public boolean isLong() {
        long value = Long.parseLong(integer.getValue());
        return value > Integer.MAX_VALUE || value < Integer.MIN_VALUE;
    }

    public boolean isDouble() {
        if (decimal != null || eInteger != null) {
            return true;
        }
        BigInteger value = new BigInteger(integer.getValue());
        if (value.compareTo(MAX_LONG) > 0 || value.compareTo(MIN_LONG) < 0) {
            return true;
        }
        return false;
    }

    @Override
    public <T, R> R accept(AstVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    public String value() {
        String res = (sign != null ? sign.getValue() : "") +
                integer.getValue() + (decimal != null ? ("." + decimal.getValue()) : "") +
                (eInteger != null ? ("e" + (eSign != null ? eSign.getValue() : "") + eInteger) : "");
        return res.replace("_", "");
    }

    @Override
    public String toString() {
        String e = "";
        if (eInteger != null) {
            e = "e" + (eSign != null ? eSign.getValue() : "") + eInteger;
        }
        if (sign != null) {
            return "(" + sign.getValue() + integer.getValue() + (decimal != null ? ("." + decimal.getValue()) : "") + e + ")";
        }
        return integer.getValue() + (decimal != null ? ("." + decimal.getValue()) : "") + e;
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
