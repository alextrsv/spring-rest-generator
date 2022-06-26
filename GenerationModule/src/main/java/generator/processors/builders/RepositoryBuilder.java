package generator.processors.builders;

import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RepositoryBuilder implements JavaSrcBuilder {

    private final MutableClassDeclaration entity;
    final TransformationContext context;

    public RepositoryBuilder(MutableClassDeclaration entity, TransformationContext context) {
        this.entity = entity;
        this.context = context;
    }

    @Override
    public void build() {
        final MutableInterfaceDeclaration repositoryType = context.findInterface("generated.repositories." +entity.getSimpleName() + "Repository");

        TypeReference entityIdKeyType = null;
        context.setPrimarySourceElement(repositoryType, entity);

        final TypeReference entityType = context.newSelfTypeReference(entity);

        for (FieldDeclaration field: entity.getDeclaredFields()) {
            if (field.getSimpleName().contains("id"))
                entityIdKeyType = context.newTypeReference(field.getType().getType());
        }

        repositoryType.addAnnotation(context.newAnnotationReference(Repository.class));

        TypeReference repositoryInterfaceTypeReference = context.newTypeReference(
                CrudRepository.class,
                entityType,
                entityIdKeyType);

        repositoryType.setExtendedInterfaces(Collections.unmodifiableList(Arrays.asList(repositoryInterfaceTypeReference)));

        IterableExtensions.filter(entity.getDeclaredFields(), field -> {
            return !field.isTransient() && !"id".equals(field.getSimpleName()) && !isInCustomType(context, field.getType())
                    && !isInCollection(context, field.getType());
        }).forEach(field -> {
            repositoryType.addMethod("findBy" + StringExtensions.toFirstUpper(field.getSimpleName()), method -> {
                method.setReturnType(isUnique(field, context) ?
                        entityType :
                        context.newTypeReference(List.class, entityType));
                method.addParameter(field.getSimpleName(), field.getType())
                        .addAnnotation(context.newAnnotationReference(Param.class, annotation -> {
                            annotation.set("value", field.getSimpleName());
                        }));
            });
        });
    }
}
