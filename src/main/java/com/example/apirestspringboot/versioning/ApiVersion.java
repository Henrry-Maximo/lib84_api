package com.example.apirestspringboot.versioning;

import java.lang.annotation.*;

/**
 * Marks a controller or method as belonging to a specific API version.
 * Evaluated by {@link ApiVersionInterceptor} via the X-API-Version request header.
 * When absent on a controller, the default version (1) is assumed.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {
    int[] value();
}
