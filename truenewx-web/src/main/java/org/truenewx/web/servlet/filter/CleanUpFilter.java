package org.truenewx.web.servlet.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.util.PlaceholderResolver;
import org.truenewx.web.servlet.http.SessionIdRequestWrapper;
import org.truenewx.web.servlet.http.SessionIdResponseWrapper;
import org.truenewx.web.util.WebUtil;

/**
 * 进行一些清理工作的过滤器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class CleanUpFilter implements Filter {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    /**
     * 根路径属性名
     */
    private String contextPathAttributeName = "context";
    private String httpContextPathAttributeName = "httpContext";
    private String profile;
    private Map<String, String> attributes = new HashMap<>();
    /**
     * 是否伪造sessionId，以解决中间件不支持http和https访问共享session的问题
     */
    private boolean cookSessionId = false;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String contextPathAttributeName = filterConfig
                        .getInitParameter("contextPathAttributeName");
        if (StringUtils.isNotBlank(contextPathAttributeName)) {
            this.contextPathAttributeName = contextPathAttributeName;
        }

        final String httpContextPathAttributeName = filterConfig
                        .getInitParameter("httpContextPathAttributeName");
        if (StringUtils.isNotBlank(httpContextPathAttributeName)) {
            this.httpContextPathAttributeName = httpContextPathAttributeName;
        }

        this.profile = filterConfig.getServletContext().getInitParameter("spring.profiles.active");

        final String attributes = filterConfig.getInitParameter("attributes");
        if (StringUtils.isNotBlank(attributes)) {
            final String[] attributePairs = attributes.split(Strings.COMMA);
            final PlaceholderResolver placeholderResolver = getPlaceholderResolver(filterConfig);
            for (final String attributePair : attributePairs) {
                final int index = attributePair.indexOf(Strings.EQUAL);
                if (index > 0 && attributePair.length() > index + 1) {
                    final String name = attributePair.substring(0, index);
                    String value = attributePair.substring(index + 1);
                    if (placeholderResolver != null) {
                        value = placeholderResolver.resolveStringValue(value);
                    }
                    this.attributes.put(name, value);
                }
            }
        }
        if ("true".equalsIgnoreCase(filterConfig.getInitParameter("cookSessionId"))) {
            this.cookSessionId = true;
        }
    }

    private PlaceholderResolver getPlaceholderResolver(final FilterConfig filterConfig) {
        final ApplicationContext context = WebApplicationContextUtils
                        .getWebApplicationContext(filterConfig.getServletContext());
        if (context != null) {
            try {
                return context.getBean(PlaceholderResolver.class);
            } catch (final BeansException e) {
            }
        }
        return null;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp,
                    final FilterChain chain) throws IOException, ServletException {
        // 预处理request和response
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (this.cookSessionId) {
            request = new SessionIdRequestWrapper(request, response);
        }
        response = new SessionIdResponseWrapper(request, response);

        // 生成简单的相对访问根路径属性
        final String contextPath = request.getContextPath();
        request.setAttribute(this.contextPathAttributeName, contextPath);
        // 生成http绝对访问根路径属性
        final String host = WebUtil.getProtocolAndHost(request);
        final boolean https = host.startsWith(HTTPS_PREFIX);
        if (https) { // 当前访问是https
            request.setAttribute(this.httpContextPathAttributeName, host);
        } else {
            request.setAttribute(this.httpContextPathAttributeName, contextPath);
        }
        if (this.profile != null) {
            request.setAttribute("profile", this.profile);
        }
        // 生成配置的属性
        for (final Entry<String, String> entry : this.attributes.entrySet()) {
            String value = entry.getValue();
            if (https) { // 如果配置属性值中包含http协议路径，则在https访问时替换成https协议路径
                value = value.replace(HTTP_PREFIX, HTTPS_PREFIX);
            }
            request.setAttribute(entry.getKey(), value);
        }

        try {
            chain.doFilter(request, response);
        } catch (final Throwable e) {
            final String url = WebUtil.getRelativeRequestUrlWithQueryString(request, false);
            LoggerFactory.getLogger(getClass()).error("An exception happended on {}:", url);
            throw e;
        }
    }

    @Override
    public void destroy() {
    }

}
