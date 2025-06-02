package org.yangxc.operatoroverloading.core.util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public abstract class BaseAnnotationValueVisitor<R, P> implements AnnotationValueVisitor<R, P> {

    @Override
    public R visit(AnnotationValue av) {
        return null;
    }

    @Override
    public R visit(AnnotationValue av, P p) {
        return visitDefault(av, p);
    }

    @Override
    public R visitBoolean(boolean b, P p) {
        return visitDefault(b, p);
    }

    @Override
    public R visitByte(byte b, P p) {
        return visitDefault(b, p);
    }

    @Override
    public R visitChar(char c, P p) {
        return visitDefault(c, p);
    }

    @Override
    public R visitDouble(double d, P p) {
        return visitDefault(d, p);
    }

    @Override
    public R visitFloat(float f, P p) {
        return visitDefault(f, p);
    }

    @Override
    public R visitInt(int i, P p) {
        return visitDefault(i, p);
    }

    @Override
    public R visitLong(long i, P p) {
        return visitDefault(i, p);
    }

    @Override
    public R visitShort(short s, P p) {
        return visitDefault(s, p);
    }

    @Override
    public R visitString(String s, P p) {
        return visitDefault(s, p);
    }

    @Override
    public R visitType(TypeMirror t, P p) {
        return visitDefault(t, p);
    }

    @Override
    public R visitEnumConstant(VariableElement c, P p) {
        return visitDefault(c, p);
    }

    @Override
    public R visitAnnotation(AnnotationMirror a, P p) {
        return visitDefault(a, p);
    }

    @Override
    public R visitArray(List<? extends AnnotationValue> vals, P p) {
        return visitDefault(vals, p);
    }

    private R visitDefault(Object value, P p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R visitUnknown(AnnotationValue av, P p) {
        return null;
    }

}
