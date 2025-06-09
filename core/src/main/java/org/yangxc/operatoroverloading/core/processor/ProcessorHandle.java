package org.yangxc.operatoroverloading.core.processor;

import org.yangxc.operatoroverloading.core.handle.overloading.OverloadingContext;
import org.yangxc.operatoroverloading.core.handle.service.ServiceHandle;
import org.yangxc.operatoroverloading.core.handle.service.VariableSetContext;
import org.yangxc.operatoroverloading.core.handle.writer.ServiceWriterContext;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

public interface ProcessorHandle {

    default void startRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {}

    default void postAllInit(List<ServiceHandle> handles) {}

    default void postAllOverloading(OverloadingContext context) {}

    default void postAllConst(VariableSetContext context) {}

    default void preAllSetup(List<ServiceHandle> serviceHandles, OverloadingContext overloadingContext, VariableSetContext variableSetContext) {}

    default void postSetup(ServiceHandle handle) {}

    default void postAllSetup(List<ServiceHandle> serviceHandles, OverloadingContext overloadingContext, VariableSetContext variableSetContext) {}

    default void preWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {}

    default void postWrite(ServiceHandle handle, ServiceWriterContext serviceWriterContext) {}

    default void postAllWrite(List<ServiceHandle> serviceHandles, OverloadingContext overloadingContext, VariableSetContext variableSetContext) {}

}
