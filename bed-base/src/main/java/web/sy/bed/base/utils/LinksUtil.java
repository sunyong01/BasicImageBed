package web.sy.bed.base.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class LinksUtil {

    public static HashMap<String, String> createLinks(String baseUrl, String pathname) {
        HashMap<String, String> links = new HashMap<>();
        String imageUrl = baseUrl + "image/" + pathname;
        String thumbnailUrl = baseUrl + "thumb/" + pathname;
        
        links.put("url", imageUrl);
        links.put("thumbnailUrl", thumbnailUrl);
        links.put("markdown", "![Image](" + imageUrl + ")");
        links.put("markdownWithLink", "[![Image](" + imageUrl + ")](" + imageUrl + ")");
        links.put("html", "<img src='" + imageUrl + "' />");
        links.put("bbcode", "[img]" + imageUrl + "[/img]");
        
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
