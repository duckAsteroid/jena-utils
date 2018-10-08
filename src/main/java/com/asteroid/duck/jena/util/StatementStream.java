/**
 * Copyright (c) 2017 Snap-on Business Solutions Ltd.
 */
package com.asteroid.duck.jena.util;

import org.apache.jena.rdf.model.*;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utilities to move from Model iteration operations into (non-parralel) streams
 */
public class StatementStream {

    /**
     * Given a statement iterator from a model - provide a parallel stream of statements
     * @param iterator the iterator
     * @return a stream of statements
     */
    public static Stream<Statement> from(StmtIterator iterator) {
        return iterator.toList().parallelStream();
    }

    public static Stream<Statement> fromSeq(StmtIterator iterator) {
        return iterator.toList().stream();
    }

    public static Stream<Statement> on(Model m, Resource subject, Property predicate, Resource object) {
        return from(m.listStatements(subject, predicate, object));
    }

    /**
     * Extract the subject from a statement. Useful to map to a stream of subjects
     * @param statement the statement to
     * @return
     */
    public static Resource subject(Statement statement) {
        return statement.getSubject();
    }

    public static Property predicate(Statement statement) {
        return statement.getPredicate();
    }

    public static RDFNode object(Statement statement) {
        return statement.getObject();
    }

    public static final Predicate<Statement> HAS_RESOURCE_OBJECT = ((Statement s) -> s.getObject().isResource());

    public static Resource objectResource(Statement statement) {
        return object(statement).asResource();
    }

    public static final Predicate<Statement> HAS_LITERAL_OBJECT = ((Statement s) -> s.getObject().isLiteral());

    public static Literal literalResource(Statement statement) {
        return object(statement).asLiteral();
    }




}
