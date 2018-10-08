package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.states.WhereState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Update {

    /**
     * Applies the UPDATE clause to the query.
     * @param deleteString The string to be replaced.
     * @param insertString The replacer string.
     * @return {@link WhereState} - a state where only compatible clauses can be applied.
     */
    WhereState update(String deleteString, String... insertString);
}
