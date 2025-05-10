package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.annotation.OperatorClassConst;
import org.yangxc.operatoroverloading.core.annotation.ServiceFunction;
import org.yangxc.operatoroverloading.core.annotation.OperatorService;

@OperatorService(imports = {Complex.class, Complex2.class, ComplexUtil.class, ComplexService.class})
public interface ComplexService {

    @OperatorClassConst
    Complex2 ONE = new Complex2(1, 0);

    @ServiceFunction("a*Complex.I")
    Complex rotation(Complex a);

    @ServiceFunction("a*ComplexService.ONE")
    Complex2 rotation(Complex2 a);

    @ServiceFunction("a+b+c")
    Complex add(Complex a, Complex b, Complex c);

    @ServiceFunction("a+b+c")
    Complex2 add(Complex2 a, Complex2 b, Complex2 c);

    @ServiceFunction(value = "a+(Complex)1", numberType = NumberType.BIG_DECIMAL)
    Complex cast(Complex a);

    @ServiceFunction("a+(Complex)b")
    Complex cast1(Complex a, Complex2 b);

    @ServiceFunction("(Complex2)a+b")
    Complex2 cast2(Complex a, Complex2 b);

}
