package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.OperatorFunction;
import org.yangxc.operatoroverloading.core.annotation.OperatorService;

@OperatorService(imports = {Complex.class, Complex2.class})
public interface ComplexService {

    @OperatorFunction("a+b+c")
    Complex add(Complex a, Complex b, Complex c);

    @OperatorFunction("a+b+c")
    Complex2 add(Complex2 a, Complex2 b, Complex2 c);

    @OperatorFunction("a+(Complex)1")
    Complex cast(Complex a);

    @OperatorFunction("a+(Complex)b")
    Complex cast1(Complex a, Complex2 b);

    @OperatorFunction("(Complex2)a+b")
    Complex2 cast2(Complex a, Complex2 b);

}
