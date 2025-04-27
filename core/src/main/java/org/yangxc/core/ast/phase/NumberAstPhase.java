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
        for (int i = 0; i < points.size(); i++) {
            int point = points.get(i);
            if (res[point] == null) {
                continue;
            }
            Token sign = null;
            if (point == 1 && tokens.get(0) instanceof Token s) {
                if (s == Token.SUBTRACT) {
                    sign = s;
                    res[0] = null;
                }
                if (s == Token.PLUS) {
                    res[0] = null;
                }
            }
            Token number = (Token) tokens.get(point);
            Token decimal = null;
            if (point < tokens.size() - 1 && tokens.get(point + 1) instanceof Token dot) {
                if (dot == Token.DOT) {
                    if (point < tokens.size() - 2 && tokens.get(point + 2) instanceof Token d) {
                        if (d.isDigit()) {
                            decimal = d;
                            res[point + 1] = null;
                            res[point + 2] = null;
                        }
                    }
                }
            }
            res[point] = new NumberAst(sign, number, decimal);
        }
        return new Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
    }

}
