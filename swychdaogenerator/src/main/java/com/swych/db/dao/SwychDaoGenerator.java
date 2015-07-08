package com.swych.db.dao;

import java.io.IOException;

import javax.xml.stream.events.EntityReference;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class SwychDaoGenerator {

    private static String DAOLOCATION = "../app/src/main/java";
    private static String STRUCTURE = "Structure";
    private static String BOOKMARKS = "Bookmark";
    private static String SENTENCES = "Sentence";
    private static String MAPPINGS = "Mapping";
    private static String PHRASEREPLACEMENT = "PhraseReplacement";
    private static String BOOK = "Book";
    private static String VERSION = "Version";


    public void buildSchemas(Schema schema) {
        // Book table

        Entity book = schema.addEntity(BOOK);
        Property bookId = book.addLongProperty("id").primaryKey().autoincrement().getProperty();

        book.addStringProperty("title");
        book.addLongProperty("author_id").index();
        book.addStringProperty("author_name").index();
        book.addDateProperty("date").index();
        book.addStringProperty("imageUrl");
        // version table
        Entity version = schema.addEntity(VERSION);
        version.addIdProperty().notNull().autoincrement().primaryKey();
        version.addStringProperty("language").notNull();
        version.addDateProperty("date").notNull();
        version.addStringProperty("description").notNull();
        Property bookIdProperty = version.addLongProperty("book_id").notNull().getProperty();
        version.addStringProperty("title").notNull();
        version.addStringProperty("author").notNull();

        version.addToOne(book, bookIdProperty);
        ToMany bookVersions = book.addToMany(version, bookIdProperty);
        bookVersions.setName("bookVersions");


        // bookmarks table;
        Entity bookmarks = schema.addEntity(BOOKMARKS);
        bookmarks.addIdProperty().primaryKey().notNull();
        bookmarks.addIntProperty("mode").notNull();
        bookmarks.addIntProperty("book_position").notNull();
        bookmarks.addDateProperty("date").notNull();
        Property nativeLanguageVersionId = bookmarks.addLongProperty("version1_id").notNull().getProperty();
        bookmarks.addToOne(version, nativeLanguageVersionId, "nativeVersion");
        Property foreignLanguageVersionId = bookmarks.addLongProperty("version2_id").getProperty();
        bookmarks.addToOne(version, foreignLanguageVersionId, "foreignVersion");

        Index book_pairs = new Index();
        book_pairs.addProperty(nativeLanguageVersionId);
        book_pairs.addProperty(foreignLanguageVersionId);
        book_pairs.makeUnique();
        bookmarks.addIndex(book_pairs);


        // sentence tables;
        Entity bookSentences = schema.addEntity(SENTENCES);
        bookSentences.addIdProperty().primaryKey();
        Property sentenceIDProperty = bookSentences.addLongProperty("sentence_id").notNull().index().getProperty();
        bookSentences.addStringProperty("content").notNull();
        Property versionIdOfSentence = bookSentences.addLongProperty("version_id").notNull().getProperty();
        bookSentences.addToOne(version, versionIdOfSentence);
        ToMany sentences = version.addToMany(bookSentences, versionIdOfSentence);
        sentences.setName("sentences");
        sentences.orderAsc(sentenceIDProperty);

        //structure table
        Entity bookStructure = schema.addEntity(STRUCTURE);
        bookStructure.addIdProperty().primaryKey().autoincrement().notNull();
        Property positionInBookStructure = bookStructure.addLongProperty("position").getProperty();
        bookStructure.addStringProperty("content");
        Property versionIdBookStructure = bookStructure.addLongProperty("version_id").getProperty();
        bookStructure.addToOne(version, versionIdBookStructure);
        ToMany structure = version.addToMany(bookStructure, versionIdBookStructure);
        structure.setName("structure");
        structure.orderAsc(positionInBookStructure);

        Index bookStructureIndex = new Index();
        bookStructureIndex.addProperty(positionInBookStructure);
        bookStructureIndex.addProperty(versionIdBookStructure);
        bookStructureIndex = bookStructureIndex.makeUnique();
        bookStructure.addIndex(bookStructureIndex);

        //phrase replacement table.
        Entity phraseReplacements = schema.addEntity(PHRASEREPLACEMENT);
        phraseReplacements.addIdProperty().autoincrement().notNull().primaryKey();
        Property languageOfPhrase = phraseReplacements.addStringProperty("language").getProperty();
        phraseReplacements.addIntProperty("fromChar");
        phraseReplacements.addIntProperty("toChar");
        phraseReplacements.addStringProperty("content");
        Property sentenceContainingPhrase = phraseReplacements.addLongProperty("sentence_id").getProperty();
        phraseReplacements.addToOne(bookSentences, sentenceContainingPhrase);
        ToMany containedPhrases = bookSentences.addToMany(phraseReplacements, sentenceContainingPhrase);
        containedPhrases.setName("containedPhraseTranslation");
        containedPhrases.orderAsc(languageOfPhrase);

        // book_mapping table.
        Entity sentenceMappings = schema.addEntity(MAPPINGS);
        sentenceMappings.addIdProperty().notNull().primaryKey();
        sentenceMappings.addStringProperty("strMapping");

        nativeLanguageVersionId = sentenceMappings.addLongProperty("version1_id").getProperty();
        foreignLanguageVersionId = sentenceMappings.addLongProperty("version2_id").getProperty();
        sentenceMappings.addToOne(version, nativeLanguageVersionId, "nativeVersion");
        sentenceMappings.addToOne(version, foreignLanguageVersionId, "foreignVersion");
//*/

    }


    public static void main(String[] args) throws Exception {
        SwychDaoGenerator daoGen = new SwychDaoGenerator();
        Schema schema = new Schema(1, "com.swych.mobile.db");
        daoGen.buildSchemas(schema);
        new DaoGenerator().generateAll(schema, DAOLOCATION);
    }


}
