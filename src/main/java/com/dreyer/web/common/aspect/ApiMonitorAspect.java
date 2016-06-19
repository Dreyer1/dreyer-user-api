package com.dreyer.web.common.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @description 对api调用的监控
 * @author Dreyer
 * @date 2015年10月25日 下午9:04:45
 */
@Aspect
@Component
public class ApiMonitorAspect {
    private Logger logger = Logger.getLogger(ApiMonitorAspect.class);

    @Around(value = "execution(public * com.dreyer.web.user.controller..*(..))")
    public Object monitor(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 请求的服务器
        String serverName = request.getServerName();
        // 请求的端口
        int serverPort = request.getServerPort();
        // Context路径
        String contextPath = request.getContextPath();
        // Servlet的路径
        String servletPath = request.getServletPath();

        String ip = getIpAddr(request);
        logger.info("IP为:" + ip + "发起请求,请求路径：" + request.getScheme() + ":" + serverName + ":" + serverPort + contextPath + servletPath);

        Enumeration<String> parameters = request.getParameterNames();
        String element = "";
        while (parameters.hasMoreElements()) {
            element = parameters.nextElement();
            logger.info("param key：" + element + " == " + request.getParameter(element));
        }
        long start = System.currentTimeMillis();
        try {
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        } catch (Throwable e) {
            throw e;
        } finally {
            logger.info("此次请求耗时：" + (System.currentTimeMillis() - start) + " ms");
        }
    }

    /**
     * 获取客户端的IP地址
     *
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }

        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
