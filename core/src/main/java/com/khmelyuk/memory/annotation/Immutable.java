package com.khmelyuk.memory.annotation;

import java.lang.annotation.*;

/**
 * Marked class is immutable.
 *
 * @author Ruslan Khmelyuk
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Immutable {
}
