package org.yangxc.core.ast.phase;

import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.ast.tree.PlusAst;
import org.yangxc.core.ast.tree.SubtractAst;
import org.yangxc.core.ast.tree.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlusAstPhase implements AstPhase {

    private static final PlusAstPhase INSTANCE = new PlusAstPhase();

    private PlusAstPhase() {

    }

    public static PlusAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public AstPhase.Result handle(List<Ast> tokens) {
        List<Integer> points = new ArrayList<>();
        for (int i = 1; i < tokens.size() - 1; i++) {
            if (tokens.get(i) == Token.PLUS) {
                points.add(i);
            } else if (tokens.get(i) == Token.SUBTRACT) {
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
            if (tokens.get(point) == Token.PLUS) {
                res[point + 1] = new PlusAst(res[point - 1], res[point + 1]);
            } else if (tokens.get(point) == Token.SUBTRACT) {
                res[point + 1] = new SubtractAst(res[point - 1], res[point + 1]);
            }
            res[point - 1] = null;
            res[point] = null;
        }
        return new AstPhase.Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
    }

}
