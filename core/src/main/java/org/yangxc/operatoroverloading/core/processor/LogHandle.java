package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.handle.overloading.ClassOverloadingContext;
import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.FunctionHandle;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LogHandle {

    private final Messager messager;
    private final String level;

    public static final String NONE = "none";
    public static final String DEBUG = "debug";
    public static final String INFO = "info";

    public LogHandle(Messager messager, String level) {
        this.messager = messager;
        level = level != null ? level : "info";
        if (!Set.of(NONE, INFO, DEBUG).contains(level)) {
            messager.printWarning("unknown OperatorOverloadingLog: " + level);
            level = INFO;
        }
        this.level = level;
        showBanner();
    }

    private void showBanner() {
        if (NONE.equals(level)) {
            return;
        }
        messager.printNote("------------------------[ OperatorOverloading ]------------------------");
        messager.printNote("bug反馈: https://github.com/Yang-xingchen/operator-overloading");
        messager.printNote("使用 -AOperatorOverloadingLog=none 仅显示错误信息");
        messager.printNote("使用 -AOperatorOverloadingLog=info 显示基本信息");
        messager.printNote("使用 -AOperatorOverloadingLog=debug 显示详细信息");
        messager.printNote("");
    }

    public void postAllInit(List<ServiceHandle> handles) {
        if (DEBUG.equals(level) || INFO.equals(level)) {
            messager.printNote("load services: " + handles.stream().map(ServiceHandle::getTypeElement).map(TypeElement::getQualifiedName).map(Object::toString).toList());
        }
    }

    public void postOverloading(TypeElement typeElement, ClassOverloadingContext context) {
        if (DEBUG.equals(level)) {
            StringBuilder msg = new StringBuilder("find overloading type: {")
                    .append("\"qualifiedName\": \"").append(context.getTypeName()).append("\", ")
                    .append("\"supportOperator\": [").append(context.supportOperator().stream().map(op -> "\"" + op.name() + "\"").collect(Collectors.joining(", "))).append("],")
                    .append("\"supportCast\": [").append(context.supportCast().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "))).append("]")
                    .append("}");
            messager.printNote(msg.toString(), typeElement);
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
            messager.printNote("load overloads: " + msgSet);
        } else if (INFO.equals(level)) {
            messager.printNote("load overloads: " + context.typeSet());
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
            messager.printNote(msg.toString(), handle.getTypeElement());
        } else if (INFO.equals(level)) {
            messager.printNote("setup service", handle.getTypeElement());
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
            messager.printNote("write code: \n" + serviceWriterContext.code(), handle.getTypeElement());
        }
    }

    public void postWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {
        if (INFO.equals(level)) {
            messager.printNote("write complete", handle.getTypeElement());
        }
    }

}
