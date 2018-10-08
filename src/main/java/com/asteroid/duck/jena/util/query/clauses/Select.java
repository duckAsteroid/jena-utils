package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.expressions.SelectExpression;
import com.asteroid.duck.jena.util.query.states.FromState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Select {

    /**
     * Applies the SELECT clause to the query.
     * @param columns The columns to be selected by the query.
     * @return {@link FromState} - a state where only compatible clauses can be applied.
     */
    FromState select(String... columns);

    /**
     * Applies the SELECT DISTINCT clause to the query
     *@param columns The columns to be selected by the query.
     * @return {@link FromState} - a state where only compatible clauses can be applied.
     */
    FromState selectDistinct(String... columns);

    /**
     * Applies the SELECT clause to the query.
     * @param selectExpression The expression that builds the SELECT query.
     * @return {@link FromState} - a state where only compatible clauses can be applied.
     */
    FromState select(SelectExpression selectExpression);
}
