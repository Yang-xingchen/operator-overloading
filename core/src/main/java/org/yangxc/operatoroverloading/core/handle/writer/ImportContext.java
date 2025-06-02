package org.yangxc.operatoroverloading.core.handle.writer;

import org.yangxc.operatoroverloading.core.constant.ClassName;

import java.util.*;
import java.util.stream.Collectors;

public class ImportContext {

    // qualifiedName-simpleName
    private final Map<String, String> importMap;

    private static final String JAVA_LANG = "java.lang.";

    public ImportContext(Collection<String> imports) {
        Map<String, List<String>> map = imports.stream()
                .filter(type -> !ClassName.PRIMITIVE_NAME.contains(type))
                .filter(type -> !type.startsWith(JAVA_LANG))
                .distinct()
                .collect(Collectors.groupingBy(ClassName::getSimpleName));
        importMap = new HashMap<>(map.size());
        map.forEach((name, list) -> {
            if (list.size() == 1) {
                importMap.put(list.get(0), name);
            } else {
                list.forEach(n -> importMap.put(n, n));
            }
        });
    }

    public String getSimpleName(String qualifiedName) {
        if (qualifiedName.startsWith(JAVA_LANG)) {
            return qualifiedName.substring(JAVA_LANG.length());
        }
        return importMap.getOrDefault(qualifiedName, qualifiedName);
    }

    String write() {
        return this.importMap
                .keySet()
                .stream()
                .filter(s -> !Objects.equals(s, this.importMap.get(s)))
                .sorted()
                .reduce(new StringBuilder(),
                        (sb, type) -> sb.append("import ").append(type).append(";\n"),
                        StringBuilder::append)
                .toString();
    }

}
