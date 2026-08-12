package com.asanrezerv.restaurantservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class GoogleMapsLinkParser {

    // SIRA VACİBDİR: "!3d..!4d.." və "q=lat,lng" konkret PİN koordinatıdır;
    // "@lat,lng" isə xəritənin görüntü MƏRKƏZİDİR (istifadəçi sonradan sürüşdürübsə fərqli ola bilər).
    // Ona görə dəqiq pin pattern-ləri əvvəl yoxlanılmalıdır, "@" isə yalnız son fallback olmalıdır.
    private static final Pattern[] COORD_PATTERNS = {
            Pattern.compile("!3d(-?\\d+\\.\\d+)!4d(-?\\d+\\.\\d+)"),
            Pattern.compile("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)"),
            Pattern.compile("[?&]ll=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)"),
            Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)"),
    };

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public record Coordinates(double latitude, double longitude) {
    }

    public static Coordinates parse(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        Coordinates fromRaw = tryExtract(rawUrl);
        if (fromRaw != null) {
            return fromRaw;
        }

        String resolved = resolveRedirect(rawUrl);
        return resolved != null ? tryExtract(resolved) : null;
    }

    private static Coordinates tryExtract(String url) {
        for (Pattern pattern : COORD_PATTERNS) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                try {
                    return new Coordinates(
                            Double.parseDouble(matcher.group(1)),
                            Double.parseDouble(matcher.group(2))
                    );
                } catch (NumberFormatException ignored) {
                    // növbəti pattern-i sınayaq
                }
            }
        }
        return null;
    }

    private static String resolveRedirect(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<Void> response = CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
            return response.uri().toString();
        } catch (Exception e) {
            log.warn("Google Maps linkinin redirect-i izlənilə bilmədi: {}", url, e);
            return null;
        }
    }
}
