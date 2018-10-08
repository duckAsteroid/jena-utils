package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.states.LimitOffsetState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Offset extends Endpoint {

    /**
     * Applies the LIMIT clause to the query.
     * @param offset The number of results to be skipped by the query.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState offset(int offset);
}
