package generator.processors.builders;

import lombok.Data;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;

public class DTOBuilder implements JavaSrcBuilder {

    private final MutableClassDeclaration entity;
    final TransformationContext context;

    public DTOBuilder(MutableClassDeclaration entity, TransformationContext context) {
        this.entity = entity;
        this.context = context;
    }

    @Override
    public void build() {
        final MutableClassDeclaration dtoClass = context.findClass("generated.entity.dto." + entity.getSimpleName() + "DTO");

        dtoClass.addAnnotation(context.newAnnotationReference(Data.class));

        entity.getDeclaredFields().forEach(field -> {
            dtoClass.addField(field.getSimpleName(), dtoField -> {
                dtoField.setType(field.getType());
            });
        });
    }
}
