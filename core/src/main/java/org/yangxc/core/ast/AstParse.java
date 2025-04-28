package org.yangxc.core.ast;

import org.yangxc.core.ast.phase.*;
import org.yangxc.core.ast.tree.Ast;
import org.yangxc.core.parse.DefaultParse;
import org.yangxc.core.parse.Parse;

import java.util.List;
import java.util.function.Function;

public class AstParse {

    public static final AstParse DEFAULT = new Build().build();
    private final Function<String, Parse> parseFactory;
    private final List<AstPhase> phases;

    private AstParse(Function<String, Parse> parseFactory, List<AstPhase> phases) {
        this.parseFactory = parseFactory;
        this.phases = phases;
    }

    public static class Build {

        private Function<String, Parse> parseFactory = DefaultParse::new;
        private List<AstPhase> phases = List.of(
                NumberAstPhase.getInstance(),
                VariableAstPhase.getInstance(),
                MultiplyAstPhase.getInstance(),
                AddAstPhase.getInstance()
        );

        public Build setParseFactory(Function<String, Parse> parseFactory) {
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
        return parse(parseFactory.apply(expression));
    }

    public Ast parse(Parse parse) {
        List<Ast> tokens = parse.tokens().stream().map(Ast.class::cast).toList();
        for (AstPhase phase : phases) {
            while (true) {
                AstPhase.Result result = phase.handle(tokens);
                if (!result.handle()) {
                    break;
                }
                tokens = result.tokens();
            }
        }
        if (tokens.size() != 1) {
            throw new RuntimeException();
        }
        return tokens.get(0);
    }

}
