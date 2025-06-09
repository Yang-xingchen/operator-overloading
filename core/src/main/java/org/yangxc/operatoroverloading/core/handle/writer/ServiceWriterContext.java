package org.yangxc.operatoroverloading.core.handle.writer;

import org.yangxc.operatoroverloading.core.processor.MainProcessor;
import org.yangxc.operatoroverloading.core.util.ImportContext;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceWriterContext {

    private String pack;
    private List<String> docLines;
    private String className;
    private String interfaceName;

    private ImportContext importContext;
    private List<Param> fieldList;
    private List<FunctionWriterContext> functionWriterContexts;

    public static final String TAB = "    ";
    private static final String GENERATED = Generated.class.getTypeName();

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

    public ImportContext handelImport(Collection<String> imports) {
        importContext = new ImportContext(Stream.concat(imports.stream(), Stream.of(GENERATED)).collect(Collectors.toList()));
        return importContext;
    }

    public void setFieldList(List<Param> fieldList) {
        this.fieldList = fieldList;
    }

    public void setFunctionWrites(List<FunctionWriterContext> functionWriterContexts) {
        this.functionWriterContexts = functionWriterContexts;
    }

    public String code() {
        String pack = this.pack != null ? ("package " + this.pack + ";\n\n") : "";
        StringBuilder doc = new StringBuilder();
        if (docLines != null) {
            doc.append("/**\n");
            for (String docLine : docLines) {
                doc.append(" *").append(docLine).append("\n");
            }
            doc.append(" */\n");
        }
        String imports = importContext.write();
        String generated = "@" + importContext.getSimpleName(GENERATED) + "(" +
                "value=\"" + MainProcessor.class.getTypeName() + "\", " +
                "date=\"" + LocalDateTime.now().withNano(0) + "\", " +
                "comments=\"created by OperatorOverloading\")";
        String className = "public class " + this.className +" implements " + interfaceName;
        StringBuilder fields = new StringBuilder();
        for (Param field : fieldList) {
            fields.append(TAB)
                    .append("private ")
                    .append(importContext.getSimpleName(field.getType()))
                    .append(" ")
                    .append(field.getName())
                    .append(";\n");
        }
        StringBuilder methods = new StringBuilder();
        for (FunctionWriterContext functionWriterContext : functionWriterContexts) {
            methods.append(functionWriterContext.code(TAB)).append("\n");
        }
        return pack +
                imports + "\n" +
                doc +
                generated + "\n" +
                className + " {\n\n" +
                fields +
                methods +
                "}";
    }

}
