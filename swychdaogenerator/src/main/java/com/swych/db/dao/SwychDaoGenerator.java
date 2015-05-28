package com.swych.db.dao;

import java.io.IOException;

import javax.xml.stream.events.EntityReference;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class SwychDaoGenerator {

    private static String DAOLOCATION = "../app/src/main/java";
    private static String STRUCTURE = "Book_structure";
    private static String PHRASEREPLACEMENT = "Book_phrasereplacement";
    private static String BOOK = "book";

    public void buildSchemas(Schema schema){
        // Book Object
        Entity book = schema.addEntity(BOOK);
        book.addIdProperty();
        book.addStringProperty("name");
        book.addStringProperty("comment");
        // Book Structure.
//        schema.addEntity(STRUCTURE);
//        // Book Phrase replacement
//        schema.addEntity(PHRASEREPLACEMENT);



    }


    public static void main(String[] args) throws Exception {
        SwychDaoGenerator daoGen = new SwychDaoGenerator();
        Schema schema = new Schema(1, "com.swych.mobile.db");
        daoGen.buildSchemas(schema);
        new DaoGenerator().generateAll(schema,DAOLOCATION);
    }


}
