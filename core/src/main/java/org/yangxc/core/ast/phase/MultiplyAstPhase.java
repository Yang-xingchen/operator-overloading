package org.yangxc.core.ast.phase;

import org.yangxc.core.ast.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MultiplyAstPhase implements AstPhase {

    private static final MultiplyAstPhase INSTANCE = new MultiplyAstPhase();

    private MultiplyAstPhase() {

    }

    public static MultiplyAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public AstPhase.Result handle(List<Ast> tokens) {
        List<Integer> points = new ArrayList<>();
        for (int i = 1; i < tokens.size() - 1; i++) {
            if (tokens.get(i) == Token.MULTIPLY) {
                points.add(i);
            } else if (tokens.get(i) == Token.DIVIDE) {
                points.add(i);
            } else if (tokens.get(i) == Token.REMAINDER) {
                points.add(i);
            }
        }
        if (points.isEmpty()) {
            return new AstPhase.Result(false, tokens);
        }
        Ast[] res = tokens.toArray(new Ast[0]);
        for (int point : points) {
            if (res[point - 1] == null || res[point + 1] == null) {
                continue;
            }
            if (tokens.get(point) == Token.MULTIPLY) {
                res[point + 1] = new MultiplyAst(res[point - 1], res[point + 1]);
            } else if (tokens.get(point) == Token.DIVIDE) {
                res[point + 1] = new DivideAst(res[point - 1], res[point + 1]);
            } else if (tokens.get(point) == Token.REMAINDER) {
                res[point + 1] = new RemainderAst(res[point - 1], res[point + 1]);
            }
            res[point - 1] = null;
            res[point] = null;
        }
        return new AstPhase.Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
    }

}
