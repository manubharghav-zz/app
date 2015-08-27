package com.swych.db.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;
import de.greenrobot.daogenerator.ToOne;

public class SwychDaoGenerator {

    private static String DAOLOCATION = "../app/src/main/java";
    private static String STRUCTURE = "Structure";
    private static String BOOKMARKS = "Bookmark";
    private static String SENTENCES = "Sentence";
    private static String MAPPINGS = "Mapping";
    private static String PHRASEREPLACEMENT = "PhraseReplacement";
    private static String BOOK = "Book";
    private static String VERSION = "Version";
    private static String LIBRARY = "Library";

    public void buildSchemas(Schema schema) {
        // Book table

        schema.enableKeepSectionsByDefault();

        Entity book = schema.addEntity(BOOK);
        book.addIdProperty();
        Property bookTitle = book.addStringProperty("title").notNull().getProperty();

        Index titleIndex = new Index();
        titleIndex.addProperty(bookTitle);
        titleIndex.makeUnique();
        book.addIndex(titleIndex);
        book.addLongProperty("author_id").index();
        book.addStringProperty("author_name").index();
        book.addDateProperty("date").index();
        book.addStringProperty("imageUrl");
        // version table
        Entity version = schema.addEntity(VERSION);
        version.addIdProperty();
        version.addStringProperty("language").notNull();
        version.addDateProperty("date").notNull();
        version.addStringProperty("description");
        Property bookIdProperty = version.addLongProperty("book_id").notNull().getProperty();
        version.addStringProperty("title").notNull();
        version.addStringProperty("author").notNull();

        version.addToOne(book, bookIdProperty);
        ToMany bookVersions = book.addToMany(version, bookIdProperty);
        bookVersions.setName("bookVersions");

        // user library table;
        Entity library = schema.addEntity(LIBRARY);
        library.addIdProperty();
        Property srcVersionProperty = library.addLongProperty("srcVersionId").getProperty();
        Property swychVersionProperty = library.addLongProperty("swychVersionId").getProperty();
        library.addToOne(version,srcVersionProperty,"srcVersion");
        ToMany srcMappings = version.addToMany(library, srcVersionProperty);
        srcMappings.setName("srcMappings");

        library.addToOne(version, swychVersionProperty,"swychVersion");
        ToMany swychMappings = version.addToMany(library, swychVersionProperty);
        swychMappings.setName("swychMappings");

        library.addStringProperty("srcLanguage");
        library.addStringProperty("swychLanguage");
        library.addStringProperty("title");


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
        bookStructure.addIdProperty().primaryKey().autoincrement();
        Property positionInBookStructure = bookStructure.addLongProperty("position").getProperty();
        bookStructure.addLongProperty("sentenceId");
        bookStructure.addIntProperty("type").notNull();
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
        phraseReplacements.addIdProperty().autoincrement();
        Property languageOfPhrase = phraseReplacements.addStringProperty("language").getProperty();
        phraseReplacements.addStringProperty("phrases");
        nativeLanguageVersionId = phraseReplacements.addLongProperty("version1_id").getProperty();
        foreignLanguageVersionId = phraseReplacements.addLongProperty("version2_id").getProperty();
        Property libraryItemProperty = phraseReplacements.addLongProperty("library_id").getProperty();


        phraseReplacements.addToOne(library,libraryItemProperty);
        ToMany phraseMappings= library.addToMany(phraseReplacements,libraryItemProperty);
        phraseMappings.setName("phraseMappings");
        phraseReplacements.addToOne(version, nativeLanguageVersionId, "nativeVersion");
        phraseReplacements.addToOne(version, foreignLanguageVersionId, "foreignVersion");


        // book_mapping table.
        Entity sentenceMappings = schema.addEntity(MAPPINGS);
        sentenceMappings.addIdProperty().autoincrement();
        sentenceMappings.addStringProperty("strMapping");
        sentenceMappings.addDateProperty("date").notNull();
        nativeLanguageVersionId = sentenceMappings.addLongProperty("version1_id").getProperty();
        foreignLanguageVersionId = sentenceMappings.addLongProperty("version2_id").getProperty();
        sentenceMappings.addToOne(version, nativeLanguageVersionId, "nativeVersion");
        sentenceMappings.addToOne(version, foreignLanguageVersionId, "foreignVersion");
        libraryItemProperty = sentenceMappings.addLongProperty("library_item_mapping").getProperty();


        sentenceMappings.addToOne(library,libraryItemProperty);
        ToMany sentenceMappingProperty = library.addToMany(sentenceMappings,libraryItemProperty);
        sentenceMappingProperty.setName("sentenceMappings");

//*/



    }


    public static void main(String[] args) throws Exception {
        SwychDaoGenerator daoGen = new SwychDaoGenerator();
        Schema schema = new Schema(1, "com.swych.mobile.db");
        daoGen.buildSchemas(schema);
        new DaoGenerator().generateAll(schema, DAOLOCATION);
    }


}
