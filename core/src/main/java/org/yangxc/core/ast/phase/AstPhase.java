package org.yangxc.core.ast.phase;

import org.yangxc.core.ast.tree.Ast;

import java.util.List;

public interface AstPhase {

    Result handle(List<Ast> tokens, AstPhaseContext context);

    record Result(boolean handle, List<Ast> tokens) {

    }

}
