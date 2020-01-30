/**
 * Copyright (c) 2017 Snap-on Business Solutions Ltd.
 */
package com.asteroid.duck.jena.util;

import com.asteroid.duck.jena.util.impl.TinyReadOnlyMap;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities to turn a set of results from Jena into a stream of objects
 */
public class ResultStream {

    /** SLF4J Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ResultStream.class);

    /**
     * Given a variable name and a result set produce a stream of resources
     * @param resultSet
     * @param keyVariable
     * @return
     */
    public static Stream<Resource> streamResources(final ResultSet resultSet, final String keyVariable) {
        return streamRawVariable(resultSet, keyVariable)
                .filter(node -> node.isResource())
                .map(node -> node.asResource());
    }

    /**
     * Given a variable name and a result set produce a stream of resource URIs
     * @param resultSet
     * @param keyVariable
     * @return
     */
    public static Stream<String> streamResourceURIs(final ResultSet resultSet, final String keyVariable) {
        return streamResources(resultSet, keyVariable)
                .filter(Objects::nonNull)
                .map(resource -> resource.getURI());
    }

    /**
     * Given a variable name and a result set produce a stream of literal values
     * @param resultSet
     * @param keyVariable
     * @return
     */
    public static Stream<Literal> streamLiteral(final ResultSet resultSet, final String keyVariable) {
        return streamRawVariable(resultSet, keyVariable)
                .filter(node -> node.isLiteral())
                .map(node -> node.asLiteral());
    }

    /**
     * Given a variable name and a result set produce a (non-parallel) stream of abstract RDFNodes
     * @param resultSet the result set
     * @param keyVariable the variable to extract from the result set
     * @return a stream of RDF nodes
     */
    public static Stream<RDFNode> streamRawVariable(final ResultSet resultSet, final String keyVariable) {
        if (!resultSet.getResultVars().contains(keyVariable)) {
            LOG.warn("No variable '"+keyVariable+"' in ResultSet vars");
        }
        Iterator<RDFNode> iterator = new Iterator<RDFNode>() {
            @Override
            public boolean hasNext() {
                return resultSet.hasNext();
            }

            @Override
            public RDFNode next() {
                QuerySolution solution = resultSet.next();
                return solution.get(keyVariable);
            }
        };
        Spliterator<RDFNode> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * Given a ResultSet produce a (non-parallel) stream of {@link Map} instances containing the
     * value of all variables in the query for each result.
     * @param resultSet the result set
     * @return a stream of maps corresponding to the results
     */
    public static Stream<Map<String, RDFNode>> streamRawVariables(final ResultSet resultSet) {
        final List<String> keys = resultSet.getResultVars();

        return stream(resultSet).map(querySolution -> {
            RDFNode[] values = new RDFNode[keys.size()];
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                RDFNode value = querySolution.get(key);
                values[i] = value;
            }
            return new TinyReadOnlyMap<>(keys, Arrays.asList(values));
        });
    }

    /**
     * Convert a result set into a stream. If the result set is {@link ResultSetRewindable rewindable} then the size
     * is offered as a hint to the spliterator.
     * @param resultSet The result set to stream
     * @return a stream of {@link QuerySolution}s
     */
    public static Stream<QuerySolution> stream(final ResultSet resultSet) {
        Spliterator<QuerySolution> spliterator;
        if(resultSet instanceof ResultSetRewindable) {
            spliterator = Spliterators.spliterator(resultSet, ((ResultSetRewindable)resultSet).size(), 0);
        } else {
            spliterator = Spliterators.spliteratorUnknownSize(resultSet, 0);
        }
        return StreamSupport.stream(spliterator, false);
    }
}
