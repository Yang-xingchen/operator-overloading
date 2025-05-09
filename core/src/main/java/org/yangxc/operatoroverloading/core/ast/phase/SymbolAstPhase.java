package org.yangxc.operatoroverloading.core.ast.phase;

import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.ast.tree.Token;
import org.yangxc.operatoroverloading.core.ast.tree.TypeAst;
import org.yangxc.operatoroverloading.core.ast.tree.VariableAst;
import org.yangxc.operatoroverloading.core.exception.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SymbolAstPhase implements AstPhase {

    private static final SymbolAstPhase INSTANCE = new SymbolAstPhase();

    private SymbolAstPhase() {

    }

    public static SymbolAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public Result handle(List<Ast> tokens, AstPhaseContext context) {
        List<Integer> points = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof Token t && t.isSymbol()) {
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
            if (res[point] instanceof Token v) {
                List<Token> typeTokens = new ArrayList<>();
                typeTokens.add(v);
                int end = point;
                while (end + 2 < tokens.size()) {
                    if (tokens.get(end + 1) == Token.DOT && tokens.get(end + 2) instanceof Token token) {
                        res[end + 1] = null;
                        res[end + 2] = null;
                        typeTokens.add(token);
                        end += 2;
                    } else {
                        break;
                    }
                }
                if (typeTokens.size() > 1) {
                    String typeName = typeTokens.stream().limit(typeTokens.size() - 1).map(Token::getValue).collect(Collectors.joining("."));
                    typeName = context.getSymbolContext().getType(typeName);
                    String name = typeTokens.getLast().getValue();
                    if (context.getSymbolContext().isVar(typeName, name)) {
                        res[point] = new VariableAst(typeTokens, typeName + "." + name);
                        continue;
                    }
                    res[point] = new TypeAst(typeTokens, typeTokens.stream().map(Token::getValue).collect(Collectors.joining(".")));
                    continue;
                }
                if (context.getSymbolContext().isVar(v.getValue())) {
                    res[point] = new VariableAst(v);
                } else if (context.getSymbolContext().isVar("this." + v.getValue())) {
                    res[point] = new VariableAst(List.of(v), "this." + v.getValue());
                } else if (context.getSymbolContext().isType(v.getValue())) {
                    res[point] = new TypeAst(List.of(v), context.getSymbolContext().getType(v.getValue()));
                } else {
                    throw new IllegalArgumentException("unknown symbol: " + v.getValue());
                }
            }
        }
        return new Result(true, Arrays.stream(res).filter(Objects::nonNull).toList());
    }

}
