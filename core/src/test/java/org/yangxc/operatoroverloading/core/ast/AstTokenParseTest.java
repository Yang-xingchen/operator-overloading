package org.yangxc.operatoroverloading.core.ast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.ast.tree.NumberAst;
import org.yangxc.operatoroverloading.core.constant.ClassName;
import org.yangxc.operatoroverloading.core.constant.VariableDefineType;
import org.yangxc.operatoroverloading.core.handle.service.SymbolContext;
import org.yangxc.operatoroverloading.core.handle.service.VariableContext;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;

import java.util.Arrays;
import java.util.List;

class AstTokenParseTest {

    @Test
    public void number() {
        AstParse astParse = AstParse.DEFAULT;

        Assertions.assertEquals("123", ((NumberAst)astParse.parse("1_23")).value(), "1_23");
        Assertions.assertEquals("123.45", ((NumberAst)astParse.parse("1_23.4_5")).value(), "1_23.4_5");
        Assertions.assertEquals("0.45", ((NumberAst)astParse.parse(".4_5")).value(), ".4_5");

        Assertions.assertEquals("1", astParse.parse("1").toString(), "1");
        Assertions.assertEquals("1.23", astParse.parse("1.23").toString(), "1.23");
        Assertions.assertEquals("0.23", astParse.parse(".23").toString(), ".23");

        Assertions.assertEquals("1", astParse.parse("+1").toString(), "+1");
        Assertions.assertEquals("1.23", astParse.parse("+1.23").toString(), "+1.23");
        Assertions.assertEquals("0.23", astParse.parse("+.23").toString(), "+.23");

        Assertions.assertEquals("(-1)", astParse.parse("-1").toString(), "-1");
        Assertions.assertEquals("(-1.23)", astParse.parse("-1.23").toString(), "-1.23");
        Assertions.assertEquals("(-0.23)", astParse.parse("-.23").toString(), "-.23");

        Assertions.assertEquals("1", astParse.parse("(1)").toString(), "(1)");
        Assertions.assertEquals("1.23", astParse.parse("(1.23)").toString(), "(1.23)");
        Assertions.assertEquals("0.23", astParse.parse("(.23)").toString(), "(.23)");

        Assertions.assertEquals("1", astParse.parse("(+1)").toString(), "(+1)");
        Assertions.assertEquals("1.23", astParse.parse("(+1.23)").toString(), "(+1.23)");
        Assertions.assertEquals("0.23", astParse.parse("(+.23)").toString(), "(+.23)");

        Assertions.assertEquals("(-1)", astParse.parse("(-1)").toString(), "(-1)");
        Assertions.assertEquals("(-1.23)", astParse.parse("(-1.23)").toString(), "(-1.23)");
        Assertions.assertEquals("(-0.23)", astParse.parse("(-.23)").toString(), "(-.23)");


        Assertions.assertEquals("1e10", astParse.parse("1E10").toString(), "1E10");
        Assertions.assertEquals("1.23e10", astParse.parse("1.23E10").toString(), "1.23E10");
        Assertions.assertEquals("0.23e10", astParse.parse(".23E10").toString(), ".23E10");

        Assertions.assertEquals("1e10", astParse.parse("+1E10").toString(), "+1E10");
        Assertions.assertEquals("1.23e10", astParse.parse("+1.23E10").toString(), "+1.23E10");
        Assertions.assertEquals("0.23e10", astParse.parse("+.23E10").toString(), "+.23E10");

        Assertions.assertEquals("(-1e10)", astParse.parse("-1E10").toString(), "-1E10");
        Assertions.assertEquals("(-1.23e10)", astParse.parse("-1.23E10").toString(), "-1.23E10");
        Assertions.assertEquals("(-0.23e10)", astParse.parse("-.23E10").toString(), "-.23E10");

        Assertions.assertEquals("1e10", astParse.parse("(1E10)").toString(), "(1E10)");
        Assertions.assertEquals("1.23e10", astParse.parse("(1.23E10)").toString(), "(1.23E10)");
        Assertions.assertEquals("0.23e10", astParse.parse("(.23E10)").toString(), "(.23E10)");

        Assertions.assertEquals("1e10", astParse.parse("(+1E10)").toString(), "(+1E10)");
        Assertions.assertEquals("1.23e10", astParse.parse("(+1.23E10)").toString(), "(+1.23E10)");
        Assertions.assertEquals("0.23e10", astParse.parse("(+.23E10)").toString(), "(+.23E10)");

        Assertions.assertEquals("(-1e10)", astParse.parse("(-1E10)").toString(), "(-1E10)");
        Assertions.assertEquals("(-1.23e10)", astParse.parse("(-1.23E10)").toString(), "(-1.23E10)");
        Assertions.assertEquals("(-0.23e10)", astParse.parse("(-.23E10)").toString(), "(-.23E10)");
    }

    @Test
    public void base() {
        AstParse astParse = AstParse.DEFAULT;

        Assertions.assertEquals("(1+2)", astParse.parse("1+2").toString());
        Assertions.assertEquals("((1+2)+3)", astParse.parse("1+2+3").toString());
        Assertions.assertEquals("(((1+2)-3)+4)", astParse.parse("1+2-3+4").toString());

        Assertions.assertEquals("((-1)+2)", astParse.parse("-1+2").toString(), "-1+2");
        Assertions.assertEquals("(1+(-2))", astParse.parse("1+(-2)").toString(), "1+(-2)");
        Assertions.assertEquals("(1-2)", astParse.parse("1-2").toString(), "1-2");
        Assertions.assertEquals("(1.5+2)", astParse.parse("1.5+2").toString(), "1.5+2");
        Assertions.assertEquals("(1.5+2.5)", astParse.parse("1.5+2.5").toString(), "1.5+2.5");

        Assertions.assertEquals("(1+(-2.3))", astParse.parse("1+(-2.3)").toString(), "1+(-2.3)");
        Assertions.assertEquals("(1-2.3)", astParse.parse("1-2.3").toString(), "1-2.3");

        Assertions.assertEquals("(a+b)", astParse.parse("a+b", new SymbolContext(getVarContext("a", "b"), List.of())).toString(), "a+b");
        Assertions.assertEquals("((aa+b1)+1)", astParse.parse("aa+b1+1", new SymbolContext(getVarContext("aa", "b1"), List.of())).toString(), "aa+b1+1");

        Assertions.assertEquals("(1+(2*3))", astParse.parse("1+2*3").toString(), "1+2*3");
        Assertions.assertEquals("((1*2)-(3/4))", astParse.parse("1*2-3/4").toString(), "1*2-3/4");

        Assertions.assertEquals("(((-1.23e2)+0.45)+0.67e-4)", astParse.parse("-1.23E2+.45+.67e-4").toString(), "-1.23E2+.45+.67e-4");
    }

    private VariableSetContext getVarContext(String... vars) {
        VariableSetContext context = new VariableSetContext();
        for (String var : vars) {
            context.add(VariableContext.createByParam(ClassName.BIG_DECIMAL, var));
        }
        return context;
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