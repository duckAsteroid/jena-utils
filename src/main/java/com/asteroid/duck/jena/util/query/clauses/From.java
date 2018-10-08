package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.states.WhereState;

import java.util.Collection;

/**
 *
 * @author Claudiu Blajan
 */
public interface From extends Endpoint {

    /**
     * Applies the FROM clause to the query.
     * @param table The <i>table</i> where the operations take place.
     * @return {@link WhereState} - a state where only compatible clauses can be applied.
     */
    WhereState from(String table);

    /**
     * Append FROM graph
     * @param graphUris
     * @return
     */
    WhereState fromGraphs(Collection<String> graphUris);

    WhereState fromNamedGraphs(Collection<String> graphUris);

}
