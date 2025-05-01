package org.yangxc.core.ast.phase;

import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.handle.service.SymbolContext;

import java.util.List;

public interface AstPhaseContext {

    SymbolContext getSymbolContext();

    Ast subParse(List<Ast> tokens);

}
