package com.asteroid.duck.jena.util.query.impl;

import com.asteroid.duck.jena.util.query.InvalidQueryException;
import com.asteroid.duck.jena.util.query.QueryBuilder;
import com.asteroid.duck.jena.util.query.expressions.WhereExpression;
import org.apache.jena.query.Query;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryBuilderImplTest {

    @Test
    public void testSimpleFOAFQuery() throws InvalidQueryException {
        QueryBuilder subject = new QueryBuilderImpl();

        String query = subject.startQuery()
                .withPrefix("foaf", "http://xmlns.com/foaf/0.1/")
                .select("name")
                .where(new WhereExpression("?person foaf:name ?name"))
                .createQuery()
                .toString();

        assertNotNull(query);
        assertEquals("PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT ?name WHERE { ?person foaf:name ?name}", query);
    }

}