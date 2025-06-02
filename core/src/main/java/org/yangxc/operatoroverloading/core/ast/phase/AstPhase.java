package org.yangxc.operatoroverloading.core.ast.phase;

import org.yangxc.operatoroverloading.core.ast.tree.Ast;

import java.util.List;

public interface AstPhase {

    Result handle(List<Ast> tokens, AstPhaseContext context);

    class Result {
        public final boolean handle;
        public final List<Ast> tokens;

        public Result(boolean handle, List<Ast> tokens) {
            this.handle = handle;
            this.tokens = tokens;
        }

        public boolean isHandle() {
            return handle;
        }

        public List<Ast> getTokens() {
            return tokens;
        }

    }

}
