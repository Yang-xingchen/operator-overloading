package org.yangxc.operatoroverloading.example;

import org.yangxc.operatoroverloading.core.annotation.DocType;
import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.annotation.OperatorService;
import org.yangxc.operatoroverloading.core.annotation.ServiceFunction;

import java.math.BigDecimal;

@OperatorService
public interface NumberService {

    @ServiceFunction(value = "1.23+4e2*.1", numberType = NumberType.PRIMITIVE, doc = DocType.DOC)
    double primitive();

    @ServiceFunction(".23+.0045")
    BigDecimal ignoreInteger();

    @ServiceFunction("-12.34e2-.56-.78e-2")
    BigDecimal e();

}
