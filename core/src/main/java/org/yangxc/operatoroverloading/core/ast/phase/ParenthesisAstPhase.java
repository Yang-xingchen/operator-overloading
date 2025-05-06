package org.yangxc.operatoroverloading.core.ast.phase;

import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.ast.tree.CastAst;
import org.yangxc.operatoroverloading.core.ast.tree.Token;
import org.yangxc.operatoroverloading.core.ast.tree.TypeAst;

import java.util.*;

public class ParenthesisAstPhase implements AstPhase {

    private static final ParenthesisAstPhase INSTANCE = new ParenthesisAstPhase();

    private ParenthesisAstPhase() {

    }

    public static ParenthesisAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public Result handle(List<Ast> tokens, AstPhaseContext context) {
        LinkedList<Integer> stack = new LinkedList<>();
        Ast[] res = tokens.toArray(new Ast[0]);
        boolean handle = false;
        for (int i = tokens.size() - 1; i >=0 ; i--) {
            if (tokens.get(i) == Token.RIGHT_PARENTHESIS) {
                stack.addLast(i);
                continue;
            }
            if (tokens.get(i) == Token.LEFT_PARENTHESIS) {
                handle = true;
                Integer end = stack.removeLast();
                if (end == null) {
                    throw new IllegalArgumentException("miss match ')'");
                }
                // 只处理最内层
                if (!stack.isEmpty()) {
                    continue;
                }
                // 只有一个token
                if (end - 2 == i) {
                    res[end] = null;
                    if (res[i + 1] instanceof TypeAst cast && end + 1 < tokens.size()) {
                        res[i] = new CastAst(cast, res[end + 1]);
                        res[i + 1] = null;
                        res[end + 1] = null;
                    } else {
                        res[i] = res[i + 1];
                        res[i + 1] = null;
                    }
                    continue;
                }
                // 多个token
                List<Ast> subTokens = new ArrayList<>(tokens.subList(i + 1, end));
                for (int j = i + 1; j <= end; j++) {
                    res[j] = null;
                }
                res[i] = context.subParse(subTokens);
            }
        }
        if (!handle) {
            return new Result(false, tokens);
        }
        if (stack.isEmpty()) {
            return new Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
        }
        throw new IllegalArgumentException("miss match '('");
    }

}
