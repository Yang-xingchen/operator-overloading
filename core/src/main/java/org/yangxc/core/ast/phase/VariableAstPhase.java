package org.yangxc.core.ast.phase;

import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.ast.tree.NumberAst;
import org.yangxc.core.ast.tree.Token;
import org.yangxc.core.ast.tree.VariableAst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VariableAstPhase implements AstPhase {

    private static final VariableAstPhase INSTANCE = new VariableAstPhase();

    private VariableAstPhase() {

    }

    public static VariableAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public Result handle(List<Ast> tokens) {
        List<Integer> points = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof Token t && t.isVariable()) {
                points.add(i);
            }
        }
        if (points.isEmpty()) {
            return new Result(false, tokens);
        }
        Ast[] res = tokens.toArray(new Ast[0]);
        for (int i = 0; i < points.size(); i++) {
            int point = points.get(i);
            if (tokens.get(point) instanceof Token v) {
                res[point] = new VariableAst(v);
            }
        }
        return new Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
    }

}
