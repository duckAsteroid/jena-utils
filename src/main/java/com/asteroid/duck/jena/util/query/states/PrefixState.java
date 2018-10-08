package com.asteroid.duck.jena.util.query.states;

import com.asteroid.duck.jena.util.query.clauses.Prefix;

/**
 * A query state where only certain operations can be applied.<br>
 * See {@link CrudState}.
 *
 * @author Claudiu Blajan
 */
public interface PrefixState extends Prefix, CrudState {
}
