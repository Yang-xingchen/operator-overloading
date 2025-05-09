package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.ast.AstVisitor;
import org.yangxc.operatoroverloading.core.ast.tree.*;
import org.yangxc.operatoroverloading.core.constant.ClassName;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OperatorOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;

import java.util.Map;
import java.util.function.Function;

public class ExpVisitor implements AstVisitor<ExpVisitor.ExpContext, ExpVisitor.ExpResult> {

    public static final ExpVisitor INSTANCE = new ExpVisitor();

    private ExpVisitor() {
    }

    public static class ExpContext {

        private final StringBuilder stringBuilder;
        private final OverloadingContext overloadingContext;
        private final VariableSetContext variableContexts;
        private final Map<String, String> importMap;
        private final NumberType numberType;

        public ExpContext(StringBuilder stringBuilder, OverloadingContext overloadingContext, VariableSetContext variableContexts, Map<String, String> importMap, NumberType numberType) {
            this.stringBuilder = stringBuilder;
            this.overloadingContext = overloadingContext;
            this.variableContexts = variableContexts;
            this.importMap = importMap;
            this.numberType = numberType;
        }

        public ExpContext(OverloadingContext overloadingContext, VariableSetContext variableContexts, Map<String, String> importMap, NumberType numberType) {
            this(new StringBuilder(), overloadingContext, variableContexts, importMap, numberType);
        }

        private ExpContext copy(StringBuilder stringBuilder) {
            return new ExpContext(stringBuilder, overloadingContext, variableContexts, importMap, numberType);
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
        VariableContext variableContext = expContext.variableContexts.get(ast.qualifiedName());
        switch (variableContext.getDefineType()) {
            case PARAM, LOCAL -> expContext.append(variableContext.getName());
            case THIS -> expContext.append("this.").append(variableContext.getName());
            case STATIC -> expContext.append(expContext.importMap.getOrDefault(variableContext.getDefineTypeName(), variableContext.getDefineTypeName()))
                            .append(".").append(variableContext.getName());
        }
        return expContext.createResult(variableContext.getType());
    }

    @Override
    public ExpResult visit(NumberAst ast, ExpContext expContext) {
        if (expContext.numberType == NumberType.BIG_DECIMAL) {
            expContext.append("new BigDecimal(\"").append(ast.value()).append("\")");
            return expContext.createResult(ClassName.BIG_DECIMAL);
        }
        if (expContext.numberType == NumberType.BIG_INTEGER) {
            if (ast.isDecimal()) {
                throw new UnsupportedOperationException("can't convert [" + ast.value() + "] to BigInteger");
            }
            expContext.append("new BigInteger(\"").append(ast.value()).append("\")");
            return expContext.createResult(ClassName.BIG_INTEGER);
        }
        if (expContext.numberType == NumberType.PRIMITIVE) {
            if (ast.isDouble()) {
                String value = ast.value() + (ast.isDecimal() ? "" : ".0");
                if (ast.isNegative()) {
                    expContext.append("(").append(value).append(")");
                } else {
                    expContext.append(value);
                }
                return expContext.createResult(ClassName.DOUBLE);
            }
            if (ast.isLong()) {
                if (ast.isNegative()) {
                    expContext.append("(").append(ast.value()).append("L)");
                } else {
                    expContext.append(ast.value()).append("L");
                }
                return expContext.createResult(ClassName.LONG);
            }
            if (ast.isNegative()) {
                expContext.append("(").append(ast.value()).append(")");
            } else {
                expContext.append(ast.value());
            }
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
            case CAST -> expContext.append("(").append(expContext.importMap.getOrDefault(ast.getSourceType(), ast.getSourceType())).append(")")
                    .append(subExpContext.toString());
            case NEW -> expContext.append("new ").append(expContext.importMap.getOrDefault(ast.getSourceType(), ast.getSourceType()))
                    .append("(").append(subExpContext.toString()).append(")");
            case METHOD -> expContext.append(subExpContext.toString())
                    .append(".").append(cast.name()).append("()");
            case STATIC_METHOD -> {
                expContext.append(expContext.importMap.getOrDefault(cast.className(), cast.className())).append(".").append(cast.name())
                        .append("(").append(subExpContext.toString()).append(")");
            }
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
                expContext.append(expContext.importMap.getOrDefault(overloading.className(), overloading.className())).append(".").append(overloading.name());
                expContext.append("(").append(subExpContext.toString()).append(", ");
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
