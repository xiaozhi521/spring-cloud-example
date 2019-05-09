package com.mqf.study.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName MyFilter
 * @Description 自定义实现 zuul 过滤工能
 * @Author mqf
 * @Date 2019/5/9 15:26
 */
@Component
public class AccessFilter extends  ZuulFilter {

    private static Logger log= LoggerFactory.getLogger(AccessFilter.class);
    /**
     * 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型：
     *  pre：可以在请求被路由之前调用
     *  route：在路由请求时候被调用
     *  post：在route和error过滤器之后被调用
     *  error：处理请求时发生错误时被调用
     * @return
     */
    @Override
    public String filterType() {
        return "pre"; //前置过滤器
    }

    @Override
    public int filterOrder() {
        return 0; //过滤器的执行顺序，数字越大优先级越低
    }

    @Override
    public boolean shouldFilter() {
        return true;//是否执行该过滤器，此处为true，说明需要过滤
    }

    /**
     * 过滤器具体逻辑
     * @return
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info("send {} request to {}", request.getMethod(),request.getRequestURL().toString());

        Object accessToken = request.getParameter("accessToken");
        if(StringUtils.isEmpty(accessToken)) {
            log.warn("access token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            return null;
        }
        ctx.set("access token ok", true);// 设值，让下一个Filter看到上一个Filter的状态
        return null;
    }
}
