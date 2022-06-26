package generator.processors.builders;

import generator.processors.EntityProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ServiceImplBuilder implements JavaSrcBuilder {

    private final MutableClassDeclaration entity;
    final TransformationContext context;
    final EntityProcessor entityProcessor;

    public ServiceImplBuilder(MutableClassDeclaration entity, TransformationContext context) {
        this.entity = entity;
        this.context = context;
        this.entityProcessor = null;
    }


    public ServiceImplBuilder(MutableClassDeclaration entity, TransformationContext context, EntityProcessor entityProcessor) {
        this.entity = entity;
        this.context = context;
        this.entityProcessor = entityProcessor;
    }

    @Override
    public void build() {

        final MutableInterfaceDeclaration serviceType = context.findInterface("generated.services." +entity.getSimpleName() + "Service");
        final MutableClassDeclaration serviceImplType = context.findClass("generated.services.Impl." + entity.getSimpleName() + "ServiceImpl");
        final MutableInterfaceDeclaration interfaceType = context.findInterface("generated.repositories." + entity.getSimpleName() + "Repository");
        final MutableClassDeclaration dtoClass = context.findClass("generated.entity.dto." + entity.getSimpleName() + "DTO");

        final TypeReference entityType = context.newSelfTypeReference(entity);
        final TypeReference optionalType = context.newTypeReference(Optional.class, entityType);
        final TypeReference listType = context.newTypeReference(List.class, entityType);
        final TypeReference optionalListType = context.newTypeReference(Optional.class, listType);

        serviceImplType.addAnnotation(context.newAnnotationReference(Service.class));

        TypeReference implementedType = context.newTypeReference(serviceType);

        serviceImplType.setImplementedInterfaces(Collections.unmodifiableList(Arrays.asList(implementedType)));

        serviceImplType.addField(entity.getSimpleName().toLowerCase() + "Repository", field -> {
            field.setType(context.newSelfTypeReference(interfaceType));
            field.addAnnotation(context.newAnnotationReference(Autowired.class));
        });

        serviceImplType.addMethod("get" + entity.getSimpleName(), method -> {
            method.addParameter("id", getKeyType());
            method.addAnnotation(context.newAnnotationReference(Override.class));
            method.setReturnType(optionalType);
            entityProcessor.createFindByIdMethodBody(method, entity);
        });

        serviceImplType.addMethod("getAll", method -> {
            method.addAnnotation(context.newAnnotationReference(Override.class));
            method.setReturnType(optionalListType);
            entityProcessor.createGetAllMethodBody(method, entity);
        });

        serviceImplType.addMethod("add" + entity.getSimpleName(), method -> {
            method.setReturnType(context.newTypeReference(Optional.class, entityType));
            method.addParameter(entity.getSimpleName().toLowerCase() + "DTO", context.newTypeReference(dtoClass));
            entityProcessor.createAddNewMethodBody(method, entity);
        });
    }

    private TypeReference getKeyType(){
        for (FieldDeclaration field: entity.getDeclaredFields()) {
            if (field.getSimpleName().contains("id"))
                return context.newTypeReference(field.getType().getType());
        }
        return context.newTypeReference(Object.class);
    }
}
