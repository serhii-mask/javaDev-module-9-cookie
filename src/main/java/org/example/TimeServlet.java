package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

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
        configureTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");

        String timezone = getTimezoneFromRequest(request);

        String validTimezone = parserTimezone(timezone);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("time", validTimezone);

        Context context = new Context(request.getLocale(), Map.of("time", params));

        response.addCookie(new Cookie("lastTimezone", timezone));

        try (PrintWriter writer = response.getWriter()) {
            engine.process("timePage", context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureTemplateEngine() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:\\Users\\serhi\\Documents\\java\\homeWorks\\javaDev-module-9-cookie\\src\\main\\webapp\\WEB-INF\\template\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    private String getTimezoneFromRequest(HttpServletRequest request) {
        String timezoneParam = request.getParameter("timezone");

        if (timezoneParam == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("lastTimezone".equals(cookie.getName())) {
                        timezoneParam = cookie.getValue();
                        break;
                    }
                }
            }
        } else {
            timezoneParam = timezoneParam.replace("UTC+", "Etc/GMT-").replace("UTC-", "Etc/GMT+");
        }

        return timezoneParam != null ? timezoneParam : "Etc/GMT";
    }

    private String parserTimezone(String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);

        return currentTime.format(formatter).replace("GMT", "UTC");
    }
}