package org.yangxc.core.context.service;

import org.yangxc.core.ast.AstVisitor;
import org.yangxc.core.ast.tree.*;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.context.overloading.ClassOverloadingContext;
import org.yangxc.core.context.overloading.OperatorOverloadingContext;
import org.yangxc.core.context.overloading.OverloadingContext;

import java.util.Map;

public class ExpVisitor implements AstVisitor<ExpVisitor.ExpContext, ExpVisitor.ExpResult> {

    public static final ExpVisitor INSTANCE = new ExpVisitor();

    private ExpVisitor() {
    }

    public static class ExpContext {

        private StringBuilder stringBuilder;
        private OverloadingContext overloadingContext;
        private Map<String, VariableContext> varMap;

        public ExpContext(StringBuilder stringBuilder, OverloadingContext overloadingContext, Map<String, VariableContext> varMap) {
            this.stringBuilder = stringBuilder;
            this.overloadingContext = overloadingContext;
            this.varMap = varMap;
        }

        public ExpContext(OverloadingContext overloadingContext, Map<String, VariableContext> varMap) {
            this(new StringBuilder(), overloadingContext, varMap);
        }

        public ExpContext append(String string) {
            stringBuilder.append(string);
            return this;
        }

        public ExpResult createResult(String type) {
            return new ExpResult(type, overloadingContext.get(type));
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }

    }

    public static class ExpResult {
        private final String type;
        private final ClassOverloadingContext overloadingContext;

        public ExpResult(String type, ClassOverloadingContext overloadingContext) {
            this.type = type;
            this.overloadingContext = overloadingContext;
        }

        public String getType() {
            return type;
        }

        public ClassOverloadingContext getOverloadingContext() {
            return overloadingContext;
        }

    }

    @Override
    public ExpResult visit(VariableAst ast, ExpContext expContext) {
        expContext.append(ast.toString());
        return expContext.createResult(expContext.varMap.get(ast.toString()).type());
    }

    @Override
    public ExpResult visit(NumberAst ast, ExpContext expContext) {
        expContext.append("new BigDecimal(\"").append(ast.toString()).append("\")");
        return expContext.createResult(ClassName.BIG_DECIMAL);
    }

    @Override
    public ExpResult visit(AddAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.getOverloadingContext().getAdd();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(SubtractAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.getOverloadingContext().getSubtract();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(MultiplyAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.getOverloadingContext().getMultiply();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(DivideAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.getOverloadingContext().getDivide();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(RemainderAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.getOverloadingContext().getRemainder();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult defaultVisit(Ast ast, ExpContext expContext) {
        throw new UnsupportedOperationException();
    }

}
