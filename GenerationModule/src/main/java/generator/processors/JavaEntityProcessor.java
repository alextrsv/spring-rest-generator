package generator.processors;

import generator.processors.builders.*;
import org.eclipse.xtend.lib.annotations.ToString;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.ValidationContext;
import org.eclipse.xtend.lib.macro.declaration.*;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;


public class JavaEntityProcessor extends EntityProcessor {

	TypeReference entityIdKeyType = null;

	@Override
	public void doValidate(List<? extends ClassDeclaration> annotatedClasses, ValidationContext context) {
		super.doValidate(annotatedClasses, context);
		annotatedClasses.stream()
				.filter(declaration -> declaration.findAnnotation(context.findTypeGlobally(ToString.class)) == null)
				.forEach(declaration -> context.addWarning(declaration, "Ты не добавил аннотацию @ToString"));
	}

	@Override
	public void doValidate(final ClassDeclaration annotatedClass, final ValidationContext context) {
		super.doValidate(annotatedClass, context);
		IterableExtensions.forEach(annotatedClass.getTypeParameters(), typeParam -> {
			typeParam.getSimpleName();
		});
		for (FieldDeclaration field : annotatedClass.getDeclaredFields()) {
			if (hasAnnotation(field, context.findTypeGlobally(ManyToOne.class))
					&& hasAnnotation(field, context.findTypeGlobally(Column.class))) {
				context.addError(field, "@Column(s) not allowed on a @ManyToOne property, use @JoinColumn(s)");
			}
		}
	}


	@Override
	public void doRegisterGlobals(final ClassDeclaration annotatedClass, final RegisterGlobalsContext context) {
		context.registerInterface("generated.repositories."
				+ annotatedClass.getSimpleName() + "Repository");
		context.registerInterface("generated.services."
				+ annotatedClass.getSimpleName() + "Service");
		context.registerClass("generated.services.Impl."
				+ annotatedClass.getSimpleName() + "ServiceImpl");
		context.registerClass("generated.controllers."
				+ annotatedClass.getSimpleName() + "Controller");
		context.registerClass("generated.entity.dto."
				+ annotatedClass.getSimpleName() + "DTO");
	}

	@Override
	public void doTransform(MutableClassDeclaration entity, TransformationContext context) {
		super.doTransform(entity, context);
		entity.addAnnotation(context.newAnnotationReference(Entity.class));
		createClasses(entity, context);
	}


	private boolean hasAnnotation(FieldDeclaration field, Type findTypeGlobally) {
		return field.findAnnotation(findTypeGlobally) != null;
	}

	private boolean isUnique(MutableFieldDeclaration field, TransformationContext context) {
		AnnotationReference columnAnnotation = field.findAnnotation(context.findTypeGlobally(Column.class));
		return columnAnnotation != null && columnAnnotation.getBooleanValue("unique");
	}

	private boolean hasAnyJPAAnnotation(MutableFieldDeclaration field, final TransformationContext context ){

		return (hasAnnotation(field, context.findTypeGlobally(ManyToOne.class))
				|| hasAnnotation(field, context.findTypeGlobally(ManyToMany.class))
				|| hasAnnotation(field, context.findTypeGlobally(OneToMany.class))
				|| hasAnnotation(field, context.findTypeGlobally(OneToOne.class))
				|| hasAnnotation(field, context.findTypeGlobally(Column.class))
				|| hasAnnotation(field, context.findTypeGlobally(Id.class)));
	}


	private boolean isInCustomType(TransformationContext context, TypeReference type){
		MutableClassDeclaration serchedClass = null;
		serchedClass = context.findClass("generated." + type.toString());
		return serchedClass != null;
	}

	private boolean isInCollection(TransformationContext context, TypeReference type){
		return type.getSimpleName().contains("List")
				|| type.getSimpleName().contains("Collection");
	}


	private void createClasses(final MutableClassDeclaration entity, final TransformationContext context){
		List<JavaSrcBuilder> builders =
				Arrays.asList(new EntityBuilder(entity, context),
						new DTOBuilder(entity, context),
						new RepositoryBuilder(entity, context),
						new ServiceBuilder(entity, context),
						new ServiceImplBuilder(entity, context, new EntityProcessor()),
						new ControllerBuilder(entity, context, new EntityProcessor()));

		builders.forEach(JavaSrcBuilder::build);
	}
}
