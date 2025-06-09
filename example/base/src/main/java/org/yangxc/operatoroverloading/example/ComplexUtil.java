package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.*;

@OperatorClass
public class ComplexUtil {

    @OperatorClassConst
    public static final Complex2 I = new Complex2(0, 1);

    @Cast
    public static Complex to(Complex2 complex) {
        return new Complex(complex.real(), complex.imaginary());
    }

    @Operator(OperatorType.ADD)
    public static Complex2 add(Complex2 a, Complex2 b) {
        return new Complex2(a.real() + b.real(), a.imaginary() + b.imaginary());
    }

    @Operator(OperatorType.SUBTRACT)
    public static Complex2 subtract(Complex2 a, Complex2 b) {
        return new Complex2(a.real() - b.real(), a.imaginary() - b.imaginary());
    }

    @Operator(OperatorType.MULTIPLY)
    public static Complex2 multiply(Complex2 a, Complex2 b) {
        double r = a.real() * b.real() - a.imaginary() * b.imaginary();
        double i = a.real() * b.imaginary() + a.imaginary() * b.real();
        return new Complex2(r, i);
    }

    @Operator(OperatorType.DIVIDE)
    public static Complex2 divide(Complex2 a, Complex2 b) {
        double denominator = b.real() * b.real() + b.imaginary() * b.imaginary();
        double r = (a.real() * b.real() + a.imaginary() * b.imaginary()) / denominator;
        double i = (a.imaginary() * b.real() - a.real() * b.imaginary()) / denominator;
        return new Complex2(r, i);
    }

}
