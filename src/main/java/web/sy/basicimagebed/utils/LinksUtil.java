package web.sy.basicimagebed.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.sy.basicimagebed.configuration.ConfigProperties;

import java.util.Date;
import java.util.HashMap;


public class LinksUtil {

    public static HashMap<String, String> createLinks(String basePath, String pathname) {
        String url = basePath +"image/"+ pathname;

        HashMap<String, String> links = new HashMap<>();
        links.put("url", url);
        links.put("html", "<img src='" + url + "' />");
        links.put("bbcode", "[img]" + url + "[/img]");
        links.put("markdown", "![Image](" + url + ")");
        links.put("markdownWithLink", "[![Image](" + url + ")](" + url + ")");
//        links.put("thumbnailUrl", url);
        return links;
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
