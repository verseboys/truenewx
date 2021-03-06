package org.truenewx.service.relation;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.relation.Relation;

/**
 * 基于传输模型的关系服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关系类型
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 */
public interface ModelRelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends RelationService<T, L, R> {

    /**
     * 添加关系
     * 
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param model
     *            存放添加数据的提交模型对象
     *
     * @return 添加的关系
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(L leftId, R rightId, SubmitModel<T> model) throws HandleableException;

    /**
     * 修改关系
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param model
     *            存放修改数据的提交模型对象
     * @return 修改后的关系
     * @throws HandleableException
     *             如果修改校验失败
     */
    T update(L leftId, R rightId, SubmitModel<T> model) throws HandleableException;
}
