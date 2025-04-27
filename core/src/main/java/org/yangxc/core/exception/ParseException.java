package org.yangxc.core.exception;

public class ParseException extends RuntimeException{

    public ParseException(String value, int offset) {
        super("exception parse[" + value + "] of [" + value.substring(offset, Math.min(value.length() - offset, 5)) + "]");
    }

}
