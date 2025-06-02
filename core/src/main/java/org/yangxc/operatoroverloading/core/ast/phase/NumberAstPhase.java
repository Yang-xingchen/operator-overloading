package org.yangxc.operatoroverloading.core.ast.phase;

import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.ast.tree.NumberAst;
import org.yangxc.operatoroverloading.core.ast.tree.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NumberAstPhase implements AstPhase {

    private static final NumberAstPhase INSTANCE = new NumberAstPhase();
    private static final Token ZERO = new Token("0");

    private NumberAstPhase() {

    }

    public static NumberAstPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public Result handle(List<Ast> tokens, AstPhaseContext context) {
        List<Integer> points = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof Token && ((Token) tokens.get(i)).isDigit()) {
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
        return new Result(true, Arrays.stream(res).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private boolean isSign(Token s) {
        return s == Token.SUBTRACT || s == Token.PLUS;
    }

    private Token getSign(Token s) {
        return s == Token.SUBTRACT ? Token.SUBTRACT : null;
    }

    private boolean isDigit(Token d) {
        return d != null && d.isDigit();
    }

    private boolean isDot(Token d) {
        return d == Token.DOT;
    }

    private boolean isE(Token e) {
        return e != null && ("e".equals(e.getValue()) || "E".equals(e.getValue()));
    }

    private boolean isStartSymbol(Token s) {
        return s == null || s == Token.LEFT_PARENTHESIS;
    }

    private void handel(List<Ast> tokens, int point, Ast[] res) {
        if (res[point] == null) {
            return;
        }
        // p: pre, n: next
        Token p3 = null, p2 = null, p1 = null, n1 = null, n2 = null, n3 = null, n4 = null, n5 = null;
        if (point > 0 && tokens.get(point - 1) instanceof Token) {
            p1 = (Token) tokens.get(point - 1);
            if (point > 1 && tokens.get(point - 2) instanceof Token) {
                p2 = (Token) tokens.get(point - 2);
                if (point > 2 && tokens.get(point - 3) instanceof Token) {
                    p3 = (Token) tokens.get(point - 3);
                }
            }
        }
        if (point + 1 < tokens.size() && tokens.get(point + 1) instanceof Token) {
            n1 = (Token) tokens.get(point + 1);
            if (point + 2 < tokens.size() && tokens.get(point + 2) instanceof Token) {
                n2 = (Token) tokens.get(point + 2);
                if (point + 3 < tokens.size() && tokens.get(point + 3) instanceof Token) {
                    n3 = (Token) tokens.get(point + 3);
                    if (point + 4 < tokens.size() && tokens.get(point + 4) instanceof Token) {
                        n4 = (Token) tokens.get(point + 4);
                        if (point + 5 < tokens.size() && tokens.get(point + 5) instanceof Token) {
                            n5 = (Token) tokens.get(point + 5);
                        }
                    }
                }
            }
        }

        // 注释说明:
        // +/-: 正负号
        // E: 指数符号
        // ^: 表达式开头
        // n: (数字)整数部分
        // m: (数字)小数部分
        // e: (数字)指数部分

        Token integer = (Token) tokens.get(point);
        // ^-n | ^+n | ^-n.m | ^+n.m
        // ^-n.mEe | ^+n.mEe | ^-n.mE-e | ^+n.mE-e | ^-n.mE+e | ^+n.mE+e
        // ^-nEe | ^+nEe | ^-nE-e | ^+nE+e | ^-nE+e | ^+nE-e
        if ((point == 1 || isStartSymbol(p2)) && isSign(p1)) {
            res[point - 1] = null;
            // ^-n.m | ^+n.m | ^-n.mEe | ^+n.mEe | ^-n.mE-e | ^+n.mE-e | ^-n.mE+e | ^+n.mE+e
            if (isDot(n1) && isDigit(n2)) {
                res[point + 1] = null;
                res[point + 2] = null;
                // ^-n.mEe | ^+n.mEe | ^-n.mE-e | ^+n.mE-e | ^-n.mE+e | ^+n.mE+e
                if (isE(n3)) {
                    res[point + 3] = null;
                    // ^-n.mEe | ^+n.mEe
                    if (isDigit(n4)) {
                        res[point + 4] = null;
                        res[point] = new NumberAst(getSign(p1), integer, n2, null, n4);
                        return;
                    }
                    // ^-n.mE-e | ^+n.mE-e | ^-n.mE+e | ^+n.mE+e
                    if (isSign(n4) && isDigit(n5)) {
                        res[point + 4] = null;
                        res[point + 5] = null;
                        res[point] = new NumberAst(getSign(p1), integer, n2, getSign(n4), n5);
                        return;
                    }
                }
                res[point] = new NumberAst(getSign(p1), integer, n2);
                return;
            }
            // ^-nEe | ^+nEe | ^-nE-e | ^+nE+e | ^-nE+e | ^+nE-e
            if (isE(n1)) {
                res[point + 1] = null;
                // ^-nEe | ^+nEe
                if (isDigit(n2)) {
                    res[point + 2] = null;
                    res[point] = new NumberAst(getSign(p1), integer, null, null, n2);
                    return;
                }
                // ^-nE-e | ^+nE+e | ^-nE+e | ^+nE-e
                if (isSign(n2) && isDigit(n3)) {
                    res[point + 2] = null;
                    res[point + 3] = null;
                    res[point] = new NumberAst(getSign(p1), integer, null, getSign(n2), n3);
                    return;
                }
            }
            res[point] = new NumberAst(getSign(p1), integer, null);
            return;
        }
        // ^-.m | ^+.m
        // ^-.mEe | ^+.mEe | ^-.mE-e | ^+.mE-e | ^-.mE+e | ^+.mE+e
        if ((point == 2 || isStartSymbol(p3)) && isDot(p1) && isSign(p2)) {
            res[point - 2] = null;
            res[point - 1] = null;
            if (isE(n1)) {
                res[point + 1] = null;
                // ^-.mEe | ^+.mEe
                if (isDigit(n2)) {
                    res[point + 2] = null;
                    res[point] = new NumberAst(getSign(p2), ZERO, integer, null, n2);
                    return;
                }
                // ^-.mE-e | ^+.mE-e | ^-.mE+e | ^+.mE+e
                if (isSign(n2) && isDigit(n3)) {
                    res[point + 2] = null;
                    res[point + 3] = null;
                    res[point] = new NumberAst(getSign(p2), ZERO, integer, getSign(n2), n3);
                    return;
                }
            }
            res[point] = new NumberAst(getSign(p2), ZERO, integer);
            return;
        }
        // n.m
        // n.mEe | n.mE+e | n.m-e
        if (isDot(n1) && isDigit(n2)) {
            res[point + 1] = null;
            res[point + 2] = null;
            // n.mEe | n.mE+e | n.m-e
            if (isE(n3)) {
                res[point + 3] = null;
                // n.mEe
                if (isDigit(n4)) {
                    res[point + 4] = null;
                    res[point] = new NumberAst(null, integer, n2, null, n4);
                    return;
                }
                // n.mE+e | n.m-e
                if (isSign(n4) && isDigit(n5)) {
                    res[point + 4] = null;
                    res[point + 5] = null;
                    res[point] = new NumberAst(null, integer, n2, getSign(n4), n5);
                    return;
                }
            }
            res[point] = new NumberAst(null, integer, n2);
            return;
        }
        // .m
        // .mEe | .mE-e | .m+e
        if (isDot(p1)) {
            res[point - 1] = null;
            // .mEe | .mE-e | .m+e
            if (isE(n1)) {
                res[point + 1] = null;
                // .mEe
                if (isDigit(n2)) {
                    res[point + 2] = null;
                    res[point] = new NumberAst(null, ZERO, integer, null, n2);
                    return;
                }
                // .mE-e | .m+e
                if (isSign(n2) && isDigit(n3)) {
                    res[point + 2] = null;
                    res[point + 3] = null;
                    res[point] = new NumberAst(null, ZERO, integer, getSign(n2), n3);
                    return;
                }
            }
            res[point] = new NumberAst(null, ZERO, integer);
            return;
        }
        // nEe | eE-e | eE+e
        if (isE(n1)) {
            res[point + 1] = null;
            if (isDigit(n2)) {
                res[point + 2] = null;
                res[point] = new NumberAst(null, integer, null, null, n2);
                return;
            }
            if (isSign(n2) && isDigit(n3)) {
                res[point + 2] = null;
                res[point + 3] = null;
                res[point] = new NumberAst(null, integer, null, getSign(n2), n3);
                return;
            }
        }
        // n
        res[point] = new NumberAst(null, integer, null);
    }

}
