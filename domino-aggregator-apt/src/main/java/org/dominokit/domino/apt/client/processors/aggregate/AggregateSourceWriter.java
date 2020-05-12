package org.dominokit.domino.apt.client.processors.aggregate;

import com.squareup.javapoet.*;
import org.dominokit.domino.api.shared.extension.Aggregate;
import org.dominokit.domino.api.shared.extension.ContextAggregator;
import org.dominokit.domino.apt.commons.AbstractSourceBuilder;
import org.dominokit.domino.apt.commons.DominoTypeBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AggregateSourceWriter extends AbstractSourceBuilder {

    private final ExecutableElement methodElement;
    private final Element enclosingElement;

    public AggregateSourceWriter(ExecutableElement methodElement, ProcessingEnvironment processingEnv) {
        super(processingEnv);
        this.methodElement = methodElement;
        this.enclosingElement = methodElement.getEnclosingElement();
    }

    @Override
    public List<TypeSpec.Builder> asTypeBuilder() {
        String aggregateClassName = methodElement.getAnnotation(Aggregate.class).name();
        if (aggregateClassName.trim().isEmpty()) {
            aggregateClassName = processorUtil.capitalizeFirstLetter(methodElement.getSimpleName().toString());
        }

        TypeSpec.Builder aggregateType = DominoTypeBuilder.classBuilder(aggregateClassName, AggregateProcessor.class)
                .addModifiers(Modifier.PUBLIC);

        List<? extends VariableElement> parameters = methodElement.getParameters();
        parameters.forEach(p -> {
            aggregateType.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(ContextAggregator.ContextWait.class), TypeName.get(p.asType())), p.getSimpleName().toString() + "Context", Modifier.PRIVATE)
                    .initializer("$T.create()", TypeName.get(ContextAggregator.ContextWait.class))
                    .build());
        });

        aggregateType.addField(FieldSpec.builder(TypeName.get(ContextAggregator.class), "contextAggregator", Modifier.PRIVATE).build());
        aggregateType.addField(FieldSpec.builder(wildcardTypeNameIfGeneric(), "target", Modifier.PRIVATE).build());

        aggregateType.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("contextAggregator = ContextAggregator.waitFor($T.asList(" + getEventsNames(parameters) + "))\n" +
                        "                .onReady(() -> {\n" +
                        "                    target." + methodElement.getSimpleName().toString() + "(" + getEventsNamesGetters(parameters) + ");\n" +
                        "                })", TypeName.get(Arrays.class))
                .build());

        aggregateType.addMethod(MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(wildcardTypeNameIfGeneric(), "target")
                .returns(ClassName.get(elements.getPackageOf(enclosingElement).toString(), aggregateClassName))
                .addStatement("this.target = target")
                .addStatement("return this")
                .build());

        parameters.forEach(parameter -> {
            aggregateType.addMethod(MethodSpec.methodBuilder("complete" + processorUtil.capitalizeFirstLetter(parameter.getSimpleName().toString()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(TypeName.get(parameter.asType()), "value")
                    .addStatement("" + parameter.getSimpleName().toString() + "Context.complete(value)")

                    .build());

            aggregateType.addMethod(MethodSpec.methodBuilder("reset" + processorUtil.capitalizeFirstLetter(parameter.getSimpleName().toString()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addStatement("contextAggregator.resetContext($L)", parameter.getSimpleName().toString() + "Context")
                    .build());
        });

        MethodSpec.Builder resetMethod = MethodSpec.methodBuilder("reset")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        parameters.forEach(parameter -> resetMethod.addStatement("$L()", "reset" + processorUtil.capitalizeFirstLetter(parameter.getSimpleName().toString())));

        aggregateType.addMethod(resetMethod.build());

        return Collections.singletonList(aggregateType);
    }

    private TypeName wildcardTypeNameIfGeneric() {
        TypeName typeName = ClassName.get(enclosingElement.asType());
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            typeName = ParameterizedTypeName.get(ClassName.get((TypeElement) enclosingElement), parameterizedTypeName.typeArguments.stream()
                    .map(typeName1 -> TypeVariableName.get("?")).toArray(TypeName[]::new));
        }
        return typeName;
    }

    private String getEventsNames(List<? extends VariableElement> parameters) {
        return parameters.stream().map(p -> p.getSimpleName().toString() + "Context")
                .collect(Collectors.joining(","));
    }

    private String getEventsNamesGetters(List<? extends VariableElement> parameters) {
        return parameters.stream().map(p -> p.getSimpleName().toString() + "Context.get()")
                .collect(Collectors.joining(","));
    }
}
