package org.yangxc.core.parse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yangxc.core.ast.tree.Token;

import java.util.List;

class DefaultParseTest {

    @Test
    public void parse() {
        DefaultParse parse = new DefaultParse("  ");
        Assertions.assertIterableEquals(List.of(), parse.tokens());
        parse = new DefaultParse("1+2");
        Assertions.assertIterableEquals(List.of(new Token("1"), Token.PLUS, new Token("2")), parse.tokens());
        parse = new DefaultParse(" 1 +2 ");
        Assertions.assertIterableEquals(List.of(new Token("1"), Token.PLUS, new Token("2")), parse.tokens());
        parse = new DefaultParse(" 1 +23 ");
        Assertions.assertIterableEquals(List.of(new Token("1"), Token.PLUS, new Token("23")), parse.tokens());
        parse = new DefaultParse("a+ 1 ");
        Assertions.assertIterableEquals(List.of(new Token("a"), Token.PLUS, new Token("1")), parse.tokens());
        parse = new DefaultParse("a1+ 1 ");
        Assertions.assertIterableEquals(List.of(new Token("a1"), Token.PLUS, new Token("1")), parse.tokens());
        parse = new DefaultParse("(a+ 1)*2");
        Assertions.assertIterableEquals(List.of(Token.LEFT_PARENTHESIS, new Token("a"), Token.PLUS, new Token("1"), Token.RIGHT_PARENTHESIS, Token.MULTIPLY, new Token("2")), parse.tokens());
    }

}