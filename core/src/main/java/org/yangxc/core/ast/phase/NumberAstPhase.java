package org.yangxc.core.ast.phase;

import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.ast.tree.NumberAst;
import org.yangxc.core.ast.tree.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NumberAstPhase implements AstPhase {

    private static final NumberAstPhase INSTANCE = new NumberAstPhase();

    private NumberAstPhase() {

    }

    public static NumberAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public Result handle(List<Ast> tokens) {
        List<Integer> points = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof Token t && t.isDigit()) {
                points.add(i);
            }
        }
        if (points.isEmpty()) {
            return new Result(false, tokens);
        }
        Ast[] res = tokens.toArray(new Ast[0]);
        for (int point : points) {
            handel(tokens, point, res);
        }
        return new Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
    }

    private void handel(List<Ast> tokens, int point, Ast[] res) {
        if (res[point] == null) {
            return;
        }
        // p: pre, n: next
        Token p2 = null, p1 = null, n1 = null, n2 = null, n3 = null;
        if (point > 0 && tokens.get(point - 1) instanceof Token) {
            p1 = (Token) tokens.get(point - 1);
            if (point > 1 && tokens.get(point - 2) instanceof Token) {
                p2 = (Token) tokens.get(point - 2);
            }
        }
        if (point + 1 < tokens.size() && tokens.get(point + 1) instanceof Token) {
            n1 = (Token) tokens.get(point + 1);
            if (point + 2 < tokens.size() && tokens.get(point + 2) instanceof Token) {
                n2 = (Token) tokens.get(point + 2);
                if (point + 3 < tokens.size() && tokens.get(point + 3) instanceof Token) {
                    n3 = (Token) tokens.get(point + 3);
                }
            }
        }

        // (n)
        Token integer = (Token) tokens.get(point);
        if (p1 == Token.LEFT_PARENTHESIS && n1 == Token.RIGHT_PARENTHESIS) {
            res[point - 1] = null;
            res[point + 1] = null;
            res[point] = new NumberAst(null, integer, null);
            return;
        }
        // (n.m)
        if (p1 == Token.LEFT_PARENTHESIS && n1 == Token.DOT && n2 != null && n2.isDigit() && n3 == Token.RIGHT_PARENTHESIS) {
            res[point - 1] = null;
            res[point + 1] = null;
            res[point + 2] = null;
            res[point + 3] = null;
            res[point] = new NumberAst(null, integer, n2);
            return;
        }
        // (-n)
        if (p2 == Token.LEFT_PARENTHESIS && p1 == Token.SUBTRACT && n1 == Token.RIGHT_PARENTHESIS) {
            res[point - 2] = null;
            res[point - 1] = null;
            res[point + 1] = null;
            res[point] = new NumberAst(Token.SUBTRACT, integer, n2);
            return;
        }
        // (-n.m)
        if (p2 == Token.LEFT_PARENTHESIS && p1 == Token.SUBTRACT && n1 == Token.DOT && n2 != null && n2.isDigit() && n3 == Token.RIGHT_PARENTHESIS) {
            res[point - 2] = null;
            res[point - 1] = null;
            res[point + 1] = null;
            res[point + 2] = null;
            res[point + 3] = null;
            res[point] = new NumberAst(Token.SUBTRACT, integer, n2);
            return;
        }
        // ^-n | ^-n.m
        if (point == 1 && p1 == Token.SUBTRACT) {
            res[point - 1] = null;
            if (n1 == Token.DOT && n2 != null && n2.isDigit()) {
                res[point + 1] = null;
                res[point + 2] = null;
                res[point] = new NumberAst(Token.SUBTRACT, integer, n2);
                return;
            }
            res[point] = new NumberAst(Token.SUBTRACT, integer, null);
            return;
        }
        // (+n)
        if (p2 == Token.LEFT_PARENTHESIS && p1 == Token.PLUS && n1 == Token.RIGHT_PARENTHESIS) {
            res[point - 2] = null;
            res[point - 1] = null;
            res[point + 1] = null;
            res[point] = new NumberAst(null, integer, n2);
            return;
        }
        // (+n.m)
        if (p2 == Token.LEFT_PARENTHESIS && p1 == Token.PLUS && n1 == Token.DOT && n2 != null && n2.isDigit() && n3 == Token.RIGHT_PARENTHESIS) {
            res[point - 2] = null;
            res[point - 1] = null;
            res[point + 1] = null;
            res[point + 2] = null;
            res[point + 3] = null;
            res[point] = new NumberAst(null, integer, n2);
            return;
        }
        // ^+n | ^+n.m
        if (point == 1 && p1 == Token.PLUS) {
            res[point - 1] = null;
            if (n1 == Token.DOT && n2 != null && n2.isDigit()) {
                res[point + 1] = null;
                res[point + 2] = null;
                res[point] = new NumberAst(Token.PLUS, integer, n2);
                return;
            }
            res[point] = new NumberAst(null, integer, null);
            return;
        }
        // n.m
        if (n1 == Token.DOT && n2 != null && n2.isDigit()) {
            res[point + 1] = null;
            res[point + 2] = null;
            res[point] = new NumberAst(null, integer, n2);
            return;
        }
        // n
        res[point] = new NumberAst(null, integer, null);
    }

}
