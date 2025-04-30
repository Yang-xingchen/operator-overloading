package org.yangxc.example;

import org.yangxc.core.Overloading;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) throws Exception {
        BaseService service = Overloading.get(BaseService.class);
        System.out.println(service.add());
        System.out.println(service.add(new BigDecimal(1), new BigDecimal(2)));
        System.out.println(service.multiply(new BigDecimal(2)));
        System.out.println(service.var());
        System.out.println(service.big());
    }

}