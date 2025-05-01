package org.yangxc.core.ast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.handle.service.SymbolContext;
import org.yangxc.core.handle.service.VariableContext;

import java.util.Arrays;
import java.util.List;

class AstTokenParseTest {

    @Test
    public void base() {
        AstParse astParse = AstParse.DEFAULT;

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

        Assertions.assertEquals("(a+b)", astParse.parse("a+b", new SymbolContext(getVarContext("a", "b"), List.of())).toString());
        Assertions.assertEquals("((aa+b1)+1)", astParse.parse("aa+b1+1", new SymbolContext(getVarContext("aa", "b1"), List.of())).toString());

        Assertions.assertEquals("(1+(2*3))", astParse.parse("1+2*3").toString());
        Assertions.assertEquals("((1*2)-(3/4))", astParse.parse("1*2-3/4").toString());
    }

    private List<VariableContext> getVarContext(String... vars) {
        return Arrays.stream(vars).map(var -> new VariableContext(var, ClassName.BIG_DECIMAL, -1)).toList();
    }

    @Test
    public void cast() {
        AstParse astParse = AstParse.DEFAULT;
        Assertions.assertEquals("(int)1", astParse.parse("(int)1").toString());
        Assertions.assertEquals("(int)(long)1", astParse.parse("(int)(long)1").toString());
        Assertions.assertEquals("(int)(1+1)", astParse.parse("(int)(1+1)").toString());
    }

    @Test
    public void parenthesis() {
        AstParse astParse = AstParse.DEFAULT;
        Assertions.assertEquals("((1+2)*3)", astParse.parse("(1+2)*3").toString());
        Assertions.assertEquals("((1*(2+3))/4)", astParse.parse("1*(2+3)/4").toString());
    }

}