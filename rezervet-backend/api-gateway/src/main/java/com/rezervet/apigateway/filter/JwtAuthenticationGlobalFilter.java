package com.rezervet.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

/**
 * Mərkəzi təhlükəsizlik qapısı:
 *  1) Gələn sorğudan client-in uydura biləcəyi X-User-* header-lərini SİLİR.
 *  2) Public marşrutları JWT tələb etmədən ötürür.
 *  3) Qalan sorğularda Bearer JWT-nin imzasını + vaxtını yoxlayır.
 *  4) Uğurlu olduqda userId/role claim-lərini ETİBARLI header kimi downstream servisə ötürür.
 *  5) Bəzi prefikslər üçün rol yoxlaması edir (məs. /api/admin -> ADMIN).
 *
 * Bu yalnız o halda təhlükəsizdir ki, downstream servislərə yalnız Gateway çata bilsin
 * (Docker internal network — bax: docker-compose.yml).
 */
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    @Value("${spring.security.secret-key}")
    private String secretKey;

    // JWT tələb ETMƏYƏN marşrutlar (metod + regex).
    private static final List<PublicRoute> PUBLIC_ROUTES = List.of(
            new PublicRoute(null, "^/api/auth/login/?$"),
            new PublicRoute(null, "^/api/auth/register(/.*)?$"),      // initiate / verify / finish
            new PublicRoute(null, "^/api/auth/refresh/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/restaurants/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/restaurants/[0-9a-fA-F-]{36}/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/restaurants/[0-9a-fA-F-]{36}/branches/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/branches/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/branches/[0-9a-fA-F-]{36}/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/subscriptions/plans/?$"),
            // search-service — restoran/filial oxu (GET) sorğuları da public gəzintidir
            new PublicRoute(HttpMethod.GET, "^/api/search/restaurants/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/search/restaurants/search/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/search/restaurants/[0-9a-fA-F-]{36}/?$"),
            new PublicRoute(HttpMethod.GET, "^/api/search/restaurants/[0-9a-fA-F-]{36}/branches/?$"),
            new PublicRoute(null, "^/actuator(/.*)?$"),
            new PublicRoute(null, "^/oauth2/(.*)?$"),
            new PublicRoute(null, "^/login/oauth2/(.*)?$")
    );

    // Prefiks -> həmin prefiksə icazəli rollar. Siyahıda olmayan prefiks üçün rol yoxlanmır
    // (yalnız autentifikasiya kifayətdir).
    private static final Map<String, List<String>> ROLE_RULES = Map.of(
            "/api/admin", List.of("ADMIN"),
            "/api/manager", List.of("MANAGER"),
            "/api/waiter", List.of("WAITER", "MANAGER"),
            "/api/ai", List.of("MANAGER")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // (1) Client-dən gələn saxta kimlik header-lərini həmişə təmizlə
        ServerHttpRequest.Builder sanitized = request.mutate()
                .headers(h -> {
                    h.remove(USER_ID_HEADER);
                    h.remove(USER_ROLE_HEADER);
                });

        // (2) Public marşrut — kimlik yoxlamadan ötür
        if (isPublic(method, path)) {
            return chain.filter(exchange.mutate().request(sanitized.build()).build());
        }

        // (3) Bearer token yoxlaması
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        String userId = String.valueOf(claims.get("userId"));
        String role = String.valueOf(claims.get("role"));

        // (5) Rol yoxlaması
        if (!hasRequiredRole(path, role)) {
            return reject(exchange, HttpStatus.FORBIDDEN);
        }

        // (4) Etibarlı kimlik header-lərini əlavə et
        ServerHttpRequest trusted = sanitized
                .header(USER_ID_HEADER, userId)
                .header(USER_ROLE_HEADER, role)
                .build();

        return chain.filter(exchange.mutate().request(trusted).build());
    }

    private boolean isPublic(HttpMethod method, String path) {
        return PUBLIC_ROUTES.stream().anyMatch(pr -> pr.matches(method, path));
    }

    private boolean hasRequiredRole(String path, String role) {
        return ROLE_RULES.entrySet().stream()
                .filter(e -> path.startsWith(e.getKey()))
                .findFirst()
                .map(e -> e.getValue().contains(role))
                .orElse(true); // qayda yoxdursa — autentifikasiya kifayətdir
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public int getOrder() {
        return -1; // marşrutlaşdırmadan əvvəl işləsin
    }

    private record PublicRoute(HttpMethod method, String regex) {
        boolean matches(HttpMethod requestMethod, String path) {
            boolean pathOk = path.matches(regex);
            boolean methodOk = method == null || method.equals(requestMethod);
            return pathOk && methodOk;
        }
    }
}
