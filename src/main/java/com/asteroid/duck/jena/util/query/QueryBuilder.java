package com.asteroid.duck.jena.util.query;

import com.asteroid.duck.jena.util.query.states.PrefixState;
import org.apache.jena.query.Query;

/**
 * Used to create Jena queries programmatically.
 *
 * @author Claudiu Blajan
 */
public interface QueryBuilder {

    /**
     * Initializes the query.
     * @return A query state where only certain operations can be applied.
     */
    PrefixState startQuery();

    /**
     * Attempt to build the current query. Will return a valid query or throw an exception
     * @return the query (if valid)
     */
    Query build();
}
