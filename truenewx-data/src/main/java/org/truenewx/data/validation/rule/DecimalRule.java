package org.truenewx.data.validation.rule;

import java.math.BigDecimal;

/**
 * 十进制数规则
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class DecimalRule extends RangeRule<BigDecimal> {
    /**
     * 最小的十进制数.
     */
    public static final BigDecimal MIN_DECIMAL = BigDecimal.valueOf(-Double.MAX_VALUE);
    /**
     * 最大的十进制数.
     */
    public static final BigDecimal MAX_DECIMAL = BigDecimal.valueOf(Double.MAX_VALUE);

    /**
     * 数值最大长度
     */
    private int precision = 0;
    /**
     * 小数部分精度
     */
    private int scale = 0;

    public DecimalRule() {
        super(MIN_DECIMAL, MAX_DECIMAL);
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(final int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(final int scale) {
        this.scale = scale;
    }
}
