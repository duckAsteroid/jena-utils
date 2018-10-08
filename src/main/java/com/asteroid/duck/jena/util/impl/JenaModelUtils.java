package com.asteroid.duck.jena.util.impl;

import com.asteroid.duck.jena.util.StatementStream;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A helper class that can turn a jena model into a list
 */
public class JenaModelUtils {


    public static List<Statement> list(StmtIterator iter, int max) {
        return StatementStream.from(iter).limit(max).collect(Collectors.toList());
    }

    public static List<Statement> list(StmtIterator iter) {
        return StatementStream.from(iter).collect(Collectors.toList());
    }

    public static void dump(Stream<Statement> s, PrintWriter out) {
        s.map(statement -> statement.toString()).forEach(sl -> out.println(sl));
    }

    public static String toString(Stream<Statement> s) {
        StringWriter sw = new StringWriter();
        dump(s, new PrintWriter(sw));
        return sw.toString();
    }

}
