package generator.processors.builders;

import generator.processors.EntityProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class ControllerBuilder implements JavaSrcBuilder {

    private final MutableClassDeclaration entity;
    final TransformationContext context;
    final EntityProcessor entityProcessor;

    public ControllerBuilder(MutableClassDeclaration entity, TransformationContext context) {
        this.entity = entity;
        this.context = context;
        this.entityProcessor = null;
    }

    public ControllerBuilder(MutableClassDeclaration entity, TransformationContext context, EntityProcessor entityProcessor) {
        this.entity = entity;
        this.context = context;
        this.entityProcessor = entityProcessor;
    }

    @Override
    public void build() {
        final MutableInterfaceDeclaration service = context.findInterface("generated.services." + entity.getSimpleName() + "Service");
        final MutableClassDeclaration controller = context.findClass("generated.controllers." + entity.getSimpleName() + "Controller");
        final MutableClassDeclaration dtoClass = context.findClass("generated.entity.dto." + entity.getSimpleName() + "DTO");

        final TypeReference entityType = context.newSelfTypeReference(entity);

        final TypeReference responseType = context.newTypeReference(ResponseEntity.class, entityType);
        final TypeReference listType = context.newTypeReference(List.class, entityType);
        final TypeReference listResponseType = context.newTypeReference(ResponseEntity.class, listType);

        controller.addAnnotation(context.newAnnotationReference(RestController.class));
        controller.addAnnotation(context.newAnnotationReference(RequestMapping.class, tr -> {
            tr.setStringValue("path", "/" + entity.getSimpleName().toLowerCase());
        }));

        controller.addField(entity.getSimpleName().toLowerCase() + "Service", field -> {
            field.setType(context.newSelfTypeReference(service));
            field.addAnnotation(context.newAnnotationReference(Autowired.class));
        });


        controller.addMethod("get" + entity.getSimpleName(), method -> {
            method.addParameter("id", getKeyType())
                    .addAnnotation(context.newAnnotationReference(PathVariable.class));
            method.addAnnotation(context.newAnnotationReference(GetMapping.class, tr -> {
                tr.setStringValue( "path", "/{id}");
            }));
            method.setReturnType(responseType);
            entityProcessor.createGetEntityMethodBody(method, entity);
        });

        controller.addMethod("getAll", method -> {
            method.addAnnotation(context.newAnnotationReference(GetMapping.class));
            method.setReturnType(listResponseType);
            entityProcessor.createCONTROLLERGetAllBody(method,  entity);
        });

        controller.addMethod("addNew" + entity.getSimpleName(), method -> {
            method.addParameter(entity.getSimpleName().toLowerCase() + "DTO", context.newTypeReference(dtoClass))
                    .addAnnotation(context.newAnnotationReference(RequestBody.class));
            method.addAnnotation(context.newAnnotationReference(PostMapping.class));
            method.setReturnType(responseType);
            entityProcessor.createCONTROLLERAddNewBody(method, entity.getSimpleName().toLowerCase() + "DTO", entity);
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
