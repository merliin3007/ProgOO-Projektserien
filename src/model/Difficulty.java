package model;

/**
 * An enum to handle the difficulty in a nice way.
 */
public enum Difficulty {
    EASY("Easy"),
    NORMAL("Normal"),
    HARD("Hard");

    public String name;

    /**
     * As a wise man once said: A constructor.
     * 
     * @param name The name of the difficulty.
     */
    private Difficulty(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
