package org.yangxc.core.parse;

import org.yangxc.core.ast.tree.Token;
import org.yangxc.core.exception.ParseException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultParse implements Parse {

    private final String expression;
    private int point;
    private static final Set<Character> BLACK = Stream.of(
            ' ', '\t', '\n', '\r'
    ).collect(Collectors.toSet());
    private static final Map<Character, Token> SIGNAL_TOKEN = Map.of(
            '+', Token.PLUS,
            '-', Token.SUBTRACT,
            '*', Token.MULTIPLY,
            '/', Token.DIVIDE,
            '%', Token.REMAINDER,
            '.', Token.DOT,
            '(', Token.LEFT_PARENTHESIS,
            ')', Token.RIGHT_PARENTHESIS
    );

    public DefaultParse(String expression) {
        this.expression = expression;
        point = 0;
    }

    @Override
    public boolean hasNext() {
        while (true) {
            if (point >= expression.length()) {
                return false;
            }
            char charAt = expression.charAt(point);
            if (!BLACK.contains(charAt)) {
                return true;
            }
            point++;
        }
    }

    @Override
    public Token next() {
        while (true) {
            if (point >= expression.length()) {
                return null;
            }
            char charAt = expression.charAt(point);
            if (SIGNAL_TOKEN.containsKey(charAt)) {
                point++;
                return SIGNAL_TOKEN.get(charAt);
            }
            if (!BLACK.contains(charAt)) {
                break;
            }
            point++;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            char charAt = expression.charAt(point++);
            if (Character.isDigit(charAt) || Character.isAlphabetic(charAt)) {
                stringBuilder.append(charAt);
            } else {
                throw new ParseException(expression, point);
            }
            if (point == expression.length()) {
                return new Token(stringBuilder.toString());
            }
            char next = expression.charAt(point);
            if (BLACK.contains(next) || SIGNAL_TOKEN.containsKey(next)) {
                return new Token(stringBuilder.toString());
            }
        }
    }

}
