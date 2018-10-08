package com.asteroid.duck.jena.util.query.expressions;

import com.asteroid.duck.jena.util.query.clauses.Where;

import java.util.Collection;

/**
 * This expression is intended to be used in complex WHERE clauses, where<br>
 * additional clauses are to be used within WHERE. See {@link #matches}, {@link #andAny}, {@link #map}
 *
 * @author Claudiu Blajan
 */
public class WhereExpression {

    private StringBuilder value = new StringBuilder();

    public WhereExpression() {

    }

    public WhereExpression(String initialClause) {

        value.append(initialClause);
    }

    private WhereExpression getOuterClass() {
        return this;
    }

    /**
     * Matches between a variable that needs to be queried for.
     * @param attribute The variable to be matched.
     * @return An inner class on which can be applied subsequent methods.
     */
    public IntermediateWhereExpression_Match matches(String attribute) {

        return new IntermediateWhereExpression_Match(attribute);
    }

    /**
     * Adds an attribute that doesn't need to match what is queried for.
     * @param attribute The variable to be included in the query.
     * @return An inner class on which can be applied subsequent methods.
     */
    public IntermediateWhereExpression_AndAny andAny(String attribute) {

        return new IntermediateWhereExpression_AndAny(attribute);
    }

    /**
     * Maps a variable to something in the query.
     * @param attribute The variable to be mapped.
     * @return An inner class on which can be applied subsequent methods.
     */
    public IntermediateWhereExpression_MapTo map(String attribute) {

        return new IntermediateWhereExpression_MapTo(attribute);
    }

    /**
     * The UNION clause within a {@link Where WHERE} clause.
     * @param exp The expression included in the UNION.
     * @return The {@link WhereExpression}
     */
    public WhereExpression union(String exp) {

        value.append(" UNION { ")
                .append(exp)
                .append(" } ");

        return this;
    }

    /**
     * The FILTER clause within a WHERE clause
     * @param exp The expression to be filtered
     * @return The {@link WhereExpression}
     */
    public WhereExpression filter(String exp){
        value.append(" FILTER ( ")
                .append(exp)
                .append(" ) ");
        return this;
    }

    /**
     * The GRAPH clauses within a WHERE clause
     * @param graphName The graph name that needs to be included
     * @param exp The expression within the graph
     * @return The {@link WhereExpression}
     */
    public WhereExpression graph(String graphName, String exp){
        value.append(" GRAPH <")
                .append(graphName)
                .append("> { ")
                .append(exp)
                .append(" } ");
        return this;
    }

    /**
     * Add GRAPH clauses for a parameter
     * @param graphName
     * @param exp
     * @return
     */
    public WhereExpression graphWithQM(String graphName, String exp){
        value.append(" GRAPH ")
                .append(graphName)
                .append(" { ")
                .append(exp)
                .append(" } ");
        return this;
    }

    /**
     *
     * @param graphNames
     * @param exp
     * @return
     */
    public WhereExpression graph(Collection<String> graphNames, String exp){

        graphNames.forEach(graph ->
                value.append(" GRAPH <")
                        .append(graph)
                        .append("> { ")
                        .append(exp)
                        .append(" } ")
        );

        /*value.append(" GRAPH <")
                .append(graphName)
                .append("> { ")
                .append(exp)
                .append(" } ");*/
        return this;
    }

    /**
     *  @Override
    public WhereState fromNamedGraphs(Collection<String> graphUris) {

    graphUris.forEach(graph ->
    query.append(" FROM NAMED <")
    .append(graph)
    .append("> ")
    );

    return this;
    }
     */

    /**
     * The OPTIONAL clause within a {@link jena.utils.querybuilder.clauses.Where WHERE} clause.
     * @param exp The expression included in the OPTIONAL.
     * @return The {@link WhereExpression}
     */
    public WhereExpression optional(String exp) {

        value.append(" OPTIONAL { ")
                .append(exp)
                .append(" } ");

        return this;
    }

    @Override
    public String toString() {

        return value.toString();
    }


    public class IntermediateWhereExpression_Match {

        private String attribute;

        IntermediateWhereExpression_Match(String attribute) {
            this.attribute = attribute;
        }

        /**
         * Matches between a prefix and a suffix the variable that needs to be queried for.
         * @param prefix The prefix to be matched between.
         * @param suffix The suffix to be matched between.
         * @return The {@link WhereExpression}.
         */
        public WhereExpression matchesBetween(String prefix, String suffix) {

            value.append(prefix)
                    .append(attribute)
                    .append(suffix);

            return getOuterClass();
        }
    }

    public class IntermediateWhereExpression_AndAny {

        private String attribute;

        IntermediateWhereExpression_AndAny(String attribute) {
            this.attribute = attribute;
        }

        /**
         * Adds an attribute of any type that doesn't need to match what is queried for.
         * @param type The attribute's type.
         * @return The {@link WhereExpression}.
         */
        public WhereExpression ofType(String type) {

            value.append(type)
                    .append(attribute);

            return getOuterClass();
        }
    }

    public class IntermediateWhereExpression_MapTo {

        private String attribute;

        IntermediateWhereExpression_MapTo(String attribute) {
            this.attribute = attribute;
        }

        /**
         * Maps an attribute to another.
         * @param attr The attribute to be mapped on.
         * @return The {@link WhereExpression}.
         */
        public WhereExpression to(String attr) { //todo test

            value.append("map ")
                    .append(attribute)
                    .append(" to ")
                    .append(attr);

            return getOuterClass();
        }
    }

}
