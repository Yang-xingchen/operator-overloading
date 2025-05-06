package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.Overloading;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) throws Exception {
        BaseService service = Overloading.get(BaseService.class);
        System.out.println(service.add());
        System.out.println(service.add(new BigDecimal(1), new BigDecimal(2)));
        System.out.println(service.multiply(new BigDecimal(2)));
        System.out.println(service.var());
        System.out.println(service.big());
        System.out.println(service.castAdd(new BigDecimal(1), 2));
        System.out.println(service.castAdd1(new BigDecimal(1), 2));
        System.out.println(service.parenthesis());
        System.out.println(service.primitive());
        System.out.println(service.ignoreInteger());
        System.out.println(service.e());
        System.out.println(service.custom(1, 2));
    }

}