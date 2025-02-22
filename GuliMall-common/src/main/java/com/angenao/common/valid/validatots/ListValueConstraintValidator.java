package com.angenao.common.valid.validatots;

import com.angenao.common.valid.annotaion.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
;import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: ListValueConstraintValidator
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/25 20:48
 * @Version: 1.0
 **/

public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {
    Set<Integer> set;

    @Override
    public void initialize(ListValue constraintAnnotation) {
        set = new HashSet<>();
        for (int val : constraintAnnotation.vals()) {
            set.add(val);
        }
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
