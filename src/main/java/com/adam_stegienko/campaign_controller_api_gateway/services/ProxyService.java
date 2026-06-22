package com.adam_stegienko.campaign_controller_api_gateway.services;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.adam_stegienko.campaign_controller_api_gateway.config.GatewayRoutesProperties;

@Service
public class ProxyService {

    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailers", "transfer-encoding", "upgrade", "host", "content-length"
    );

    private final RestTemplate restTemplate;
    private final GatewayRoutesProperties routesProperties;

    public ProxyService(RestTemplate restTemplate, GatewayRoutesProperties routesProperties) {
        this.restTemplate = restTemplate;
        this.routesProperties = routesProperties;
    }

    public ResponseEntity<byte[]> proxy(String serviceId, String downstreamPath,
                                         HttpMethod method, HttpHeaders incomingHeaders,
                                         byte[] body) {
        String targetUri = resolveTargetUri(serviceId);
        if (targetUri == null) {
            log.warn("No route found for service: {}", serviceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("No route configured for service: " + serviceId).getBytes());
        }

        URI destination = UriComponentsBuilder
                .fromUriString(targetUri + downstreamPath)
                .build(true)
                .toUri();

        HttpHeaders forwardHeaders = filterHeaders(incomingHeaders);
        HttpEntity<byte[]> requestEntity = (body != null && body.length > 0)
                ? new HttpEntity<>(body, forwardHeaders)
                : new HttpEntity<>(forwardHeaders);

        log.debug("Proxying {} {} -> {}", method, downstreamPath, destination);

        try {
            return restTemplate.exchange(destination, method, requestEntity, byte[].class);
        } catch (HttpStatusCodeException e) {
            HttpHeaders responseHeaders = e.getResponseHeaders();
            return ResponseEntity.status(e.getStatusCode())
                    .headers(responseHeaders != null ? responseHeaders : new HttpHeaders())
                    .body(e.getResponseBodyAsByteArray());
        } catch (ResourceAccessException e) {
            log.error("Downstream service unreachable: {}", destination, e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Downstream service unreachable: " + serviceId).getBytes());
        }
    }

    private String resolveTargetUri(String serviceId) {
        return routesProperties.getRoutes().entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(serviceId))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private HttpHeaders filterHeaders(HttpHeaders incoming) {
        HttpHeaders filtered = new HttpHeaders();
        incoming.forEach((name, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                filtered.put(name, values);
            }
        });
        return filtered;
    }
}
