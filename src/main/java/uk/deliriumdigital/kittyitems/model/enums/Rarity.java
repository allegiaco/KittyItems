package uk.deliriumdigital.kittyitems.model.enums;

public enum Rarity {

    GOLD("Gold", 10),
    PURPLE("Purple", 20),
    GREEN("Green", 30),
    BLUE("Blue", 40);

    private final String name;
    private final int probability;

    private Rarity(String name, int probability) {
        this.name = name;
        this.probability = probability;
    }

    public String getName() {
        return name;
    }

    public int getProbability() {
        return probability;
    }
}
