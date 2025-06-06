package org.imtp.api.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/6 16:10
 */
public class OneNotBlankValidator implements ConstraintValidator<OneNotBlank,Object> {

    private String[] fields;

    @Override
    public void initialize(OneNotBlank constraintAnnotation) {
        this.fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if (object == null){
            return true;
        }
        if(fields == null || fields.length == 0){
            return true;
        }
        for (String field : fields) {
            try {
                Field declaredField = object.getClass().getDeclaredField(field);
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);
                if (value != null && !value.toString().trim().isEmpty()) {
                    return true; // 至少有一个字段不为空
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // 如果字段不存在或无法访问，忽略该字段
            }
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fields[0])
                .addConstraintViolation();
        return false;
    }
}
