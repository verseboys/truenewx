package org.truenewx.core.functor.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.truenewx.core.functor.BinatePredicate;

/**
 * 断言：对象相等
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PredEqual extends BinatePredicate<Object, Object> {
    /**
     * 单例
     */
    public static final PredEqual INSTANCE = new PredEqual();

    /**
     * 私有构造函数
     */
    private PredEqual() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean apply(final Object left, final Object right) {
        if (left == right) {
            return true;
        }
        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }
        if (left instanceof Collection && right instanceof Collection) {
            return CollectionUtils.isEqualCollection((Collection) left, (Collection) right);
        }
        if (left instanceof Map && right instanceof Map) {
            return testAsMap((Map<?, ?>) left, (Map<?, ?>) right);
        }
        if (left.getClass().isArray() && right.getClass().isArray()) {
            return testAsArray(left, right);
        }
        if (left.getClass() != right.getClass()) {
            return false;
        }
        return left.equals(right);
    }

    private boolean testAsArray(final Object left, final Object right) {
        if (left instanceof int[]) {
            return Arrays.equals((int[]) left, (int[]) right);
        } else if (left instanceof long[]) {
            return Arrays.equals((long[]) left, (long[]) right);
        } else if (left instanceof char[]) {
            return Arrays.equals((char[]) left, (char[]) right);
        } else if (left instanceof short[]) {
            return Arrays.equals((short[]) left, (short[]) right);
        } else if (left instanceof byte[]) {
            return Arrays.equals((byte[]) left, (byte[]) right);
        } else if (left instanceof boolean[]) {
            return Arrays.equals((boolean[]) left, (boolean[]) right);
        } else if (left instanceof double[]) {
            return Arrays.equals((double[]) left, (double[]) right);
        } else if (left instanceof float[]) {
            return Arrays.equals((float[]) left, (float[]) right);
        } else {
            return Arrays.equals((Object[]) left, (Object[]) right);
        }
    }

    private boolean testAsMap(final Map<?, ?> left, final Map<?, ?> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (final Entry<?, ?> entry : left.entrySet()) {
            final Object key = entry.getKey();
            if (!apply(entry.getValue(), right.get(key))) {
                return false;
            }
        }
        return true;
    }

}
