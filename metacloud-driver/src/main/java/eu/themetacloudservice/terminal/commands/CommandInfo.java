package eu.themetacloudservice.terminal.commands;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    String command();
    String ENdescription() default "";
    String DEdescription() default "";
    String[] aliases() default {};

}