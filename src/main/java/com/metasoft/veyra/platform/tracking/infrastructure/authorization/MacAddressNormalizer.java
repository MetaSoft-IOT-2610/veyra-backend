package com.metasoft.veyra.platform.tracking.infrastructure.authorization;

import java.util.regex.Pattern;

/**
 * Normalizes MAC addresses to the {@code AA:BB:CC:DD:EE:FF} form expected by {@code MacAddress}.
 */
public final class MacAddressNormalizer {

    private static final Pattern MAC_WITH_SEPARATORS = Pattern.compile(
            "^([0-9A-F]{2}:){5}[0-9A-F]{2}$"
    );

    private MacAddressNormalizer() {
    }

    public static String normalize(String mac) {
        if (mac == null || mac.isBlank()) {
            throw new IllegalArgumentException("Mac address cannot be blank.");
        }

        var cleaned = mac.trim().toUpperCase().replace('-', ':');
        if (!cleaned.contains(":") && cleaned.length() == 12 && cleaned.matches("[0-9A-F]{12}")) {
            var builder = new StringBuilder();
            for (var i = 0; i < 12; i += 2) {
                if (i > 0) {
                    builder.append(':');
                }
                builder.append(cleaned, i, i + 2);
            }
            cleaned = builder.toString();
        }

        if (!MAC_WITH_SEPARATORS.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Mac address must follow the format XX:XX:XX:XX:XX:XX.");
        }
        return cleaned;
    }
}
