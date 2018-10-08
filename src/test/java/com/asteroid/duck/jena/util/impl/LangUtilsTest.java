package com.asteroid.duck.jena.util.impl;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

public class LangUtilsTest {
    private static final Resource TEST = setupTestResource();

    private static Resource setupTestResource() {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.getResource("http://test.org/testResource");
        // add some properties
        resource.addProperty(RDFS.label, "UK English", "en-GB");
        resource.addProperty(RDFS.label, "English", "en");
        resource.addProperty(RDFS.label, "default", "");
        return resource;
    }

    @Test
    public void searchPath() throws Exception {
        List<Locale> locales = LangUtils.searchPath(Locale.UK);
        Assert.assertNotNull(locales);
        Assert.assertFalse(locales.isEmpty());
        Assert.assertEquals(3, locales.size());
        Assert.assertEquals(Locale.UK, locales.get(0));
        Assert.assertEquals(Locale.ENGLISH, locales.get(1));
        Assert.assertEquals(Locale.ROOT, locales.get(2));

        locales = LangUtils.searchPath(null);
        Assert.assertEquals(1, locales.size());
        Assert.assertEquals(Locale.ROOT, locales.get(0));
    }

    @Test
    public void getCodes() throws Exception {
        List<String> codes = LangUtils.getCodes(Locale.UK);
        Assert.assertNotNull(codes);
        Assert.assertEquals(3, codes.size());
        Assert.assertEquals("en-GB", codes.get(0));
        Assert.assertEquals("en", codes.get(1));
        Assert.assertEquals("", codes.get(2));

        codes = LangUtils.getCodes(null);
        Assert.assertEquals(1, codes.size());
        Assert.assertEquals("", codes.get(0));
    }

    @Test
    public void getCode() throws Exception {
        Assert.assertEquals("en", LangUtils.getCode(Locale.UK, false));
        Assert.assertEquals("en-GB", LangUtils.getCode(Locale.UK, true));
        Assert.assertEquals("en", LangUtils.getCode(Locale.ENGLISH, true));

        Assert.assertEquals("", LangUtils.getCode(null, true));
    }


    @Test
    public void getLocaleString() throws Exception {
        final String notFound = "Not found";
        Assert.assertEquals(null, LangUtils.getLocaleString(TEST, RDFS.comment, Locale.UK));
        Assert.assertEquals("UK English", LangUtils.getLocaleString(TEST, RDFS.label, Locale.UK));
        Assert.assertEquals("English", LangUtils.getLocaleString(TEST, RDFS.label, Locale.US));
        Assert.assertEquals("default", LangUtils.getLocaleString(TEST, RDFS.label, Locale.SIMPLIFIED_CHINESE));

        Assert.assertEquals("default", LangUtils.getLocaleString(TEST, RDFS.label, null));

        Assert.assertEquals(null, LangUtils.getLocaleString(TEST, RDFS.comment, null));
    }


    @Test
    public void notEmpty() throws Exception {
        Assert.assertTrue(LangUtils.notEmpty("wibble"));
        Assert.assertFalse(LangUtils.notEmpty(""));
        Assert.assertFalse(LangUtils.notEmpty(null));
    }

}