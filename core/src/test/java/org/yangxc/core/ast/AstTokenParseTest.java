package org.yangxc.core.ast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AstTokenParseTest {

    @Test
    public void test() {
        AstParse astParse = new AstParse.Build().build();

        Assertions.assertEquals("(1+2)", astParse.parse("1+2").toString());
        Assertions.assertEquals("((1+2)+3)", astParse.parse("1+2+3").toString());
        Assertions.assertEquals("(((1+2)-3)+4)", astParse.parse("1+2-3+4").toString());

        Assertions.assertEquals("((-1)+2)", astParse.parse("-1+2").toString());
        Assertions.assertEquals("(1+(-2))", astParse.parse("1+(-2)").toString());
        Assertions.assertEquals("(1-2)", astParse.parse("1-2").toString());
        Assertions.assertEquals("(1.5+2)", astParse.parse("1.5+2").toString());
        Assertions.assertEquals("(1.5+2.5)", astParse.parse("1.5+2.5").toString());

        Assertions.assertEquals("(1+(-2.3))", astParse.parse("1+(-2.3)").toString());
        Assertions.assertEquals("(1-2.3)", astParse.parse("1-2.3").toString());

        Assertions.assertEquals("(a+b)", astParse.parse("a+b").toString());
        Assertions.assertEquals("((aa+b1)+1)", astParse.parse("aa+b1+1").toString());

        Assertions.assertEquals("(1+(2*3))", astParse.parse("1+2*3").toString());
        Assertions.assertEquals("((1*2)-(3/4))", astParse.parse("1*2-3/4").toString());
    }

}