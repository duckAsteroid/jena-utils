package com.asteroid.duck.jena.util.query.expressions;


import static com.asteroid.duck.jena.util.query.impl.QueryBuilderImpl.var;

/**
 * This expression is intended to be used in complex SELECT clauses,<br>
 * where additional clauses are to be used within SELECT. See {@link #count}, {@link #sum}
 *
 * @author Claudiu Blajan
 */
public class SelectExpression {

    private StringBuilder value = new StringBuilder();

    public SelectExpression() {

    }

    public SelectExpression(String initialClause) {

        value.append(initialClause);
    }

    private SelectExpression getOuterClass() {
        return this;
    }

    /**
     * Applies the COUNT clause to the query.
     * @param attribute The condition considered as counted
     */
    public IntermediateSelectExpression_CountAs count(String attribute) {

        return new IntermediateSelectExpression_CountAs(attribute);
    }

    /**
     * Applies the SUM clause to the query.
     * @param attribute The condition considered as summed
     */
    public IntermediateSelectExpression_SumAs sum(String attribute) {

        return new IntermediateSelectExpression_SumAs(attribute);
    }

    public class IntermediateSelectExpression_CountAs {

        private String attribute;

        IntermediateSelectExpression_CountAs(String attribute) {
            this.attribute = attribute;
        }

        /**
         * Applies the COUNT clause to the query.
         * @param as The condition to be counted as
         */
        public SelectExpression as(String as) {

            value.append("(COUNT(")
                    .append(var(attribute))
                    .append(") AS ")
                    .append(var(as))
                    .append(") ");

            return getOuterClass();
        }
    }

    public class IntermediateSelectExpression_SumAs {

        private String attribute;

        IntermediateSelectExpression_SumAs(String attribute) {
            this.attribute = attribute;
        }

        /**
         * Applies the SUM clause to the query.
         * @param as The condition to be summed as
         */
        public SelectExpression as(String as) {

            value.append("(SUM(")
                    .append(var(attribute))
                    .append(") AS ")
                    .append(var(as))
                    .append(") ");

            return getOuterClass();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
