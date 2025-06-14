package org.yangxc.operatoroverloading.core.handle.writer;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionWriterContext {

    private List<String> docLines;
    private String returnType;
    private String name;
    private List<Param> params;
    private List<String> throwList;

    private List<String> bodyLines;

    public void setDocLines(List<String> docLines) {
        this.docLines = docLines;
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
        StringBuilder doc = new StringBuilder();
        if (docLines != null) {
            doc.append(prefix).append("/**\n");
            for (String docLine : docLines) {
                doc.append(prefix).append(" *").append(docLine).append("\n");
            }
            doc.append(prefix).append(" */\n");
        }
        String paramString = params.stream()
                .map(param -> param.getType() + " " + param.getName())
                .collect(Collectors.joining(", "));
        String throwsString = throwList.isEmpty() ? "" : ("throws " + String.join(",", throwList) + " ");
        return doc +
                prefix + "@Override\n" +
                prefix + "public " + returnType + " " + name + "(" + paramString + ") " + throwsString + "{\n" +
                bodyLines.stream().map(line -> prefix + ServiceWriterContext.TAB + line + "\n").collect(Collectors.joining()) +
                prefix + "}\n";
    }

}
