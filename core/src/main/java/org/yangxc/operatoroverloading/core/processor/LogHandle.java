package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.FunctionHandle;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.handle.service.VariableContext;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yangxc.operatoroverloading.core.processor.SpringBootHandle.SPRING_BOOT_TYPE;

public class LogHandle implements ProcessorHandle {

    public static final String OPERATOR_OVERLOADING_LOG = "OperatorOverloadingLog";
    private static final String NONE = "none";
    private static final String DEBUG = "debug";
    private static final String INFO = "info";

    private static Messager messager;
    private final String level;

    private static String gobleLevel = INFO;

    public LogHandle(Messager messager, String level) {
        LogHandle.messager = messager;
        level = level != null ? level : "info";
        if (Stream.of(NONE, INFO, DEBUG).noneMatch(level::equals)) {
            messager.printMessage(Diagnostic.Kind.WARNING, "unknown " + OPERATOR_OVERLOADING_LOG + ": " + level);
            level = INFO;
        }
        this.level = level;
        gobleLevel = level;
        showBanner();
    }

    private void showBanner() {
        if (NONE.equals(level)) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "------------------------[ OperatorOverloading ]------------------------");
        messager.printMessage(Diagnostic.Kind.NOTE, "issues反馈: https://github.com/Yang-xingchen/operator-overloading/issues");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -A" + OPERATOR_OVERLOADING_LOG + "=none 仅显示错误信息");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -A" + OPERATOR_OVERLOADING_LOG + "=info 显示基本信息");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -A" + OPERATOR_OVERLOADING_LOG + "=debug 显示详细信息");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -A" + SPRING_BOOT_TYPE + "=auto 自动判断");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -A" + SPRING_BOOT_TYPE + "=none 不使用spring自动注入");
        messager.printMessage(Diagnostic.Kind.NOTE, "使用 -A" + SPRING_BOOT_TYPE + "=config 使用@Configuration注解");
        messager.printMessage(Diagnostic.Kind.NOTE, "");
    }

    @Override
    public void postAllInit(List<ServiceHandle> handles) {
        if (DEBUG.equals(level) || INFO.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "load services: " + handles.stream().map(ServiceHandle::getTypeElement).map(TypeElement::getQualifiedName).map(Object::toString).collect(Collectors.toList()));
        }
    }

    @Override
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

    @Override
    public void postAllConst(VariableSetContext context) {
        if (DEBUG.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "load const: " + context.values());
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "load const: " + context.values().stream().collect(Collectors.groupingBy(VariableContext::getDefineTypeName, Collectors.counting())));
        }
    }

    @Override
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

    @Override
    public void preWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {
        if (DEBUG.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "write code: \n" + serviceWriterContext.code(), handle.getTypeElement());
        }
    }

    @Override
    public void postWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {
        if (INFO.equals(level)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "write complete", handle.getTypeElement());
        }
    }

    public static void print(Diagnostic.Kind kind, String msg) {
        if (DEBUG.equals(gobleLevel)) {
            messager.printMessage(kind, msg);
        } else if (INFO.equals(gobleLevel)) {
            if (kind != Diagnostic.Kind.NOTE) {
                messager.printMessage(kind, msg);
            }
        } else {
            if (kind == Diagnostic.Kind.ERROR) {
                messager.printMessage(kind, msg);
            }
        }
    }

    public static void print(Diagnostic.Kind kind, String msg, Element element) {
        if (DEBUG.equals(gobleLevel)) {
            messager.printMessage(kind, msg, element);
        } else if (INFO.equals(gobleLevel)) {
            if (kind != Diagnostic.Kind.NOTE) {
                messager.printMessage(kind, msg, element);
            }
        } else if (NONE.equals(gobleLevel)) {
            if (kind == Diagnostic.Kind.ERROR) {
                messager.printMessage(kind, msg, element);
            }
        }
    }

}
