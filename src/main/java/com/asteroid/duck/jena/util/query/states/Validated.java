package com.asteroid.duck.jena.util.query.states;

/**
 * Represents a validated query
 */
public interface Validated {
    /**
     * Renders the query into SPARQL syntax that can be parsed and executed
     * @return the SPARQL Query String
     */
    String toString();
}
