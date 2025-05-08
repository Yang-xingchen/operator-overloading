package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.Cast;
import org.yangxc.operatoroverloading.core.annotation.Operator;
import org.yangxc.operatoroverloading.core.annotation.OperatorClass;
import org.yangxc.operatoroverloading.core.annotation.OperatorType;

import java.math.BigDecimal;
import java.util.Objects;

@OperatorClass
public class Complex {

    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    @Cast
    public Complex(BigDecimal bigDecimal) {
        this.real = bigDecimal.doubleValue();
        this.imaginary = 0;
    }

    @Cast
    public Complex2 to() {
        return new Complex2(real, imaginary);
    }

    @Operator(OperatorType.ADD)
    public Complex add(Complex b) {
        return new Complex(real + b.real, imaginary + b.imaginary);
    }

    @Operator(OperatorType.SUBTRACT)
    public Complex subtract(Complex b) {
        return new Complex(real - b.real, imaginary - b.imaginary);
    }

    @Operator(OperatorType.MULTIPLY)
    public Complex multiply(Complex b) {
        double r = real * b.real - imaginary * b.imaginary;
        double i = real * b.imaginary + imaginary * b.real;
        return new Complex(r, i);
    }

    @Operator(OperatorType.DIVIDE)
    public Complex divide(Complex b) {
        double denominator = b.real * b.real + b.imaginary * b.imaginary;
        double r = (real * b.real + imaginary * b.imaginary) / denominator;
        double i = (imaginary * b.real - real * b.imaginary) / denominator;
        return new Complex(r, i);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Complex complex = (Complex) object;
        return Double.compare(real, complex.real) == 0 && Double.compare(imaginary, complex.imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }

    @Cast
    @Override
    public String toString() {
        if (real == 0) {
            if (imaginary == 1) {
                return "i";
            } else if (imaginary == -1) {
                return "-i";
            }
            return imaginary + "i";
        }
        if (imaginary < 0) {
            return real + (imaginary == -1 ? "-i" : (imaginary + "i"));
        }
        return real + "+" + (imaginary == 1 ? "i" : (imaginary + "i"));
    }

}
