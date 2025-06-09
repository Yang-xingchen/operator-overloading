package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.Overloading;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) throws Exception {
        testVar();
        testNumber();
        testBase();
        testCast();
        testComplex();
    }

    private static void testVar() throws Exception {
        System.out.println("---- var ---");
        VarService service = Overloading.create(VarService.class);
        service.setVar(new BigDecimal("2"));
        System.out.println(service.staticVar());
        System.out.println(service.fieldVar());
        System.out.println(service.ignoreThis());
        System.out.println(service.statementVar());
        System.out.println(service.paramVar(new BigDecimal("4")));
        System.out.println(service.var());
        System.out.println(service.var(new BigDecimal(1)));
    }

    private static void testNumber() throws Exception {
        System.out.println("---- number ---");
        NumberService service = Overloading.create(NumberService.class);
        System.out.println(service.primitive());
        System.out.println(service.ignoreInteger());
        System.out.println(service.e());
    }

    private static void testBase() throws Exception {
        System.out.println("---- base ---");
        BaseService service = Overloading.create(BaseService.class);
        System.out.println(service.add());
        System.out.println(service.add(new BigDecimal(1), new BigDecimal(2)));
        System.out.println(service.multiply(new BigDecimal(2)));
        System.out.println(service.big());
        System.out.println(service.parenthesis());
        System.out.println(service.custom(1, 2));
    }

    private static void testCast() throws Exception {
        System.out.println("---- cast ---");
        CastService service = Overloading.create(CastService.class);
        System.out.println(service.notCast());
        System.out.println(service.bdToInt());
        System.out.println(service.intToBd(1));
        System.out.println(service.multiCast("1"));
        System.out.println(service.add(new BigDecimal(1), 2));
        System.out.println(service.complex(new BigDecimal(1), 2));
    }

    private static void testComplex() throws Exception {
        System.out.println("---- complex ---");
        ComplexService service = Overloading.create(ComplexService.class);
        System.out.println(service.rotation(new Complex(2, 1)));
        System.out.println(service.rotation(new Complex2(2, 1)));
        System.out.println(service.add(new Complex(1, 1), new Complex(1, 0), new Complex(0, 1)));
        System.out.println(service.add(new Complex2(1, 1), new Complex2(1, 0), new Complex2(0, 1)));
        System.out.println(service.cast(new Complex(-1, 1)));
        System.out.println(service.cast1(new Complex(0, 1), new Complex2(1, 0)));
        System.out.println(service.cast2(new Complex(0, 1), new Complex2(1, 0)));
    }

}