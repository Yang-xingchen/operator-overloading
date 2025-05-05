package org.yangxc.core.handle.writer;

import org.yangxc.core.constant.ClassName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceWriter {

    private String pack;
    private String className;
    private String interfaceName;

    // qualifiedName-simpleName
    private Map<String, String> importMap;
    private List<FunctionWriter> functionWriters;

    public static final String TAB = "    ";
    private static final String GENERATED = "javax.annotation.processing.Generated";

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Map<String, String> handelImport(List<String> imports) {
        Map<String, List<String>> map = Stream.concat(imports.stream(), Stream.of(GENERATED))
                .filter(type -> !ClassName.PRIMITIVE_NAME.contains(type))
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

    public void setFunctionWrites(List<FunctionWriter> functionWriters) {
        this.functionWriters = functionWriters;
    }

    public String code() {
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
                "value=\"org.yangxc.core.processor.ServiceProcessor\", " +
                "date=\"" + LocalDateTime.now().withNano(0) + "\", " +
                "comments=\"OperatorOverloading service\")";
        String className = "public class " + this.className +" implements " + interfaceName;
        String methods = functionWriters
                .stream()
                .map(functionWriter -> functionWriter.code(TAB))
                .collect(Collectors.joining("\n"));
        return (this.pack != null ? ("package " + this.pack + ";\n\n") : "") +
                imports + "\n" +
                generated + "\n" +
                className + " {\n\n" +
                methods + "\n" +
                "}";
    }

}
