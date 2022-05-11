package dev.fumaz.loyalty.card;

import java.util.UUID;

public class PlatinumLoyaltyCard extends LoyaltyCard{

    public PlatinumLoyaltyCard(LoyaltyCard other) {
        super(other);
    }

    public PlatinumLoyaltyCard(UUID uuid, String firstName, String lastName) {
        super(uuid, firstName, lastName, 100);
    }

    public PlatinumLoyaltyCard(UUID uuid, String firstName, String lastName, int points) {
        super(uuid, firstName, lastName, points);
    }

    @Override
    public String getType() {
        return "Platinum";
    }

    @Override
    public void purchase(int price) {
        if (price < 100) {
            super.purchase(price); // if the price is below 100, return to default behavior (1 point per $1)
            return;
        }

        this.setPoints(this.getPoints() + (price * 3));
    }

    @Override
    public String toString() {
        return "Platinum" + super.toString();
    }

}
