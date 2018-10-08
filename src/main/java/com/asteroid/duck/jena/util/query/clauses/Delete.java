package com.asteroid.duck.jena.util.query.clauses;

/**
 *
 * @author Claudiu Blajan
 */
public interface Delete {

    /**
     * Applies the DELETE clause to the query.
     * @param clause The condition encapsulated in the DELETE clause.
     * @return {@link Endpoint} - a state where no additional clauses can be applied.
     */
    Endpoint delete(String clause);

    /**
     * Applies the DELETE WHERE clause to the query.
     * @param clause The condition encapsulated in the DELETE WHERE clause.
     * @return {@link Endpoint} - a state where no additional clauses can be applied.
     */
    Endpoint deleteWhere(String clause);
}
