package org.truenewx.test.spring.jdbc.datasource;

import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.AbstractDriverBasedDataSource;
import org.truenewx.core.Strings;

/**
 * H2的XA数据源工厂Bean
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class H2XADataSourceFactoryBean implements FactoryBean<XADataSource> {
    /**
     * 内嵌数据源
     */
    private AbstractDriverBasedDataSource dataSource;
    /**
     * 数据源URL后缀
     */
    private String urlSuffix = Strings.EMPTY;

    public void setDataSource(final AbstractDriverBasedDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setUrlSuffix(final String urlSuffix) {
        if (urlSuffix.startsWith(Strings.SEMICOLON)) {
            this.urlSuffix = urlSuffix;
        } else { // 确保分号开头
            this.urlSuffix = Strings.SEMICOLON + urlSuffix;
        }
    }

    @Override
    public XADataSource getObject() throws Exception {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(this.dataSource.getUrl() + this.urlSuffix);
        ds.setUser(this.dataSource.getUsername());
        ds.setPassword(this.dataSource.getPassword());
        return ds;
    }

    @Override
    public Class<?> getObjectType() {
        return XADataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
