package generator.processors.builders;

import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;

import java.util.List;
import java.util.Optional;

public class ServiceBuilder implements JavaSrcBuilder {

    private final MutableClassDeclaration entity;
    final TransformationContext context;

    public ServiceBuilder(MutableClassDeclaration entity, TransformationContext context) {
        this.entity = entity;
        this.context = context;
    }

    @Override
    public void build() {
        final MutableInterfaceDeclaration serviceType = context.findInterface("generated.services." +entity.getSimpleName() + "Service");
        final MutableClassDeclaration dtoClass = context.findClass("generated.entity.dto." + entity.getSimpleName() + "DTO");
        final TypeReference entityType = context.newSelfTypeReference(entity);

        TypeReference listType = context.newTypeReference(List.class, entityType);

        serviceType.addMethod("getAll", method -> {
            method.setReturnType(context.newTypeReference(Optional.class, listType));
        });
        serviceType.addMethod("get" + entity.getSimpleName(), method -> {
            method.setReturnType(context.newTypeReference(Optional.class, entityType));
            method.addParameter("id", getKeyType());
        });
        serviceType.addMethod("add" + entity.getSimpleName(), method -> {
            method.setReturnType(context.newTypeReference(Optional.class, entityType));
            method.addParameter(entity.getSimpleName().toLowerCase() + "DTO", context.newTypeReference(dtoClass));
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
