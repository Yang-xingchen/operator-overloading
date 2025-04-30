package org.yangxc.core.ast.tokenparse;

import org.yangxc.core.ast.tree.Token;

import java.util.ArrayList;
import java.util.List;

public interface TokenParse {

    boolean hasNext();

    Token next();

    default List<Token> tokens() {
        List<Token> tokens = new ArrayList<>();
        while (hasNext()) {
            tokens.add(next());
        }
        return tokens;
    }

}
