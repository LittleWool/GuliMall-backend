package com.angenao.common.valid.annotaion;

import com.angenao.common.valid.validatots.ListValueConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @ClassName: ListValue
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/25 20:42
 * @Version: 1.0
 **/


@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {

    String message() default "{com.angenao.common.validator.annotaion.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals () default {};
}
