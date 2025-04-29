package org.yangxc.core.exception;

import javax.lang.model.element.Element;

public class ElementException extends RuntimeException {

    private final Element element;

    public ElementException(String message, Element element) {
        super(message);
        this.element = element;
    }

    public ElementException(Throwable cause, Element element) {
        super(cause);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

}
