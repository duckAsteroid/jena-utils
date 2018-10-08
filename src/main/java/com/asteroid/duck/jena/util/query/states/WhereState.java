package com.asteroid.duck.jena.util.query.states;

import com.asteroid.duck.jena.util.query.clauses.*;

/**
 * A query state where only certain operations can be applied.<br>
 * See {@link Where}, {@link OrderBy}, {@link Limit}, {@link Offset}.
 *
 * @author Claudiu Blajan
 */
public interface WhereState extends Where, OrderBy, Limit, Offset {


}

