package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.states.LimitOffsetState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Limit extends Endpoint {

    /**
     * Applies the LIMIT clause to the query.
     * @param limit The number of results to be returned.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState limit(int limit);
}
