package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.expressions.WhereExpression;
import com.asteroid.duck.jena.util.query.states.LimitOffsetState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Where extends Endpoint, OrderBy {

    /**
     * Applies the WHERE clause to the query. <br>
     * This method does not support higher level functions
     * such as UNION and OPTIONAL by itself. <br>
     * Use {@link #where(WhereExpression expression)} instead.
     * @param condition The condition encapsulated in the WHERE clause.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState where(String condition);

    /**
     * Applies the WHERE clause to the query.
     * @param whereExpression The {@link WhereExpression} to be taken as a parameter.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState where(WhereExpression whereExpression);
}
