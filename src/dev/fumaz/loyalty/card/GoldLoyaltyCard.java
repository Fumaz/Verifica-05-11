package dev.fumaz.loyalty.card;

import java.util.UUID;

public class GoldLoyaltyCard extends LoyaltyCard {

    public GoldLoyaltyCard(LoyaltyCard other) {
        super(other);
    }

    public GoldLoyaltyCard(UUID uuid, String firstName, String lastName) {
        super(uuid, firstName, lastName, 50);
    }

    public GoldLoyaltyCard(UUID uuid, String firstName, String lastName, int points) {
        super(uuid, firstName, lastName, points);
    }

    @Override
    public String getType() {
        return "Gold";
    }

    @Override
    public void purchase(int price) {
        if (price < 50) {
            super.purchase(price); // if the price is below 50, return to default behavior (1 point per $1)
            return;
        }

        this.setPoints(this.getPoints() + (price * 2));
    }

    @Override
    public String toString() {
        return "Gold" + super.toString();
    }

}
