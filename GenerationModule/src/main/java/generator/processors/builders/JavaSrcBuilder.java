package generator.processors.builders;

import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.*;

import javax.persistence.*;

public interface JavaSrcBuilder {

    void build();

    default boolean hasAnnotation(FieldDeclaration field, Type findTypeGlobally) {
        return field.findAnnotation(findTypeGlobally) != null;
    }

    default boolean isUnique(MutableFieldDeclaration field, TransformationContext context) {
        AnnotationReference columnAnnotation = field.findAnnotation(context.findTypeGlobally(Column.class));
        return columnAnnotation != null && columnAnnotation.getBooleanValue("unique");
    }

    default boolean hasAnyJPAAnnotation(MutableFieldDeclaration field, final TransformationContext context ){

        return (hasAnnotation(field, context.findTypeGlobally(ManyToOne.class))
                || hasAnnotation(field, context.findTypeGlobally(ManyToMany.class))
                || hasAnnotation(field, context.findTypeGlobally(OneToMany.class))
                || hasAnnotation(field, context.findTypeGlobally(OneToOne.class))
                || hasAnnotation(field, context.findTypeGlobally(Column.class))
                || hasAnnotation(field, context.findTypeGlobally(Id.class)));
    }


    default boolean isInCustomType(TransformationContext context, TypeReference type){
        MutableClassDeclaration serchedClass = null;
        serchedClass = context.findClass("generated." + type.toString());
        return serchedClass != null;
    }

    default boolean isInCollection(TransformationContext context, TypeReference type){
        return type.getSimpleName().contains("List")
                || type.getSimpleName().contains("Collection");
    }

}
