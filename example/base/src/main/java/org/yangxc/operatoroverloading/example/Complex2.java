package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.Cast;

import java.util.Objects;

public final class Complex2 {

    private final double real;
    private final double imaginary;

    public Complex2(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
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

    public double real() {
        return real;
    }

    public double imaginary() {
        return imaginary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Complex2 that = (Complex2) obj;
        return Double.doubleToLongBits(this.real) == Double.doubleToLongBits(that.real) &&
                Double.doubleToLongBits(this.imaginary) == Double.doubleToLongBits(that.imaginary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }


}
