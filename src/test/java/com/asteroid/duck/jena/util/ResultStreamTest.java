package com.asteroid.duck.jena.util;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultStreamTest {

    private static final String TEST_NS = "http://example.org/test#";
    private static final String[] EXPECTED_KEYS = new String[]{ "a", "b", "c"};
    private static final String[] EXPECTED_A = new String[]{ TEST_NS + "A", TEST_NS + "B", TEST_NS + "C", TEST_NS + "D", TEST_NS + "E"};
    private static final Double[] EXPECTED_B = new Double[]{ 0.0, 1.0, 1.0, 2.0, 2.0};
    private static final String[] EXPECTED_C = new String[]{ "A", "B", "C", "D", "E"};

    private final ResultSet testResultSet() {
        Dataset dataset = DatasetFactory.createTxnMem();
        RDFDataMgr.read(dataset.getDefaultModel(), ResultStreamTest.class.getResourceAsStream("/test.ttl"), Lang.TTL);
        Query query = QueryFactory.create(
                "PREFIX test: <"+TEST_NS+"> \n " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?a ?b ?c \n" +
                "WHERE { \n" +
                        "?a a test:Class . \n" +
                        "?a test:weight ?b . \n" +
                        "?a rdfs:label ?c . \n" +
                        "} ");
        try(QueryExecution exec = QueryExecutionFactory.create(query, dataset))
        {
            return ResultSetFactory.copyResults(exec.execSelect());
        }
    }

    @Test
    public void streamRawVariable() {
        List<String> results = ResultStream.streamRawVariable(testResultSet(), "a")
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(Resource::getURI)
                .collect(Collectors.toList());
        assertResultEqual(EXPECTED_A, results);
    }

    @Test
    public void streamRawVariables() {
        List<Map<String, RDFNode>> results = ResultStream.streamRawVariables(testResultSet())
                .collect(Collectors.toList());
        String[] aValues = new String[EXPECTED_A.length];
        Double[] bValues = new Double[EXPECTED_B.length];
        String[] cValues = new String[EXPECTED_C.length];
        for (Map<String, RDFNode> result : results) {
            assertEquals(3, result.size());
            assertResultEqual(EXPECTED_KEYS, result.keySet());
            RDFNode aNode = result.get("a");
            assertTrue(aNode.isResource());
            Resource aRes = aNode.asResource();
            String a = aRes.getURI();
            int index = Arrays.binarySearch(EXPECTED_A, a);
            aValues[index] = a;

            RDFNode bNode = result.get("b");
            assertTrue(bNode.isLiteral());
            Literal bLit = bNode.asLiteral();
            assertEquals(XSDDatatype.XSDdouble, bLit.getDatatype());
            Double b = bLit.getDouble();
            bValues[index] = b;

            RDFNode cNode = result.get("c");
            assertTrue(cNode.isLiteral());
            Literal cLit = cNode.asLiteral();
            assertEquals(XSDDatatype.XSDstring, cLit.getDatatype());
            String c = cLit.getString();
            cValues[index] = c;
        }
        assertResultEqual(EXPECTED_A, Arrays.asList(aValues));
        assertResultEqual(EXPECTED_B, Arrays.asList(bValues));
        assertResultEqual(EXPECTED_C, Arrays.asList(cValues));
    }

    public static <T> void assertResultEqual(T[] expected, Collection<T> actual) {
        assertEquals(expected.length, actual.size());
        for(T a : expected) {
            assertTrue(actual.contains(a));
        }
    }
}