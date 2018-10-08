package com.asteroid.duck.jena.util.query.states;

import com.asteroid.duck.jena.util.query.clauses.Limit;
import com.asteroid.duck.jena.util.query.clauses.Offset;
import com.asteroid.duck.jena.util.query.clauses.OrderBy;

/**
 * A query state where only certain operations can be applied.<br>
 * See {@link OrderBy}, {@link Limit}, {@link Offset}.
 *
 * @author Claudiu Blajan
 */
public interface LimitOffsetState extends OrderBy, Limit, Offset {
}
