package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.Cast;

public record Complex2(double real, double imaginary) {

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
