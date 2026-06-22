package com.metasoft.veyra.platform.tracking.infrastructure.authorization;

import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.DeviceType;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.IotStatus;
import com.metasoft.veyra.platform.tracking.domain.model.valueobjects.MacAddress;
import com.metasoft.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Authenticates on-premise edge gateways via {@code X-Device-Id} + {@code X-Device-Mac}.
 */
public class EdgeGatewayAuthFilter extends OncePerRequestFilter {

    public static final String HEADER_DEVICE_ID = "X-Device-Id";
    public static final String HEADER_DEVICE_MAC = "X-Device-Mac";

    private final DeviceRepository deviceRepository;

    public EdgeGatewayAuthFilter(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        var path = request.getRequestURI();
        if (path.startsWith("/api/v1/edge/")) {
            return false;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())
                && "/api/v1/measurements".equals(path)
                && request.getHeader(HEADER_DEVICE_ID) != null) {
            return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        var externalDeviceId = request.getHeader(HEADER_DEVICE_ID);
        var macHeader = request.getHeader(HEADER_DEVICE_MAC);

        if (externalDeviceId == null || externalDeviceId.isBlank()
                || macHeader == null || macHeader.isBlank()) {
            writeUnauthorized(response, "Missing X-Device-Id or X-Device-Mac");
            return;
        }

        try {
            var macAddress = new MacAddress(MacAddressNormalizer.normalize(macHeader));
            var device = deviceRepository.findByExternalDeviceIdAndMacAddress(
                    externalDeviceId.trim(),
                    macAddress
            );

            if (device.isEmpty()) {
                writeUnauthorized(response, "Invalid edge gateway credentials");
                return;
            }

            var gateway = device.get();
            if (gateway.getDeviceType() != DeviceType.EDGE_GATEWAY) {
                writeUnauthorized(response, "Device is not an edge gateway");
                return;
            }
            if (gateway.getIotStatus() != null && gateway.getIotStatus() != IotStatus.ACTIVE) {
                writeForbidden(response, "Edge gateway IoT access is revoked");
                return;
            }

            var principal = new EdgeGatewayPrincipal(
                    gateway.getId(),
                    gateway.getNursingHomeId().nursingHomeId(),
                    gateway.getExternalDeviceId(),
                    gateway.getMacAddress().macAddress()
            );
            SecurityContextHolder.getContext().setAuthentication(new EdgeGatewayAuthentication(principal));
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException ex) {
            writeUnauthorized(response, ex.getMessage());
        }
    }

    private static void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private static void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
