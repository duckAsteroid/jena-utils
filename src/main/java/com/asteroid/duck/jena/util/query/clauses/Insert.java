package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.states.WhereState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Insert {

    /**
     * Applies the INSERT clause to the query.
     * @param columns The columns to be inserted in the process.
     * @return {@link WhereState} - a state where only compatible clauses can be applied.
     */
    WhereState insert(String... columns);
}
