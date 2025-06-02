package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.FunctionHandle;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.handle.service.VariableContext;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogHandle {

    private final Messager messager;
    private final String level;

    public static final String NONE = "none";
    public static final String DEBUG = "debug";
    public static final String INFO = "info";

    public LogHandle(Messager messager, String level) {
        this.messager = messager;
        level = level != null ? level : "info";
        if (Stream.of(NONE, INFO, DEBUG).noneMatch(level::equals)) {
            messager.printMessage(Diagnostic.Kind.WARNING, "unknown OperatorOverloadingLog: " + level);
            level = INFO;
        }
        this.level = level;
        showBanner();
    }

    private void showBanner() {
        if (NONE.equals(level)) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "------------------------[ OperatorOverloading ]------------------------");
        messager.printMessage(Diagnostic.Kind.NOTE, "issues反馈: https://github.com/Yang-xingchen/operator-overloading/issues");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -AOperatorOverloadingLog=none 仅显示错误信息");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -AOperatorOverloadingLog=info 显示基本信息");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -AOperatorOverloadingLog=debug 显示详细信息");
        messager.printMessage(Diagnostic.Kind.NOTE, "");
    }

    public void postAllInit(List<ServiceHandle> handles) {
        if (DEBUG.equals(level) || INFO.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "load services: " + handles.stream().map(ServiceHandle::getTypeElement).map(TypeElement::getQualifiedName).map(Object::toString).collect(Collectors.toList()));
        }
    }

    public void postAllOverloading(OverloadingContext context) {
        if (DEBUG.equals(level)) {
            Set<String> msgSet = context.typeSet()
                    .stream()
                    .map(context::get)
                    .map(overloadContext -> "{\"type\": \"" + overloadContext.getTypeName() + "\", " +
                            "\"operator\": \"" + overloadContext.supportOperator().stream().sorted().map(t -> t.symbol).collect(Collectors.joining()) + "\", " +
                            "\"castTo\": [" + overloadContext.supportCast().stream().sorted().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) + "]}")
                    .collect(Collectors.toSet());
            messager.printMessage(Diagnostic.Kind.NOTE, "load overloads: " + msgSet);
        } else if (INFO.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "load overloads: " + context.typeSet());
        }
    }

    public void postAllConst(VariableSetContext context) {
        if (DEBUG.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "load const: " + context.values());
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "load const: " + context.values().stream().collect(Collectors.groupingBy(VariableContext::getDefineTypeName, Collectors.counting())));
        }
    }

    public void postSetup(ServiceHandle handle) {
        if (DEBUG.equals(level)) {
            StringBuilder msg = new StringBuilder("setup service: {")
                    .append("\"qualifiedName\": \"").append(handle.getQualifiedName()).append("\", ")
                    .append("\"numberType\": \"").append(handle.getNumberType()).append("\", ")
                    .append("\"docType\": \"").append(handle.getDocType()).append("\", ");
            String function = handle.getFunctionHandles().stream()
                    .map(this::getFunctionInfo)
                    .collect(Collectors.joining(","));
            msg.append("\"function\": [").append(function).append("]}");
            messager.printMessage(Diagnostic.Kind.NOTE, msg.toString(), handle.getTypeElement());
        } else if (INFO.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "setup service", handle.getTypeElement());
        }
    }

    private StringBuilder getFunctionInfo(FunctionHandle functionHandle) {
        return new StringBuilder()
                .append("{\"function\": \"").append(functionHandle.getElement().toString()).append("\", ")
                .append("\"exp\": \"").append(functionHandle.getExp()).append("\", ")
                .append("\"numberType\": \"").append(functionHandle.getNumberType()).append("\", ")
                .append("\"docType\": \"").append(functionHandle.getDocType()).append("\"")
                .append("}");
    }

    public void preWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {
        if (DEBUG.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "write code: \n" + serviceWriterContext.code(), handle.getTypeElement());
        }
    }

    public void postWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {
        if (INFO.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "write complete", handle.getTypeElement());
        }
    }
}
