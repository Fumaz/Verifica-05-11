package dev.fumaz.loyalty.card;

import java.util.UUID;

public abstract class LoyaltyCard {

    private final UUID uuid;
    private String firstName;
    private String lastName;
    private int points;

    protected LoyaltyCardVerifier verifier; // protected so subclasses can access it

    public LoyaltyCard(LoyaltyCard other) {
        this.uuid = other.uuid;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.points = other.points;
        this.verifier = other.verifier;
    }

    public LoyaltyCard(UUID uuid, String firstName, String lastName, int points) {
        this.verifier = new LoyaltyCardVerifier();

        verifier.verifyUUID(uuid);
        verifier.verifyName(firstName);
        verifier.verifyName(lastName);
        verifier.verifyPoints(points);

        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.points = points;
    }

    public abstract String getType();

    public void purchase(int price) {
        this.setPoints(this.getPoints() + price);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        verifier.verifyName(firstName);

        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        verifier.verifyName(lastName);

        this.lastName = lastName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        verifier.verifyPoints(points);

        this.points = points;
    }

    public String toDatabase() {
        return uuid.toString() + "," + firstName + "," + lastName + "," + points + "," + getType();
    }

    @Override
    public String toString() {
        return "LoyaltyCard{" +
                "uuid=" + uuid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", points=" + points +
                '}';
    }

}
