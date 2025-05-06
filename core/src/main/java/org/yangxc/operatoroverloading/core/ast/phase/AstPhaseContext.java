package org.yangxc.operatoroverloading.core.ast.phase;

import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.handle.service.SymbolContext;

import java.util.List;

public interface AstPhaseContext {

    SymbolContext getSymbolContext();

    Ast subParse(List<Ast> tokens);

}
