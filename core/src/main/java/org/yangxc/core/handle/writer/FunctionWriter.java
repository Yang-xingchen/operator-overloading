package org.yangxc.core.handle.writer;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionWriter {

    private String returnType;
    private String name;
    private List<Param> params;
    private List<String> throwList;

    private List<String> bodyLines;

    public record Param(String type, String name) {

    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setThrowList(List<String> throwList) {
        this.throwList = throwList;
    }

    public void setBodyLines(List<String> bodyLines) {
        this.bodyLines = bodyLines;
    }

    public String code(String prefix) {
        String paramString = params.stream().map(param -> param.type + " " + param.name).collect(Collectors.joining(", "));
        String throwsString = throwList.isEmpty() ? "" : ("throws " + String.join(",", throwList) + " ");
        return prefix + "@Override\n" +
                prefix + "public " + returnType + " " + name + "(" + paramString + ") " + throwsString + "{\n" +
                bodyLines.stream().map(line -> prefix + ServiceWriter.TAB + line + "\n").collect(Collectors.joining()) +
                prefix + "}\n";
    }

}
