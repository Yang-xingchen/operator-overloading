package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.annotation.NumberType;
import org.yangxc.operatoroverloading.core.ast.AstVisitor;
import org.yangxc.operatoroverloading.core.ast.tree.*;
import org.yangxc.operatoroverloading.core.constant.ClassName;
import org.yangxc.operatoroverloading.core.constant.VariableDefineType;
import org.yangxc.operatoroverloading.core.handle.overloading.CastContext;
import org.yangxc.operatoroverloading.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OperatorOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;

import java.util.function.Function;
import java.util.stream.Stream;

public class GetImportVisitor implements AstVisitor<GetImportVisitor.ExpContext, GetImportVisitor.ExpResult> {

    public static final GetImportVisitor INSTANCE = new GetImportVisitor();

    private GetImportVisitor() {
    }

    public static class ExpContext {

        private final OverloadingContext overloadingContext;
        private final VariableSetContext variableContexts;
        private final NumberType numberType;

        public ExpContext(OverloadingContext overloadingContext, VariableSetContext variableContexts, NumberType numberType) {
            this.overloadingContext = overloadingContext;
            this.variableContexts = variableContexts;
            this.numberType = numberType;
        }

        public ExpResult createResult(String type, Stream<String> stream) {
            return new ExpResult(type, overloadingContext.get(type), stream);
        }

    }

    public static class ExpResult {
        private final String type;
        private final ClassOverloadingContext overloadingContext;
        private final Stream<String> stream;

        public ExpResult(String type, ClassOverloadingContext overloadingContext, Stream<String> stream) {
            this.type = type;
            this.overloadingContext = overloadingContext;
            this.stream = stream;
        }

        public String getType() {
            return type;
        }

        public CastContext cast(String toTypeName) {
            return overloadingContext.cast(toTypeName);
        }

        public Stream<String> stream() {
            return stream;
        }
    }

    @Override
    public ExpResult visit(VariableAst ast, ExpContext expContext) {
        VariableContext variableContext = expContext.variableContexts.get(ast.toString());
        if (variableContext.getDefineType() == VariableDefineType.STATIC) {
            return expContext.createResult(ClassName.unboxedType(variableContext.getType()), Stream.of(variableContext.getDefineTypeName()));
        }
        return expContext.createResult(ClassName.unboxedType(variableContext.getType()), Stream.empty());
    }

    @Override
    public ExpResult visit(NumberAst ast, ExpContext expContext) {
        if (expContext.numberType == NumberType.BIG_DECIMAL) {
            return expContext.createResult(ClassName.BIG_DECIMAL, Stream.of(ClassName.BIG_DECIMAL));
        }
        if (expContext.numberType == NumberType.BIG_INTEGER) {
            return expContext.createResult(ClassName.BIG_INTEGER, Stream.of(ClassName.BIG_INTEGER));
        }
        if (expContext.numberType == NumberType.PRIMITIVE) {
            if (ast.isDouble()) {
                return expContext.createResult(ClassName.DOUBLE, Stream.empty());
            }
            if (ast.isLong()) {
                return expContext.createResult(ClassName.LONG, Stream.empty());
            }
            return expContext.createResult(ClassName.INT, Stream.empty());
        }
        throw new UnsupportedOperationException("unknown numberType convert: " + expContext.numberType);
    }

    @Override
    public ExpResult visit(CastAst ast, ExpContext expContext) {
        ExpResult res = ast.getAst().accept(this, expContext);
        if (res.type.equals(ast.getSourceType())) {
            return expContext.createResult(ast.getSourceType(), Stream.empty());
        }
        CastContext cast = res.cast(ast.getSourceType());
        return switch (cast.getType()) {
            case CAST, NEW -> expContext.createResult(ast.getSourceType(), Stream.concat(res.stream, Stream.of(ast.getSourceType())));
            case METHOD -> expContext.createResult(ast.getSourceType(), res.stream);
            case STATIC_METHOD -> expContext.createResult(ast.getSourceType(), Stream.concat(res.stream, Stream.of(cast.getClassName())));
        };
    }

    private ExpResult visitBiOperator(BiAst ast, ExpContext expContext, Function<ClassOverloadingContext, OperatorOverloadingContext> getOverloading) {
        ExpResult leftRes = ast.getLeft().accept(this, expContext);
        ExpResult rightRes = ast.getRight().accept(this, expContext);
        OperatorOverloadingContext overloading = getOverloading.apply(leftRes.overloadingContext);
        return switch (overloading.getType()) {
            case PRIMITIVE -> expContext.createResult(ClassName.getPrimitiveType(leftRes.type, rightRes.type), Stream.concat(leftRes.stream, rightRes.stream));
            case METHOD -> expContext.createResult(overloading.getResultType(), Stream.concat(leftRes.stream, rightRes.stream));
            case STATIC_METHOD -> expContext.createResult(overloading.getResultType(),
                    Stream.of(leftRes.stream, rightRes.stream, Stream.of(overloading.getClassName())).flatMap(Function.identity()));
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
