package com.asteroid.duck.jena.util.query.impl;

import com.asteroid.duck.jena.util.query.InvalidQueryException;
import com.asteroid.duck.jena.util.query.OrderType;
import com.asteroid.duck.jena.util.query.QueryBuilder;
import com.asteroid.duck.jena.util.query.clauses.Endpoint;
import com.asteroid.duck.jena.util.query.expressions.SelectExpression;
import com.asteroid.duck.jena.util.query.expressions.WhereExpression;
import com.asteroid.duck.jena.util.query.states.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Claudiu Blajan
 */
public class QueryBuilderImpl implements QueryBuilder, PrefixState, CrudState, FromState, LimitOffsetState, WhereState , Validated{

    private StringBuilder query = new StringBuilder();

    private List<String> selectArguments;
    //    private List<String> whereArguments;
    private String whereCondition;

    public QueryBuilderImpl() {

    }

    public PrefixState startQuery() {

        return this;
    }

    public PrefixState withPrefixes(String ... prefix) {

        for (String p : prefix) {
            withPrefix(p);
        }
        return this;
    }

    public PrefixState withPrefix(String prefix) {

        query.append("PREFIX ")
                .append(prefix);
        return this;
    }

    public PrefixState withPrefix(String prefix, String namespace) {

        query.append("PREFIX ")
                .append(prefix)
                .append(": <")
                .append(namespace)
                .append("> ");

        return this;
    }

    @Override
    public FromState select(String... columns) {

        selectArguments = new ArrayList<>();

        query.append("SELECT ");

        for (String s : columns) {
            if (s.contains("(")) {
                query.append(varWithoutQ(s)).append(" ");
            } else {
                query.append(var(s)).append(" ");
            }
            selectArguments.add(s);
        }

        return this;
    }

    @Override
    public FromState selectDistinct(String... columns) {

        selectArguments = new ArrayList<>();

        query.append("SELECT DISTINCT");

        for (String s : columns) {
            if (s.contains("(")) {
                query.append(varWithoutQ(s)).append(" ");
            } else {
                query.append(var(s)).append(" ");
            }
            selectArguments.add(s);
        }

        return this;
    }

    @Override
    public FromState select(SelectExpression selectExpression) {

        query.append("SELECT ");

        query.append(selectExpression.toString());

        return this;
    }

    @Override
    public WhereState insert(String... columns) {

        query.append("insert ");

        for (String s : columns) {
            query.append(s);
        }

        return this;
    }

    @Override
    public WhereState update(String deleteString, String... insertString) {

        delete(deleteString);
        insert(insertString);

        return this;
    }

    @Override
    public Endpoint delete(String clause) {

        query.append("delete ");
        query.append(clause);

        return this;
    }

    @Override
    public Endpoint deleteWhere(String clause) {

        query.append("DELETE WHERE ")
                .append(clause);

        return this;
    }

    @Override
    public WhereState from(String table) {

        query.append(" FROM NAMED ")
                .append(table)
                .append(" ");

        return this;
    }

    @Override
    public WhereState fromGraphs(Collection<String> graphUris) {

        graphUris.forEach(graph ->
                query.append(" FROM <")
                        .append(graph)
                        .append("> ")
        );

        return this;
    }

    @Override
    public WhereState fromNamedGraphs(Collection<String> graphUris) {

        graphUris.forEach(graph ->
                query.append(" FROM NAMED <")
                        .append(graph)
                        .append("> ")
        );

        return this;
    }

    @Override
    public LimitOffsetState limit(int limit) {

        query.append("LIMIT ")
                .append(limit)
                .append(" ");

        return this;
    }

    @Override
    public LimitOffsetState offset(int offset) {

        query.append("OFFSET ")
                .append(offset)
                .append(" ");

        return this;
    }

    @Override
    public LimitOffsetState where(String condition) {

//        whereArguments = new ArrayList<>(Arrays.asList(condition.split(" ")));
        whereCondition = condition;

        query.append("WHERE { ")
                .append(condition)
                .append("}");

        return this;
    }

    @Override
    public LimitOffsetState where(WhereExpression whereExpression) {

        return where(whereExpression.toString());
    }

    @Override
    public LimitOffsetState orderBy(String column) {

        query.append(" ORDER BY ")
                .append(var(column))
                .append(" ");

        return this;
    }

    @Override
    public LimitOffsetState orderByAppend(String column) {
        query.append(" ")
                .append(var(column))
                .append(" ");

        return this;
    }

    @Override
    public LimitOffsetState orderBy(String column, OrderType orderType) {

        query.append(" ORDER BY ")
                .append(orderType.getType())
                .append("(")
                .append(var(column))
                .append(") ");

        return this;
    }

    @Override
    public LimitOffsetState orderByAppend(String column, OrderType orderType) {

        query.append(" ")
                .append(orderType.getType())
                .append("(")
                .append(var(column))
                .append(") ");

        return this;
    }


    @Override
    public LimitOffsetState orderBy(String column, OrderType orderType, String sortedBy) {

        query.append(" ORDER BY ")
                .append(var(column))
                .append(" ")
                .append(orderType.getType())
                .append("(")
                .append(var(sortedBy))
                .append(") ");

        return this;
    }

    @Override
    public Validated createQuery() throws InvalidQueryException {

        validateQuery();

        return this;
    }

    private void validateQuery() throws InvalidQueryException {

        //todo uncomment this after solution is found

//        // Check that what it is requested in SELECT clause is also present in WHERE clause
//        for (String s : selectArguments) {
//            if (!whereCondition.toLowerCase().contains(s.toLowerCase()))
//                throw new InvalidQueryException();
//        }
    }

    /**
     * Provides a stream view of the result of executing a SPARQL query that returns GUIDs
     *
     * @param dataset a dataset to query (or null - to query the default)
     * @return a stream of GUIDs
     */
    public QueryExecution getQueryExecution(Dataset dataset) {

        org.apache.jena.query.Query localQuery = QueryFactory.create(query.toString());

        return QueryExecutionFactory.create(localQuery, dataset);
    }



    /**
     * Appends the variable identifier <i>?</i> to the variable.
     *
     * @param s The given variable.
     * @return The resulted String.
     */
    public static String var(String s) {
        return "?" + s;
    }

    public static String varWithoutQ(String s) {
        return s;
    }

    public static String exact(String s) {
        return "<" + s + ">";
    }

    @Override
    public String toString() {

        return query.toString();
    }

    @Override
    public Query build() {
        String queryString = this.toString();
        try {
            return QueryFactory.create(queryString);
        }
        catch(QueryException e) {
            throw new QueryException("Error parsing: "+queryString, e);
        }
    }
}
