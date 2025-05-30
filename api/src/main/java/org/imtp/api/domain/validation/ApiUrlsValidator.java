package org.imtp.api.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/18 13:26
 */
public class ApiUrlsValidator implements ConstraintValidator<ValidApiUrls, String> {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^/[\\w\\-./{}:+*\\[\\]()\\\\]+$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.trim().isEmpty()){
            // 交给其他校验
            return true;
        }
        return URL_PATTERN.matcher(value).matches();
    }
}
