package com.asteroid.duck.jena.util.query.clauses;

import com.asteroid.duck.jena.util.query.states.PrefixState;

/**
 *
 * @author Claudiu Blajan
 */
public interface Prefix {

    /**
     * Applies the PREFIX clause to the query.
     * @param prefix The prefix to be applied to the query.
     * @param namespace The namespace to be applied to the query.
     * @return {@link PrefixState} - a state where only compatible clauses can be applied.
     */
    PrefixState withPrefix(String prefix, String namespace);

    /**
     * Applies the PREFIX clause to the query.
     * @param prefix The prefix to be applied to the query.
     * @return {@link PrefixState} - a state where only compatible clauses can be applied.
     */
    PrefixState withPrefix(String prefix);

    /**
     * Applies the PREFIX clause to the query.
     * @param prefixes The prefixes to be applied to the query. See {@link Prefixes}.
     * @return {@link PrefixState} - a state where only compatible clauses can be applied.
     */
    PrefixState withPrefixes(String ... prefixes);
}
