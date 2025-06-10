package org.yangxc.operatoroverloading.core.util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class GetAnnotationValueVisitor<R> extends BaseAnnotationValueVisitor<R, Object> {

    @Override
    public R visit(AnnotationValue av) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visit(AnnotationValue av, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitBoolean(boolean b, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitByte(byte b, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitChar(char c, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitDouble(double d, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitFloat(float f, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitInt(int i, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitLong(long i, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitShort(short s, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitString(String s, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitType(TypeMirror t, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitEnumConstant(VariableElement c, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitAnnotation(AnnotationMirror a, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitArray(List<? extends AnnotationValue> vals, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitUnknown(AnnotationValue av, Object object) {
        throw new UnsupportedOperationException();
    }

    public static AnnotationValueVisitor<Boolean, Object> visitBoolean() {
        return visitBoolean(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitBoolean(Function<Boolean, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitBoolean(boolean b, Object object) {
                return map.apply(b);
            }
        };
    }

    public static AnnotationValueVisitor<Byte, Object> visitByte() {
        return visitByte(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitByte(Function<Byte, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitByte(byte b, Object object) {
                return map.apply(b);
            }
        };
    }

    public static AnnotationValueVisitor<Character, Object> visitChar() {
        return visitChar(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitChar(Function<Character, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitChar(char c, Object object) {
                return map.apply(c);
            }
        };
    }

    public static AnnotationValueVisitor<Double, Object> visitDouble() {
        return visitDouble(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitDouble(Function<Double, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitDouble(double d, Object object) {
                return map.apply(d);
            }
        };
    }

    public static AnnotationValueVisitor<Float, Object> visitFloat() {
        return visitFloat(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitFloat(Function<Float, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitFloat(float f, Object object) {
                return map.apply(f);
            }
        };
    }

    public static AnnotationValueVisitor<Integer, Object> visitInt() {
        return visitInt(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitInt(Function<Integer, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitInt(int i, Object object) {
                return map.apply(i);
            }
        };
    }

    public static AnnotationValueVisitor<Long, Object> visitLong() {
        return visitLong(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitLong(Function<Long, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitLong(long i, Object object) {
                return map.apply(i);
            }
        };
    }

    public static AnnotationValueVisitor<Short, Object> visitShort() {
        return visitShort(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitShort(Function<Short, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitShort(short s, Object object) {
                return map.apply(s);
            }
        };
    }

    public static AnnotationValueVisitor<String, Object> visitString() {
        return visitString(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitString(Function<String, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitString(String s, Object object) {
                return map.apply(s);
            }
        };
    }

    public static AnnotationValueVisitor<TypeMirror, Object> visitType() {
        return visitType(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitType(Function<TypeMirror, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitType(TypeMirror t, Object object) {
                return map.apply(t);
            }
        };
    }

    public static AnnotationValueVisitor<VariableElement, Object> visitEnumConstant() {
        return visitEnumConstant(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitEnumConstant(Function<VariableElement, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitEnumConstant(VariableElement c, Object object) {
                return map.apply(c);
            }
        };
    }

    public static <R extends Enum<R>> AnnotationValueVisitor<R, Object> visitEnum(Class<R> enumType) {
        return visitEnumConstant(e -> Enum.valueOf(enumType, e.getSimpleName().toString()));
    }

    public static AnnotationValueVisitor<AnnotationMirror, Object> visitAnnotation() {
        return visitAnnotation(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitAnnotation(Function<AnnotationMirror, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitAnnotation(AnnotationMirror a, Object object) {
                return map.apply(a);
            }
        };
    }

    public static AnnotationValueVisitor<List<? extends AnnotationValue>, Object> visitArray() {
        return visitArray(Function.identity());
    }

    public static <R> AnnotationValueVisitor<R, Object> visitArray(Function<List<? extends AnnotationValue>, R> map) {
        return new GetAnnotationValueVisitor<R>() {
            @Override
            public R visitArray(List<? extends AnnotationValue> vals, Object object) {
                return map.apply(vals);
            }
        };
    }

    public static <R> AnnotationValueVisitor<List<R>, Object> visitArrayEach(AnnotationValueVisitor<R, Object> map) {
        return new GetAnnotationValueVisitor<List<R>>() {
            @Override
            public List<R> visitArray(List<? extends AnnotationValue> vals, Object object) {
                return vals.stream()
                        .map(val -> val.accept(map, object))
                        .collect(Collectors.toList());
            }
        };
    }

    public static <R, P> AnnotationValueVisitor<List<R>, Object> visitArrayEach(AnnotationValueVisitor<R, Object> map, P p) {
        return new GetAnnotationValueVisitor<List<R>>() {
            @Override
            public List<R> visitArray(List<? extends AnnotationValue> vals, Object object) {
                return vals.stream()
                        .map(val -> val.accept(map, p))
                        .collect(Collectors.toList());
            }
        };
    }

}
