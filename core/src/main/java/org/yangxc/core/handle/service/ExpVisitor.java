package org.yangxc.core.handle.service;

import org.yangxc.core.annotation.NumberType;
import org.yangxc.core.ast.AstVisitor;
import org.yangxc.core.ast.tree.*;
import org.yangxc.core.constant.ClassName;
import org.yangxc.core.handle.overloading.CastContext;
import org.yangxc.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.core.handle.overloading.OperatorOverloadingContext;
import org.yangxc.core.handle.overloading.OverloadingContext;

import java.util.Map;

public class ExpVisitor implements AstVisitor<ExpVisitor.ExpContext, ExpVisitor.ExpResult> {

    public static final ExpVisitor INSTANCE = new ExpVisitor();

    private ExpVisitor() {
    }

    public static class ExpContext {

        private final StringBuilder stringBuilder;
        private final OverloadingContext overloadingContext;
        private final Map<String, VariableContext> varMap;
        private final NumberType numberType;

        public ExpContext(StringBuilder stringBuilder, OverloadingContext overloadingContext, Map<String, VariableContext> varMap, NumberType numberType) {
            this.stringBuilder = stringBuilder;
            this.overloadingContext = overloadingContext;
            this.varMap = varMap;
            this.numberType = numberType;
        }

        public ExpContext(OverloadingContext overloadingContext, Map<String, VariableContext> varMap, NumberType numberType) {
            this(new StringBuilder(), overloadingContext, varMap, numberType);
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
        return expContext.createResult(expContext.varMap.get(ast.toString()).type());
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
