package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        JakartaServletWebApplication jakartaServletWebApplication =
                JakartaServletWebApplication.buildApplication(this.getServletContext());

        WebApplicationTemplateResolver
                resolver = new WebApplicationTemplateResolver(jakartaServletWebApplication);
        resolver.setPrefix("/WEB-INF/template/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");

        String timezone = getTimezoneFromRequestOrCookie(request, response);

        String validTimezone = parserTimezone(timezone);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("time", validTimezone);

        Context context = new Context(request.getLocale(), Map.of("time", params));

        try (PrintWriter writer = response.getWriter()) {
            engine.process("timePage", context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTimezoneFromRequestOrCookie(HttpServletRequest request, HttpServletResponse response) {
        String timezoneParam = request.getParameter("timezone");

        if (timezoneParam != null) {
            timezoneParam = timezoneParam.replace("UTC+", "Etc/GMT-").replace("UTC-", "Etc/GMT+");
            saveTimezoneToCookie(response, timezoneParam);

            return timezoneParam;
        }

        return getTimezoneFromCookie(request);
    }

    private void saveTimezoneToCookie(HttpServletResponse response, String timezoneParam) {
        response.addCookie(new Cookie("lastTimezone", timezoneParam));
    }

    private String getTimezoneFromCookie(HttpServletRequest request) {
        String timezoneParam = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    timezoneParam = cookie.getValue();
                    break;
                }
            }
        }

        return (timezoneParam != null) ? timezoneParam : "Etc/GMT";
    }

    private String parserTimezone(String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);

        return currentTime.format(formatter).replace("GMT", "UTC");
    }
}