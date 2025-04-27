package org.yangxc.example;

import org.yangxc.core.Overloading;

public class Main {

    public static void main(String[] args) throws Exception {
        BaseService service = Overloading.get(BaseService.class);
        System.out.println(service.add());
        System.out.println(service.add(1, 2));
    }

}