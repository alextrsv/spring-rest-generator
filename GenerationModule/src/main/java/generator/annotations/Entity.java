package generator.annotations;

import generator.processors.JavaEntityProcessor;
import org.eclipse.xtend.lib.macro.Active;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Active(JavaEntityProcessor.class)
public @interface Entity {
}

