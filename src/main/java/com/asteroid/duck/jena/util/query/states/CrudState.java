package com.asteroid.duck.jena.util.query.states;

import com.asteroid.duck.jena.util.query.clauses.Delete;
import com.asteroid.duck.jena.util.query.clauses.Insert;
import com.asteroid.duck.jena.util.query.clauses.Select;
import com.asteroid.duck.jena.util.query.clauses.Update;

/**
 * A query state where only certain operations can be applied.<br>
 * See {@link Insert}, {@link Select}, {@link Update}, {@link Delete}.
 *
 * @author Claudiu Blajan
 */
public interface CrudState extends Insert, Select, Update, Delete {

}
