package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.truenewx.data.validation.rule.ValidationRule;

/**
 * 校验规则构建器
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <R>
 *            校验规则类型
 */
public interface ValidationRuleBuilder<R extends ValidationRule> {

    /**
     * 获取支持的校验约束注解类型集
     * 
     * @return 支持的校验约束注解类型集
     */
    Class<?>[] getConstraintTypes();

    /**
     * 用指定校验约束注解中的数据更新指定校验规则
     * 
     * @param annotation
     *            校验约束注解
     * @param rule
     *            校验规则
     */
    void update(Annotation annotation, R rule);

    /**
     * 用指定校验约束注解创建校验规则
     * 
     * @param annotation
     *            校验约束注解
     * @return 校验规则
     */
    R create(Annotation annotation);
}
