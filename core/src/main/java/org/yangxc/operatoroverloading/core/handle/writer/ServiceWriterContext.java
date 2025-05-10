package org.yangxc.operatoroverloading.core.handle.writer;

import org.yangxc.operatoroverloading.core.constant.ClassName;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceWriterContext {

    private String pack;
    private List<String> docLines;
    private String className;
    private String interfaceName;

    // qualifiedName-simpleName
    private Map<String, String> importMap;
    private List<Param> fieldList;
    private List<FunctionWriterContext> functionWriterContexts;

    public static final String TAB = "    ";
    private static final String GENERATED = "javax.annotation.processing.Generated";

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setDocLines(List<String> docLines) {
        this.docLines = docLines;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Map<String, String> handelImport(Collection<String> imports) {
        Map<String, List<String>> map = Stream.concat(imports.stream(), Stream.of(GENERATED))
                .filter(type -> !ClassName.PRIMITIVE_NAME.contains(type))
                .filter(type -> !type.startsWith("java.lang."))
                .distinct()
                .collect(Collectors.groupingBy(ClassName::getSimpleName));
        importMap = new HashMap<>(map.size());
        map.forEach((name, list) -> {
            if (list.size() == 1) {
                importMap.put(list.getFirst(), name);
            } else {
                list.forEach(n -> importMap.put(n, n));
            }
        });
        return importMap;
    }

    public void setFieldList(List<Param> fieldList) {
        this.fieldList = fieldList;
    }

    public void setFunctionWrites(List<FunctionWriterContext> functionWriterContexts) {
        this.functionWriterContexts = functionWriterContexts;
    }

    public String code() {
        StringBuilder doc = new StringBuilder();
        if (docLines != null) {
            doc.append("/**\n");
            for (String docLine : docLines) {
                doc.append(" *").append(docLine).append("\n");
            }
            doc.append(" */\n");
        }
        String imports = this.importMap
                .keySet()
                .stream()
                .filter(s -> !Objects.equals(s, this.importMap.get(s)))
                .sorted()
                .reduce(new StringBuilder(),
                        (sb, type) -> sb.append("import ").append(type).append(";\n"),
                        StringBuilder::append)
                .toString();
        String generated = "@" + this.importMap.getOrDefault(GENERATED, GENERATED) + "(" +
                "value=\"org.yangxc.operatoroverloading.core.processor.MainProcessor\", " +
                "date=\"" + LocalDateTime.now().withNano(0) + "\", " +
                "comments=\"created by OperatorOverloading\")";
        String className = "public class " + this.className +" implements " + interfaceName;
        StringBuilder fields = new StringBuilder();
        for (Param field : fieldList) {
            fields.append(TAB)
                    .append("private ")
                    .append(this.importMap.getOrDefault(field.type(), field.type()))
                    .append(" ")
                    .append(field.name())
                    .append(";\n");
        }
        StringBuilder methods = new StringBuilder();
        for (FunctionWriterContext functionWriterContext : functionWriterContexts) {
            methods.append(functionWriterContext.code(TAB)).append("\n");
        }
        return (this.pack != null ? ("package " + this.pack + ";\n\n") : "") +
                imports + "\n" +
                doc +
                generated + "\n" +
                className + " {\n\n" +
                fields +
                methods +
                "}";
    }

}
