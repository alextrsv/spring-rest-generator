package generator.processors.builders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend2.lib.StringConcatenationClient;

import javax.persistence.*;

public class EntityBuilder implements JavaSrcBuilder {

        private final MutableClassDeclaration entity;
        final TransformationContext context;
    MutableClassDeclaration dtoClass;

    public EntityBuilder(MutableClassDeclaration entity, TransformationContext context) {
        this.entity = entity;
        this.context = context;
    }

    @Override
    public void build() {

        dtoClass = context.findClass("generated.entity.dto." + entity.getSimpleName() + "DTO");

        setClassLevelAnnotations();
        setFieldLevelAnnotations();
        createConstructor();

    }

    private void setClassLevelAnnotations() {
        entity.getAnnotations().forEach(entity::removeAnnotation);

        entity.addAnnotation(context.newAnnotationReference(Data.class));
        entity.addAnnotation(context.newAnnotationReference(NoArgsConstructor.class));
        entity.addAnnotation(context.newAnnotationReference(AllArgsConstructor.class));
        entity.addAnnotation(context.newAnnotationReference(Entity.class));
        entity.addAnnotation(context.newAnnotationReference(Table.class, annotation -> {
            annotation.set("name", entity.getSimpleName().toLowerCase()); })
        );
    }


    private void setFieldLevelAnnotations() {
        //добавление аннотаций на сгенерированный исходник из JaxB
        entity.getDeclaredFields().forEach(field -> {

            if (isInCollection(context, field.getType()) && !hasAnyJPAAnnotation(field, context)){

                //Вытаскиваем Тип коллекции/листа
                TypeReference dependType = field.getType().getActualTypeArguments().get(0);
                //Ищем класс этого типа
                MutableClassDeclaration dependClass = context.findClass("generated." + dependType.toString());

                //пробегаем по полям вытащенного типа и смотрим, есть ли там интересующие нас
                dependClass.getDeclaredFields().forEach(depField -> {
                    if (depField.getType().getSimpleName().equals(entity.getSimpleName())){ //ManyToOne-OneToMany
                        depField.getAnnotations().forEach(field::removeAnnotation);

                        depField.addAnnotation(context.newAnnotationReference(ManyToOne.class, annotation ->{
                            annotation.setBooleanValue("optional", true);
                        }));
                        depField.addAnnotation(context.newAnnotationReference(JoinColumn.class, annotation ->{
                            annotation.setStringValue("name", entity.getSimpleName().toLowerCase() + "_id");
                        }));

                        field.addAnnotation(context.newAnnotationReference(OneToMany.class, annotation ->{
                            annotation.setStringValue("mappedBy", depField.getSimpleName());
                        }));
                    }
                    else if (depField.getType().getSimpleName().contains(entity.getSimpleName())
                            && depField.getType().getSimpleName().contains("List")){

                        depField.addAnnotation(context.newAnnotationReference(ManyToMany.class, annotation ->{
                            annotation.setStringValue("mappedBy", field.getSimpleName());
                        }));

                        field.addAnnotation(context.newAnnotationReference(ManyToMany.class));
                        field.addAnnotation(context.newAnnotationReference(JoinTable.class, annotation ->{
                            annotation.setStringValue("name", entity.getSimpleName().toLowerCase() + "_to_"
                                    + dependClass.getSimpleName().toLowerCase());
                            annotation.setAnnotationValue("joinColumns", context.newAnnotationReference(JoinColumn.class, argAnnotation -> {
                                argAnnotation.setStringValue("name", entity.getSimpleName().toLowerCase() + "_id");
                            }));
                            annotation.setAnnotationValue("inverseJoinColumns", context.newAnnotationReference(JoinColumn.class, argAnnotation -> {
                                argAnnotation.setStringValue("name", dependClass.getSimpleName().toLowerCase() + "_id");
                            }));
                        }));
                    }
                });

            }
            else if (field.getSimpleName().equals("id") || field.getSimpleName().equals("Id")
                    || field.getSimpleName().equals("ID")){
                field.addAnnotation(context.newAnnotationReference(Id.class));
            }
            else if (!hasAnnotation(field, context.findTypeGlobally(OneToMany.class))
                    && !hasAnnotation(field, context.findTypeGlobally(OneToOne.class))
                    && !hasAnnotation(field, context.findTypeGlobally(ManyToOne.class))
                    && !hasAnnotation(field, context.findTypeGlobally(ManyToMany.class))
                    && !hasAnnotation(field, context.findTypeGlobally(JoinColumn.class))) {
                if (!isInCustomType(context, field.getType())) {
                    System.out.println("CLASS: "+ entity.getSimpleName() + ",      FIELD: " + field.getSimpleName());
                    field.addAnnotation(context.newAnnotationReference(Column.class, annotation -> {
                                annotation.set("name", field.getSimpleName().toLowerCase());
                            })
                    );
                }
            }
        });


        entity.getDeclaredFields().forEach(field -> {
            if (hasAnnotation(field, context.findTypeGlobally(OneToOne.class)) ||
                    hasAnnotation(field, context.findTypeGlobally(OneToMany.class)) ||
                    hasAnnotation(field, context.findTypeGlobally(ManyToOne.class)) ||
                    hasAnnotation(field, context.findTypeGlobally(ManyToMany.class))) {
                field.addAnnotation(context.newAnnotationReference(JsonIgnore.class));
            }
        });

    }

    private void createConstructor() {
        entity.addConstructor(constructor -> {
            constructor.addParameter(entity.getSimpleName().toLowerCase() + "DTO", context.newTypeReference(dtoClass));

            StringConcatenationClient _client = new StringConcatenationClient() {
                @Override
                protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
                    entity.getDeclaredFields().forEach(field -> {
                        if (field.findAnnotation(context.findTypeGlobally(Transient.class)) == null &&
                                field.findAnnotation(context.findTypeGlobally(Id.class)) == null) {
                            if (field.getType().getSimpleName().contains("List") || field.getType().getSimpleName().contains("list")
                                    || field.getType().getSimpleName().contains("Collection")){
                                _builder.append("this." + field.getSimpleName() + " = new ArrayList<>(" + entity.getSimpleName().toLowerCase() + "DTO.get"
                                        + field.getSimpleName().toUpperCase().charAt(0) + field.getSimpleName().substring(1) + "());");
                                _builder.newLineIfNotEmpty();
                            }else {
                                _builder.append("this." + field.getSimpleName() + " = " + entity.getSimpleName().toLowerCase() + "DTO.get"
                                        + field.getSimpleName().toUpperCase().charAt(0) + field.getSimpleName().substring(1) + "();");
                                _builder.newLineIfNotEmpty();
                            }
                        }
                    });
                }
            };
            constructor.setBody(_client);
        });
    }


}
