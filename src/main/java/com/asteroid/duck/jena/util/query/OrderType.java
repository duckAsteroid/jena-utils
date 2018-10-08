package com.asteroid.duck.jena.util.query;

/**
 * The order type used in {@link com.asteroid.duck.jena.util.query.clauses.OrderBy ORDER BY} clause.<br>
 * Can be ascending or descending.
 *
 * @author Claudiu Blajan
 */
public enum OrderType {
    /**
     * Ascending order
     */
    ASC("ASC"),
    /**
     * Descending order
     */
    DESC("DESC");

    private String type;

    OrderType(String type) {
        this.type = type;
    }

    /**
     * Returns the order type of the enum as a {@link String}.
     * @return The order type.
     */
    public String getType() {
        return type;
    }
}
