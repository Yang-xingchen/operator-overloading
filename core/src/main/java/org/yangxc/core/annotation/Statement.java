package org.yangxc.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Statement {

    Class<?> type();

    String varName();

    String exp();

}
