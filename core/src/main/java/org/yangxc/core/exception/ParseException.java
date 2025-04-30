package org.yangxc.core.exception;

public class ParseException extends RuntimeException{

    public ParseException(String value, int offset) {
        super(toMsg(value, offset));
    }

    private static String toMsg(String value, int offset) {
        if ((offset + 10) >= value.length()) {
            return "exception parse[" + value + "] of [" +value.substring(offset) + "]";
        }
        return "exception parse[" + value + "] of [" + value.substring(offset, offset + 10) + "...]";
    }

}
