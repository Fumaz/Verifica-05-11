package dev.fumaz.loyalty.util;

import java.util.UUID;

public class UUIDs {

    public static boolean equals(UUID uuid, String string) {
        return uuid.toString().replace("-", "").equals(string.replace("-", ""));
    }

}
