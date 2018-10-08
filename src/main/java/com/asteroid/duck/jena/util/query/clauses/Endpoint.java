package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.InvalidQueryException;
import com.asteroid.duck.jena.util.query.QueryBuilder;
import com.asteroid.duck.jena.util.query.states.Validated;

/**
 * This interface must be implemented by any
 * clauses from the SPARQL query that can be
 * final.
 * @author Claudiu Blajan
 */
public interface Endpoint {

    /**
     * This method represents the final call on the {@link QueryBuilder}.
     * @return The {@link QueryBuilder} that was constructed.
     * @throws InvalidQueryException Thrown when there is a syntax error.
     */
    Validated createQuery() throws InvalidQueryException;
}
