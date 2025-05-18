package org.imtp.api.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ApiUrlsValidator.class)
@Documented
public @interface ValidApiUrls {

    String message() default "资源路径规则格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
