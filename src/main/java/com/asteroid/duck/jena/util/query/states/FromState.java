package com.asteroid.duck.jena.util.query.states;

import com.asteroid.duck.jena.util.query.clauses.From;
import com.asteroid.duck.jena.util.query.clauses.Where;

/**
 * A query state where only certain operations can be applied.<br>
 * See {@link From}, {@link Where}.
 *
 * @author Claudiu Blajan
 */
public interface FromState extends From, Where {
}
