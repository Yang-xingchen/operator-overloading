package org.yangxc.core.handle.service;

import org.yangxc.core.annotation.NumberType;
import org.yangxc.core.ast.AstVisitor;
import org.yangxc.core.ast.tree.*;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.handle.overloading.CastContext;
import org.yangxc.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.core.handle.overloading.OperatorOverloadingContext;
import org.yangxc.core.handle.overloading.OverloadingContext;

public class ExpVisitor implements AstVisitor<ExpVisitor.ExpContext, ExpVisitor.ExpResult> {

    public static final ExpVisitor INSTANCE = new ExpVisitor();

    private ExpVisitor() {
    }

    public static class ExpContext {

        private final StringBuilder stringBuilder;
        private final OverloadingContext overloadingContext;
        private final SymbolContext symbolContext;
        private final NumberType numberType;

        public ExpContext(StringBuilder stringBuilder, OverloadingContext overloadingContext, SymbolContext symbolContext, NumberType numberType) {
            this.stringBuilder = stringBuilder;
            this.overloadingContext = overloadingContext;
            this.symbolContext = symbolContext;
            this.numberType = numberType;
        }

        public ExpContext(OverloadingContext overloadingContext, SymbolContext symbolContext, NumberType numberType) {
            this(new StringBuilder(), overloadingContext, symbolContext, numberType);
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

        public CastContext cast(String toTypeName) {
            return overloadingContext.cast(toTypeName);
        }

    }

    @Override
    public ExpResult visit(VariableAst ast, ExpContext expContext) {
        expContext.append(ast.toString());
        return expContext.createResult(expContext.symbolContext.getVarType(ast.toString()));
    }

    @Override
    public ExpResult visit(NumberAst ast, ExpContext expContext) {
        if (expContext.numberType == NumberType.BIG_DECIMAL) {
            expContext.append("new BigDecimal(\"").append(ast.value()).append("\")");
            return expContext.createResult(ClassName.BIG_DECIMAL);
        }
        if (expContext.numberType == NumberType.BIG_INTEGER) {
            if (ast.getDecimal() != null) {
                throw new UnsupportedOperationException("can't convert [" + ast + "] to BigInteger");
            }
            expContext.append("new BigInteger(\"").append(ast.value()).append("\")");
            return expContext.createResult(ClassName.BIG_INTEGER);
        }
        throw new UnsupportedOperationException("unknown numberType convert[" + expContext.numberType + "]");
    }

    @Override
    public ExpResult visit(CastAst ast, ExpContext expContext) {
        ExpContext subExpContext = new ExpContext(new StringBuilder(), expContext.overloadingContext, expContext.symbolContext, expContext.numberType);
        ExpResult res = ast.getAst().accept(this, subExpContext);
        if (res.type.equals(ast.getSourceType())) {
            expContext.append(subExpContext.toString());
            return expContext.createResult(ast.getSourceType());
        }
        CastContext cast = res.cast(ast.getSourceType());
        switch (cast.type()) {
            case CAST -> expContext.append("(").append(ClassName.getSimpleName(ast.getSourceType())).append(")")
                    .append(subExpContext.toString());
            case NEW -> expContext.append("new ").append(ClassName.getSimpleName(ast.getSourceType()))
                    .append("(").append(subExpContext.toString()).append(")");
            case METHOD -> expContext.append(subExpContext.toString())
                    .append(".").append(cast.name()).append("()'");
            case STATIC_METHOD -> expContext.append(cast.name())
                    .append("(").append(subExpContext.toString()).append(")");
        }
        return expContext.createResult(ast.getSourceType());
    }

    @Override
    public ExpResult visit(AddAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.overloadingContext.getAdd();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(SubtractAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.overloadingContext.getSubtract();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(MultiplyAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.overloadingContext.getMultiply();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(DivideAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.overloadingContext.getDivide();
        expContext.append(".").append(overloading.name()).append("(");
        ast.getRight().accept(this, expContext);
        expContext.append(")");
        return expContext.createResult(overloading.resultType());
    }

    @Override
    public ExpResult visit(RemainderAst ast, ExpContext expContext) {
        ExpResult res = ast.getLeft().accept(this, expContext);
        OperatorOverloadingContext overloading = res.overloadingContext.getRemainder();
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
