package org.yangxc.operatoroverloading.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MainApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    public CommandLineRunner testVar(VarService service) {
        return arg -> {
            System.out.println("---- var ---");
            service.setVar(new BigDecimal("2"));
            System.out.println(service.staticVar());
            System.out.println(service.fieldVar());
            System.out.println(service.ignoreThis());
            System.out.println(service.statementVar());
            System.out.println(service.paramVar(new BigDecimal("4")));
            System.out.println(service.var());
            System.out.println(service.var(new BigDecimal(1)));
        };
    }

    @Bean
    public CommandLineRunner testNumber(NumberService service) {
        return arg -> {
            System.out.println("---- number ---");
            System.out.println(service.primitive());
            System.out.println(service.ignoreInteger());
            System.out.println(service.e());
        };
    }

    @Bean
    public CommandLineRunner testBase(BaseService service) {
        return arg -> {
            System.out.println("---- base ---");
            System.out.println(service.add());
            System.out.println(service.add(new BigDecimal(1), new BigDecimal(2)));
            System.out.println(service.multiply(new BigDecimal(2)));
            System.out.println(service.big());
            System.out.println(service.parenthesis());
            System.out.println(service.custom(1, 2));
        };
    }

    @Bean
    public CommandLineRunner testCast(CastService service) {
        return arg -> {
            System.out.println("---- cast ---");
            System.out.println(service.notCast());
            System.out.println(service.bdToInt());
            System.out.println(service.intToBd(1));
            System.out.println(service.multiCast("1"));
            System.out.println(service.add(new BigDecimal(1), 2));
            System.out.println(service.complex(new BigDecimal(1), 2));
        };
    }

    @Bean
    public CommandLineRunner testComplex(ComplexService service) {
        return arg -> {
            System.out.println("---- complex ---");
            System.out.println(service.rotation(new Complex(2, 1)));
            System.out.println(service.rotation(new Complex2(2, 1)));
            System.out.println(service.add(new Complex(1, 1), new Complex(1, 0), new Complex(0, 1)));
            System.out.println(service.add(new Complex2(1, 1), new Complex2(1, 0), new Complex2(0, 1)));
            System.out.println(service.cast(new Complex(-1, 1)));
            System.out.println(service.cast1(new Complex(0, 1), new Complex2(1, 0)));
            System.out.println(service.cast2(new Complex(0, 1), new Complex2(1, 0)));
        };
    }

}