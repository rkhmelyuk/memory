package com.khmelyuk.memory.annotation;

import java.lang.annotation.*;

/**
 * Marked method or class is thread-safe.
 *
 * @author Ruslan Khmelyuk
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafe {
}
