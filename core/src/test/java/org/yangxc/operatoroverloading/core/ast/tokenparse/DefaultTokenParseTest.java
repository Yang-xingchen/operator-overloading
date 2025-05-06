package org.yangxc.operatoroverloading.core.ast.tokenparse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yangxc.operatoroverloading.core.ast.tree.Token;

import java.util.List;

class DefaultTokenParseTest {

    @Test
    public void parse() {
        DefaultTokenParse parse = new DefaultTokenParse("  ");
        Assertions.assertIterableEquals(List.of(), parse.tokens());
        parse = new DefaultTokenParse("1+2");
        Assertions.assertIterableEquals(List.of(new Token("1"), Token.PLUS, new Token("2")), parse.tokens());
        parse = new DefaultTokenParse(" 1 +2 ");
        Assertions.assertIterableEquals(List.of(new Token("1"), Token.PLUS, new Token("2")), parse.tokens());
        parse = new DefaultTokenParse(" 1 +23 ");
        Assertions.assertIterableEquals(List.of(new Token("1"), Token.PLUS, new Token("23")), parse.tokens());
        parse = new DefaultTokenParse("a+ 1 ");
        Assertions.assertIterableEquals(List.of(new Token("a"), Token.PLUS, new Token("1")), parse.tokens());
        parse = new DefaultTokenParse("a1+ 1 ");
        Assertions.assertIterableEquals(List.of(new Token("a1"), Token.PLUS, new Token("1")), parse.tokens());
        parse = new DefaultTokenParse("(a+ 1)*2");
        Assertions.assertIterableEquals(List.of(Token.LEFT_PARENTHESIS, new Token("a"), Token.PLUS, new Token("1"), Token.RIGHT_PARENTHESIS, Token.MULTIPLY, new Token("2")), parse.tokens());
    }

}