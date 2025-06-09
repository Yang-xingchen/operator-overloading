package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.ast.AstVisitor;
import org.yangxc.operatoroverloading.core.ast.tree.*;
import org.yangxc.operatoroverloading.core.constant.CastMethodType;
import org.yangxc.operatoroverloading.core.constant.ClassName;
import org.yangxc.operatoroverloading.core.constant.OperatorMethodType;
import org.yangxc.operatoroverloading.core.constant.VariableDefineType;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OperatorOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import java.util.function.Function;

public class ExpVisitor implements AstVisitor<ExpVisitor.ExpContext, ExpVisitor.ExpResult> {

    public static final ExpVisitor INSTANCE = new ExpVisitor();

    private ExpVisitor() {
    }

    public static class ExpContext {

        private final StringBuilder stringBuilder;
        private final OverloadingContext overloadingContext;
        private final VariableSetContext variableContexts;
        private final ImportContext importContext;
        private final NumberType numberType;

        public ExpContext(StringBuilder stringBuilder, OverloadingContext overloadingContext, VariableSetContext variableContexts, ImportContext importContext, NumberType numberType) {
            this.stringBuilder = stringBuilder;
            this.overloadingContext = overloadingContext;
            this.variableContexts = variableContexts;
            this.importContext = importContext;
            this.numberType = numberType;
        }

        public ExpContext(OverloadingContext overloadingContext, VariableSetContext variableContexts, ImportContext importContext, NumberType numberType) {
            this(new StringBuilder(), overloadingContext, variableContexts, importContext, numberType);
        }

        private ExpContext copy(StringBuilder stringBuilder) {
            return new ExpContext(stringBuilder, overloadingContext, variableContexts, importContext, numberType);
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
        if (variableContext.getDefineType() == VariableDefineType.PARAM
                || variableContext.getDefineType() == VariableDefineType.LOCAL) {
            expContext.append(variableContext.getName());
        } else if (variableContext.getDefineType() == VariableDefineType.THIS) {
            expContext.append("this.").append(variableContext.getName());
        } else if (variableContext.getDefineType() == VariableDefineType.STATIC) {
            expContext.append(expContext.importContext.getSimpleName(variableContext.getDefineTypeName()))
                    .append(".").append(variableContext.getName());
        }
        return expContext.createResult(ClassName.unboxedType(variableContext.getType()));
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
        if (cast.getType() == CastMethodType.CAST) {
            expContext.append("(").append(expContext.importContext.getSimpleName(ast.getSourceType())).append(")")
                    .append(subExpContext.toString());
        } else if (cast.getType() == CastMethodType.NEW) {
            expContext.append("new ").append(expContext.importContext.getSimpleName(ast.getSourceType()))
                    .append("(").append(subExpContext.toString()).append(")");
        } else if (cast.getType() == CastMethodType.METHOD) {
            expContext.append(subExpContext.toString())
                    .append(".").append(cast.getName()).append("()");
        } else if (cast.getType() == CastMethodType.STATIC_METHOD) {
            expContext.append(expContext.importContext.getSimpleName(cast.getClassName())).append(".").append(cast.getName())
                    .append("(").append(subExpContext.toString()).append(")");
            return expContext.createResult(ast.getSourceType());
        } else {
            throw new UnsupportedOperationException("unknown cast type: " + cast.getType());
        }
        return expContext.createResult(ast.getSourceType());
    }

    private ExpResult visitBiOperator(BiAst ast, ExpContext expContext, Function<ClassOverloadingContext, OperatorOverloadingContext> getOverloading) {
        ExpContext subExpContext = expContext.copy(new StringBuilder());
        ExpResult res = ast.getLeft().accept(this, subExpContext);
        OperatorOverloadingContext overloading = getOverloading.apply(res.overloadingContext);
        if (overloading.getType() == OperatorMethodType.PRIMITIVE) {
            expContext.append("(").append(subExpContext.toString()).append(overloading.getName());
            ExpResult rightRes = ast.getRight().accept(this, expContext);
            expContext.append(")");
            return expContext.createResult(ClassName.getPrimitiveType(res.type, rightRes.type));
        } else if (overloading.getType() == OperatorMethodType.METHOD) {
            expContext.append(subExpContext.toString()).append(".").append(overloading.getName()).append("(");
            ast.getRight().accept(this, expContext);
            expContext.append(")");
            return expContext.createResult(overloading.getResultType());
        } else if (overloading.getType() == OperatorMethodType.STATIC_METHOD) {
            expContext.append(expContext.importContext.getSimpleName(overloading.getClassName())).append(".").append(overloading.getName());
            expContext.append("(").append(subExpContext.toString()).append(", ");
            ast.getRight().accept(this, expContext);
            expContext.append(")");
            return expContext.createResult(overloading.getResultType());
        }
        throw new UnsupportedOperationException("unknown OperatorMethodType: " + overloading.getType());
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
