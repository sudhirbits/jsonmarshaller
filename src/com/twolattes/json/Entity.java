package com.twolattes.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a JSON entity.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface Entity {
  /**
   * Whether this entity should always be inline. An entity can be inline
   * only if it has a single {@link Value}. If an entity should sometimes be
   * inline and sometimes not, use {@link Value#inline} instead.
   */
  boolean inline() default false;

  /**
   * Specifies the implementation class of an annotated interface. This is used
   * during unmarshalling to create an instance of an object implementing the
   * annotated interface.
   */
  Class<?> implementedBy() default Object.class;

  /**
   * List of subclasses of an entity. All the subclasses will be marshallable.
   * This option must be used in conjunction with {@link #discriminatorName()}.
   */
  Class<?>[] subclasses() default {};

  /**
   * The discriminator name. This option must be used in conjunction with
   * {@link #subclasses()}.
   */
  String discriminatorName() default "";

  /**
   * The value of the discriminator for this entity. This value is used if the
   * entity is mentioned in a parent's {@link #subclasses()} option.
   */
  String discriminator() default "";

  /**
   * Whether to embed the value or not.
   */
  boolean embed() default false;
}
