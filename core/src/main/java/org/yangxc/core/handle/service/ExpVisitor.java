package org.yangxc.core.handle.service;

import org.yangxc.core.annotation.NumberType;
import org.yangxc.core.ast.AstVisitor;
import org.yangxc.core.ast.tree.*;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.handle.overloading.CastContext;
import org.yangxc.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.core.handle.overloading.OperatorOverloadingContext;
import org.yangxc.core.handle.overloading.OverloadingContext;

import java.util.function.Function;

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

        private ExpContext copy(StringBuilder stringBuilder) {
            return new ExpContext(stringBuilder, overloadingContext, symbolContext, numberType);
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
        if (expContext.numberType == NumberType.PRIMITIVE) {
            if (ast.isDouble()) {
                expContext.append(ast.toString());
                return expContext.createResult(ClassName.DOUBLE);
            }
            if (ast.isLong()) {
                expContext.append(ast.toString()).append("L");
                return expContext.createResult(ClassName.LONG);
            }
            expContext.append(ast.toString());
            return expContext.createResult(ClassName.INT);
        }
        throw new UnsupportedOperationException("unknown numberType convert: " + expContext.numberType);
    }

    @Override
    public ExpResult visit(CastAst ast, ExpContext expContext) {
        ExpContext subExpContext = expContext.copy(new StringBuilder());
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
                    .append(".").append(cast.name()).append("()");
            case STATIC_METHOD -> expContext.append(cast.name())
                    .append("(").append(subExpContext.toString()).append(")");
            default -> throw new UnsupportedOperationException("unknown cast type: " + cast.type());
        }
        return expContext.createResult(ast.getSourceType());
    }

    private ExpResult visitBiOperator(BiAst ast, ExpContext expContext, Function<ClassOverloadingContext, OperatorOverloadingContext> getOverloading) {
        ExpContext subExpContext = expContext.copy(new StringBuilder());
        ExpResult res = ast.getLeft().accept(this, subExpContext);
        OperatorOverloadingContext overloading = getOverloading.apply(res.overloadingContext);
        return switch (overloading.type()) {
            case PRIMITIVE -> {
                expContext.append("(").append(subExpContext.toString()).append(overloading.name());
                ast.getRight().accept(this, expContext);
                expContext.append(")");
                yield expContext.createResult(overloading.resultType());
            }
            case METHOD -> {
                expContext.append(subExpContext.toString()).append(".").append(overloading.name()).append("(");
                ast.getRight().accept(this, expContext);
                expContext.append(")");
                yield expContext.createResult(overloading.resultType());
            }
            case STATIC_METHOD -> {
                expContext.append(overloading.name()).append(subExpContext.toString()).append(", ");
                ast.getRight().accept(this, expContext);
                expContext.append(")");
                yield expContext.createResult(overloading.resultType());
            }
        };
    }

    @Override
    public ExpResult visit(AddAst ast, ExpContext expContext) {
        return visitBiOperator(ast, expContext, ClassOverloadingContext::getAdd);
    }

    @Override
    public ExpResult visit(SubtractAst ast, ExpContext expContext) {
        return visitBiOperator(ast, expContext, ClassOverloadingContext::getSubtract);
    }

    @Override
    public ExpResult visit(MultiplyAst ast, ExpContext expContext) {
        return visitBiOperator(ast, expContext, ClassOverloadingContext::getMultiply);
    }

    @Override
    public ExpResult visit(DivideAst ast, ExpContext expContext) {
        return visitBiOperator(ast, expContext, ClassOverloadingContext::getDivide);
    }

    @Override
    public ExpResult visit(RemainderAst ast, ExpContext expContext) {
        return visitBiOperator(ast, expContext, ClassOverloadingContext::getRemainder);
    }

    @Override
    public ExpResult defaultVisit(Ast ast, ExpContext expContext) {
        throw new UnsupportedOperationException("unsupported exp handle[" + ast.getClass().getSimpleName() + "]: " + ast);
    }

}
