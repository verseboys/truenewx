package org.truenewx.core.net;

import java.net.InetAddress;

import org.springframework.util.Assert;
import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.util.NetUtil;

/**
 * IP地址段
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class InetAddressRange<T extends InetAddress> {
    private T begin;
    private T end;

    public InetAddressRange(final T address) {
        Assert.notNull(address);
        this.begin = address;
        this.end = address;
    }

    public InetAddressRange(final T begin, final T end) {
        Assert.isTrue(begin.getClass() == end.getClass()); // 必须同时都为IPv4，或同时都为IPv6
        // 确保起始地址小于等于结束地址
        if (NetUtil.intValueOf(begin) <= NetUtil.intValueOf(end)) {
            this.begin = begin;
            this.end = end;
        } else {
            this.begin = end;
            this.end = begin;
        }
    }

    public T getBegin() {
        return this.begin;
    }

    private int getBeginValue() {
        return NetUtil.intValueOf(this.begin);
    }

    public T getEnd() {
        return this.end;
    }

    private int getEndValue() {
        return NetUtil.intValueOf(this.end);
    }

    @SuppressWarnings("unchecked")
    public Class<T> getAddressClass() {
        return (Class<T>) this.begin.getClass();
    }

    /**
     * 判断是否包含指定IP地址
     *
     * @param address
     *            IP地址
     * @return true if 包含指定IP地址, otherwise false
     */
    public boolean contains(final InetAddress address) {
        if (address.getClass() != getAddressClass()) {
            return false;
        }
        final int value = NetUtil.intValueOf(address);
        return getBeginValue() <= value && value <= getEndValue();
    }

    /**
     * 判断指定IP地址是否可加入到当前段内
     *
     * @param address
     *            要加入的IP地址
     * @return 指定IP地址是否可加入到当前段内
     */
    public boolean isAddable(final InetAddress address) {
        if (address.getClass() != getAddressClass()) {
            return false;
        }
        final int value = NetUtil.intValueOf(address);
        return getBeginValue() - 1 <= value && value <= getEndValue() + 1;
    }

    /**
     * 判断指定IP地址段是否可加入到当前段中
     *
     * @param range
     *            要加入的IP地址段
     * @return 指定IP地址段是否可加入到当前段中
     */
    public boolean isAddable(final InetAddressRange<?> range) {
        if (range.getAddressClass() != getAddressClass()) {
            return false;
        }
        // 前后只要有一个是可加入的，则整个段都是可加入的
        return isAddable(range.getBegin()) || isAddable(range.getEnd());
    }

    /**
     * 将指定IP地址加入到当前段中
     *
     * @param address
     *            要加入的IP地址
     * @return 是否加入成功，不可加入时返回false
     */
    @SuppressWarnings("unchecked")
    public boolean add(final InetAddress address) {
        if (address.getClass() != getAddressClass()) {
            return false;
        }
        final int value = NetUtil.intValueOf(address);
        final int beginValue = getBeginValue();
        // 已经包含在段内的地址不需要添加，不可添加则不添加
        if (value == beginValue - 1) { // 刚好比起始地址小一个数，则把加入的地址作为新的起始地址
            this.begin = (T) address;
            return true;
        }
        final int endValue = getEndValue();
        if (value == endValue + 1) { // 刚好比结束地址大一个数，则把加入的地址作为新的结束地址
            this.end = (T) address;
            return true;
        }
        // 已经包含的尽管未进行添加动作，但仍视为添加成功
        return beginValue <= value && value <= endValue;
    }

    /**
     * 将指定IP地址段加入到当前段中
     *
     * @param range
     *            要加入的IP地址段
     * @return 是否加入成功，不可加入时返回false
     */
    @SuppressWarnings("unchecked")
    public boolean add(final InetAddressRange<?> range) {
        if (range.getAddressClass() != getAddressClass()) {
            return false;
        }
        final int thisBeginValue = getBeginValue();
        final int thisEndValue = getEndValue();
        final int addBeginValue = range.getBeginValue();
        final int addEndValue = range.getEndValue();
        if (addEndValue < thisBeginValue - 1 || thisEndValue + 1 < addBeginValue) { // 超出了可加入的范围
            return false;
        }
        // 要添加的起始地址小于当前起始地址，则要添加的起始地址作为新的起始地址
        if (addBeginValue < thisBeginValue) {
            this.begin = (T) range.getBegin();
        }
        // 要添加的结束地址大于当前结束地址，则要添加的结束地址作为新的结束地址
        if (thisEndValue < addEndValue) {
            this.end = (T) range.getEnd();
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final InetAddressRange<T> other = (InetAddressRange<T>) obj;
        return this.begin.equals(other.begin) && this.end.equals(other.end);
    }

    @Override
    public int hashCode() {
        final Object[] array = new Object[] { this.begin, this.end };
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public String toString() {
        return this.begin.getHostAddress() + " - " + this.end.getHostAddress();
    }
}
