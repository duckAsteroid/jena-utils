
package com.asteroid.duck.jena.util.impl;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;

import java.util.*;

/**
 * Some helper methods for working with language codes
 */
public class LangUtils {
    /**
     * steal search path code from Java resource bundle
     */
    private static final ResourceBundle.Control HELPER = new ResourceBundle.Control() {
    };

    /**
     * An ordered list of candidate locales for the given locale
     *
     * @param l the locale
     * @return candidate locales
     */
    public static List<Locale> searchPath(Locale l) {
        if (l == null) {
            return Collections.singletonList(Locale.ROOT);
        }
        return HELPER.getCandidateLocales("", l);
    }


    /**
     * An unmodifiable collection of RFC3066 language codes for the given locale
     *
     * @param l the Java locale
     * @return the language codes
     */
    public static List<String> getCodes(Locale l) {
        // FIXME we should use ICU4J and do this properly
        List<String> codes = new ArrayList<>();
        for (Locale option : searchPath(l)) {
            String code = getCode(option, true);
            if (!codes.contains(code)) {
                codes.add(code);
            }
        }
        return Collections.unmodifiableList(codes);
    }

    /**
     * Generate an RFC 3066 style language code for a given locale
     *
     * @param l              the locale
     * @param includeCountry should we bother with country (if there is one)
     * @return the language code
     */
    public static String getCode(Locale l, boolean includeCountry) {
        StringBuilder result = new StringBuilder();
        if (l != null) {
            String lang = l.getLanguage();
            if (notEmpty(lang)) {
                if (result.length() > 0) {
                    result.append('-');
                }
                result.append(lang);
            }
            if (includeCountry) {
                String country = l.getCountry().toUpperCase();
                if (notEmpty(country)) {
                    if (result.length() > 0) {
                        result.append('-');
                    }
                    result.append(country);
                }
            }
        }
        return result.toString();
    }

    /**
     * Get the rdfs:label from an RDF resource in the best match for the given locale.
     * Falling back to the resource URI if no label found
     * Falling back to the string "null" if the resource is null
     *
     * @param resource the rdf resource
     * @param locale   the desired locale
     * @return the best match label for the rdfs:label of the given resource
     */
    public static String getRdfsLabel(Resource resource, Locale locale) {
        String label = getLabelProperty(resource, RDFS.label, locale);
        return label != null ? label : resource.getURI();
    }

    public static String getLabelProperty(Resource resource, Property property, Locale locale) {
        if (resource != null) {
            return getLocaleString(resource, property, locale);
        }
        return resource.getURI();
    }

    /**
     * Extract an internationalised string from a {@link Property} of an RDF {@link Resource}
     *
     * @param rdfResource  the RDF resource (e.g. ontology class)
     * @param propertyName the property name (e.g. RDFS.label)
     * @param locale       the locale sought
     * @return the best match string for the locale (or the default)
     */
    public static String getLocaleString(Resource rdfResource, Property propertyName, Locale locale) {
        if (rdfResource != null && propertyName != null) {
            // work through language codes
            if (locale != null) {
                for (String langCode : LangUtils.getCodes(locale)) {
                    Statement labelStatement = rdfResource.getProperty(propertyName, langCode);
                    if (labelStatement != null) {
                        return labelStatement.getLiteral().getString();
                    }
                }
            }
            // try without a language
            Statement labelStatement = rdfResource.getProperty(propertyName);
            if (labelStatement != null) {
                return labelStatement.getLiteral().getString();
            }
        }
        // fallback
        return null;
    }

    /**
     * Extract all internationalised strings for instances of a {@link Property} of an RDF {@link Resource}
     *
     * @param rdfResource the RDF resource (e.g. ontology class)
     * @param property    the RDF property (e.g. RDFS.label)
     * @param locale      the locale sought
     * @return the best match strings for the locale (may be empty)
     */
    public static Iterable<String> getLocaleStrings(Resource rdfResource, Property property, Locale locale) {
        if (rdfResource != null && property != null) {
            ArrayList<String> results = new ArrayList<>();
            // work through language codes
            for (String langCode : LangUtils.getCodes(locale)) {
                Statement labelStatement = rdfResource.getProperty(property, langCode);
                if (labelStatement != null) {
                    results.add(labelStatement.getLiteral().getString());
                }
            }
            if (results.isEmpty()) {
                // try without a language
                Statement labelStatement = rdfResource.getProperty(property);
                if (labelStatement != null) {
                    results.add(labelStatement.getLiteral().getString());
                }
            }
            return results;
        }
        // fallback
        return Collections.emptyList();
    }

    public static boolean notEmpty(final String lang) {
        return lang != null && lang.length() > 0;
    }
}
