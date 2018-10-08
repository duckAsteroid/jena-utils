package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.OrderType;
import com.asteroid.duck.jena.util.query.states.LimitOffsetState;

/**
 *
 * @author Claudiu Blajan
 */
public interface OrderBy {

    /**
     * Applies the ORDER BY clause to the query.
     * @param column The column to be ordered by.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState orderBy(String column);

    /**
     * Applies the ORDER BY clause to the query.
     * @param column The column to be ordered by.
     * @param orderType The order of the query. See {@link OrderType#ASC} and {@link OrderType#DESC}.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState orderBy(String column, OrderType orderType);

    /**
     * Applies the ORDER BY clause to the query.
     * @param column The column to be ordered by.
     * @param orderType The order of the query. See {@link OrderType#ASC} and {@link OrderType#DESC}.
     * @param sortedBy The column to be sorted by.
     * @return {@link LimitOffsetState} - a state where only compatible clauses can be applied.
     */
    LimitOffsetState orderBy(String column, OrderType orderType, String sortedBy);

    LimitOffsetState orderByAppend(String column);

    LimitOffsetState orderByAppend(String column, OrderType orderType);
}
