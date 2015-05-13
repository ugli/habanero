package se.ugli.habanero.j.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface HabaneroTestConfig {

	static final String NO_RESOURCE = "NO_RESOURCE";

	String dataset() default NO_RESOURCE;

	String schema() default NO_RESOURCE;

}
