package org.truenewx.core.version;

import org.springframework.context.ApplicationContext;

/**
 * 从MANIFEST.MF文件中读取实现版本号的抽象版本号读取器<br/>
 * 注意：使用时应创建位于目标jar包中的子类，以便读取MANIFEST.MF文件，且确保其中包含Implementation-Version属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractImplementationVersionReader extends AbstractVersionReader {

    @Override
    protected String readFullVersion(final ApplicationContext context) {
        return getClass().getPackage().getImplementationVersion();
    }

}
