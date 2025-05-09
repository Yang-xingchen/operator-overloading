package org.yangxc.operatoroverloading.core.ast;

import org.yangxc.operatoroverloading.core.ast.phase.*;
import org.yangxc.operatoroverloading.core.ast.tokenparse.DefaultTokenParse;
import org.yangxc.operatoroverloading.core.ast.tokenparse.TokenParse;
import org.yangxc.operatoroverloading.core.ast.tree.Ast;
import org.yangxc.operatoroverloading.core.handle.service.SymbolContext;

import java.util.List;
import java.util.function.Function;

public class AstParse {

    public static final AstParse DEFAULT = new Build().build();
    private final Function<String, TokenParse> parseFactory;
    private final List<AstPhase> phases;

    private AstParse(Function<String, TokenParse> parseFactory, List<AstPhase> phases) {
        this.parseFactory = parseFactory;
        this.phases = phases;
    }

    public static class Build {

        private Function<String, TokenParse> parseFactory = DefaultTokenParse::new;
        private List<AstPhase> phases = List.of(
                NumberAstPhase.getInstance(),
                SymbolAstPhase.getInstance(),
                ParenthesisAstPhase.getInstance(),
                MultiplyAstPhase.getInstance(),
                AddAstPhase.getInstance()
        );

        public Build setParseFactory(Function<String, TokenParse> parseFactory) {
            this.parseFactory = parseFactory;
            return this;
        }

        public Build setPhases(List<AstPhase> phases) {
            this.phases = phases;
            return this;
        }

        public AstParse build() {
            return new AstParse(parseFactory, phases);
        }

    }

    public Ast parse(String expression) {
        return parse(parseFactory.apply(expression), new SymbolContext());
    }

    public Ast parse(String expression, SymbolContext symbolContext) {
        return parse(parseFactory.apply(expression), symbolContext);
    }

    public Ast parse(TokenParse tokenParse, SymbolContext symbolContext) {
        List<Ast> tokens = tokenParse.tokens().stream().map(Ast.class::cast).toList();
        AstPhaseContext context = new DefaultAstPhaseContext(symbolContext);
        return doParse(tokens, context);
    }

    private Ast doParse(List<Ast> tokens, AstPhaseContext context) {
        if (tokens.isEmpty()) {
            return null;
        }
        for (AstPhase phase : phases) {
            while (true) {
                AstPhase.Result result = phase.handle(tokens, context);
                if (!result.handle()) {
                    break;
                }
                tokens = result.tokens();
            }
        }
        if (tokens.size() != 1) {
            throw new RuntimeException("multi ast parse result: " + tokens);
        }
        return tokens.get(0);
    }

    public class DefaultAstPhaseContext implements AstPhaseContext {

        private final SymbolContext symbolContext;

        public DefaultAstPhaseContext(SymbolContext symbolContext) {
            this.symbolContext = symbolContext;
        }

        @Override
        public SymbolContext getSymbolContext() {
            return symbolContext;
        }

        @Override
        public Ast subParse(List<Ast> tokens) {
            return doParse(tokens, this);
        }

    }

}
