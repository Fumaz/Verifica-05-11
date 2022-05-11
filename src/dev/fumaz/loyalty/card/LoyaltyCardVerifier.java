package dev.fumaz.loyalty.card;

import java.util.UUID;

public class LoyaltyCardVerifier {

    public void verifyUUID(UUID uuid) {
        verify(() -> uuid != null, "UUID cannot be null");
    }

    public void verifyName(String name) {
        verify(() -> name != null && !name.isBlank(), "Name cannot be null or blank");
    }

    public void verifyPoints(int points) {
        verify(() -> points >= 0, "Points cannot be less than 0");
    }

    private void verify(EmptyPredicate predicate, String message) {
        if (predicate.test()) {
            return;
        }

        throw new IllegalArgumentException(message);
    }

    @FunctionalInterface
    private interface EmptyPredicate {
        boolean test();
    }
}
