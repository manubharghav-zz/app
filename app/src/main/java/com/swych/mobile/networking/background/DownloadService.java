package com.swych.mobile.networking.background;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.swych.mobile.MyApplication;
import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.commons.utils.StructType;
import com.swych.mobile.db.Book;
import com.swych.mobile.db.BookDao;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.Library;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.db.Mapping;
import com.swych.mobile.db.MappingDao;
import com.swych.mobile.db.PhraseReplacement;
import com.swych.mobile.db.PhraseReplacementDao;
import com.swych.mobile.db.Sentence;
import com.swych.mobile.db.SentenceDao;
import com.swych.mobile.db.Structure;
import com.swych.mobile.db.StructureDao;
import com.swych.mobile.db.Version;
import com.swych.mobile.db.VersionDao;
import com.swych.mobile.networking.Deserializer;
import com.swych.mobile.networking.Details;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.URLs;

import de.greenrobot.dao.query.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by manu on 6/21/15.
 */
public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_DUPLICATE = 3;
    public static final int STATUS_DELETED = 4;
    public static final int STATUS_REFRESHED = 5;

    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);


    private static final String TAG = "DownloadService";

    public DownloadService() {
        super(DownloadService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Download started");
        ActionType actionType = (ActionType) intent.getSerializableExtra("ActionType");

        switch (actionType) {
            case BOOK_DOWNLOAD:
                downloadBook(intent);
                break;
            case BOOK_DELETE:
                deleteLibraryItem(intent);
                break;
            case BOOK_REFRESH:
                syncLibraryItems(intent);

        }
    }

    private void syncLibraryItems(Intent intent){
        long[] libraryItemIds = intent.getLongArrayExtra("library_item_id");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        if(libraryItemIds==null || libraryItemIds.length <1){
            Bundle bundle = new Bundle();
            bundle.putString("message", "Invalid library Item id, please select a book");
            receiver.send(STATUS_ERROR,bundle);
        }

        final DaoSession session = MyApplication.getNewSession();
        LibraryDao libraryDao = session.getLibraryDao();
        StringBuffer messageBuffer = new StringBuffer();
        for(long libraryItemId: libraryItemIds) {
            Library libraryItem = libraryDao.load(libraryItemId);
            Book book = libraryItem.getSrcVersion().getBook();
            String bookTitle = book.getTitle();
            String srcLanguage = libraryItem.getSrcLanguage();
            String swychLanguage = libraryItem.getSwychLanguage();

            try {
                JSONObject srcLanguageBook = downloadData(URLs.VERSION + bookTitle + "/" +
                        srcLanguage);
                JSONObject swychLanguageBook = downloadData(URLs.VERSION + bookTitle + "/" +
                        swychLanguage);
                JSONObject mappings = null;
                JSONObject phrases = null;
                if (libraryItem.getMode2()) {
                    if(srcLanguage.compareTo(swychLanguage)>0){
                        mappings = downloadData(URLs.MAPPING + bookTitle + "/" + swychLanguage + "/" +
                                srcLanguage);
                    }
                    else{
                        mappings = downloadData(URLs.MAPPING + bookTitle + "/" + srcLanguage + "/" +
                                swychLanguage);
                    }
                }
                if (libraryItem.getMode1()) {

                    phrases = downloadData(URLs.PHRASES + bookTitle + "/" + srcLanguage + "/" +
                            swychLanguage);
                }
                //persist these objects to database;
                if (srcLanguageBook!=null && srcLanguageBook.length() > 0) {
                    Long srcVersionId = libraryItem.getSrcVersionId();
                    Long swychVersionId = libraryItem.getSwychVersionId();
                    Version srcVersion = libraryItem.getSrcVersion();
                    Date lastModifiedDateOnServer = df.parse(srcLanguageBook.getString
                            ("date_modified"));
                    if(lastModifiedDateOnServer.compareTo(srcVersion.getLast_modified_date()) >0){
                        srcVersion.setLast_modified_date(new Date());
                        srcVersion.update();
                        deleteVersionStructureandSentences(srcVersion,session);
                        addStructureandSentencesToDB(session, srcVersionId, srcLanguageBook
                                .getJSONArray("structure"));
                    }


                    if(swychVersionId != null) {
                        Version swychVersion = libraryItem.getSwychVersion();
                        lastModifiedDateOnServer = df.parse(swychLanguageBook.getString
                                ("date_modified"));
                        if(lastModifiedDateOnServer.compareTo(swychVersion.getLast_modified_date()) >0){
                            swychVersion.setLast_modified_date(new Date());
                            swychVersion.update();
                            deleteVersionStructureandSentences(swychVersion,session);
                            addStructureandSentencesToDB(session ,swychVersionId,
                                    swychLanguageBook.getJSONArray("structure"));
                        }
                    }


                    Log.d(TAG, bookTitle + " " + srcLanguage + "  " +
                            swychLanguage + " Book sync complete");
                    messageBuffer.append(bookTitle + " " + srcLanguage + "  " +
                            swychLanguage +"  "+ "Book sync complete");
                }
                else{
                    Log.d(TAG, bookTitle + " " + srcLanguage + "  " +
                            swychLanguage + " Error syncing the book: Couldnt download src " +
                            "Version");
                }


                if(swychLanguageBook!=null && mappings!=null) {
                    addMappingsToDB(session,libraryItemId,libraryItem.getSrcVersionId(),
                            libraryItem.getSwychVersionId(), srcLanguage, swychLanguage,mappings);
                }

                if (phrases != null) {
                    addPhrasesToDB(session,libraryItemId,libraryItem.getSrcVersionId(),swychLanguage,phrases);
                }
            } catch (JSONException | IOException | ParseException e) {
                Log.d(TAG, bookTitle + " " + srcLanguage + "  " +
                        swychLanguage + " Error syncing the book");
                messageBuffer.append(bookTitle + " " + srcLanguage + "  " +
                        swychLanguage + "  " + "Book sync incomplete");

            }
        }

    }


    private void deleteLibraryItem(Intent intent){
        long[] libraryItemIds = intent.getLongArrayExtra("library_item_id");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");


        if(libraryItemIds==null || libraryItemIds.length <1){
            Bundle bundle = new Bundle();
            bundle.putString("message", "Invalid library Item id, please select a book");
            receiver.send(STATUS_ERROR,bundle);
        }

        final DaoSession session = MyApplication.getNewSession();
        LibraryDao libraryDao = session.getLibraryDao();



        for(long libraryItemId:libraryItemIds ){
            Library libraryItem = libraryDao.load(libraryItemId);
            Book book = libraryItem.getSrcVersion().getBook();
            // delete mode 1 phrase replacements
            if(libraryItem.getMode1()){
                List<PhraseReplacement> phraseReplacements = libraryItem.getPhraseMappings();
                PhraseReplacement phraseReplacement = phraseReplacements.get(0);
                session.delete(phraseReplacement);
                Log.d(TAG,"deleted phrase replacements "+phraseReplacement.getVersion1_id());
            }


            //delete mode 2 sentence mappings
            if(libraryItem.getMode2()){
                List<Mapping> sentMappings = libraryItem.getSentenceMappings();
                session.delete(sentMappings.get(0));
                Log.d(TAG, "deleted sentence mappings" + sentMappings.get(0).getVersion1_id() + "" +
                        "  " +
                        "" + sentMappings.get(0).getVersion2_id());
            }

            session.delete(libraryItem);

            if(libraryItem.getSrcVersion().getSrcMappings().size()==0){
                // deleting sentences first and then the version itself.
                Version srcVersion = libraryItem.getSrcVersion();
                deleteCompleteVersion(srcVersion, session);
            }

            if(libraryItem.getMode2()|| libraryItem.getSwychVersionId()!=null){
                Version swychVersion = libraryItem.getSwychVersion();
                deleteCompleteVersion(swychVersion, session);
            }

            if(book.getBookVersions().size()==0){
                Log.d(TAG,"Deleting book " + book.getTitle() + "  " + book.getAuthor_name());
                session.delete(book);
            }


            Log.d(TAG, "Book delete completed. ");
        }




        Bundle bundle = new Bundle();
        bundle.putString("message", "bookId's deleted");
        receiver.send(STATUS_FINISHED,bundle);
    }

    private void deleteCompleteVersion(Version version, final DaoSession session){
        deleteVersionStructureandSentences(version,session);
        session.delete(version);
        Log.d(TAG, "Deleted source Version : " + version.getTitle() + "  " + version.getLanguage());
    }

    private void deleteVersionStructureandSentences(Version version, final DaoSession session){
        final List<Sentence> sentences = version.getSentences();
        final List<Structure> structureList = version.getStructure();
        session.runInTx(new Runnable() {
            public void run() {
                for (Sentence sent : sentences) {
                    session.delete(sent);
                }
                for (Structure struct : structureList) {
                    session.delete(struct);
                }
            }
        });
        Log.d(TAG,"Deleted Version sentence and structures : "+version.getTitle() + "  " +
                version
                .getLanguage());
    }


    private void downloadBook(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        DisplayBookObject displayBook = (DisplayBookObject) intent.getSerializableExtra("book");
        String bookName = displayBook.getTitle();
        String srcLanguage = intent.getStringExtra("nativeLanguage");
        String swychLanguage = intent.getStringExtra("foreignLanguage");
        boolean isMode1Present = intent.getBooleanExtra("isMode1Present", false);
        boolean isMode2Present = intent.getBooleanExtra("isMode2Present", false);

        Log.i(TAG, "Downloading book: " + bookName + " srcLang: " + srcLanguage + " swychLang: "
                + swychLanguage + " M1:" + isMode1Present + " M2:" + isMode2Present);

        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        DaoSession session = MyApplication.getSession();


        if (checkIfPresentInLibrary(displayBook.getTitle(),srcLanguage,swychLanguage,session)) {
            Log.d(TAG, "Book" + bookName + "already present in the library.");
            receiver.send(STATUS_DUPLICATE, bundle);
            return;
        }


        try {
            JSONObject srcLanguageBook = downloadData(URLs.VERSION + bookName + "/" + srcLanguage);
            JSONObject swychLanguageBook = downloadData(URLs.VERSION + bookName + "/" + swychLanguage);
            JSONObject mappings = null;
            JSONObject phrases = null;
            if (isMode2Present) {
                if(srcLanguage.compareTo(swychLanguage)>0){
                    mappings = downloadData(URLs.MAPPING + bookName + "/" + swychLanguage + "/" +
                            srcLanguage);
                }
                else{
                    mappings = downloadData(URLs.MAPPING + bookName + "/" + srcLanguage + "/" +
                            swychLanguage);
                }
            }
            if (isMode1Present) {

                phrases = downloadData(URLs.PHRASES + bookName + "/" + srcLanguage + "/" +
                        swychLanguage);
            }
            //persist these objects to database;
            if (srcLanguageBook!=null && srcLanguageBook.length() > 0) {
                Log.d(TAG,"persisting the book to database");
                persistToDb(displayBook, srcLanguage, srcLanguageBook, swychLanguage, swychLanguageBook,
                        mappings, phrases);

                //                BookDao bookDao = daoSession.getBookDao();
                //                Book book = new Book();

                bundle.putString("message", "Book download complete");
                receiver.send(STATUS_FINISHED, bundle);
            }
            else{
                bundle.putString("message", "Error Downloading the book");
                receiver.send(STATUS_ERROR, bundle);
            }
        } catch (JSONException | IOException | ParseException   e) {
            Log.d(TAG, "Error downloading the book " + e.toString());
            bundle.putString("message", "Error Downloading the book");
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    private JSONObject downloadData(String requestUrl) throws IOException, JSONException {
        // utility method to download book.
        //todo switch to volley if possible. Since its from a service we could do without volley
        // for now.
        Log.d(TAG, "Downloading from " + requestUrl);
        HttpURLConnection urlConnection ;

        /* forming th java.net.URL object */
        URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        /* optional request header */
        urlConnection.setRequestProperty("Authorization", Details.getBasicAuth());


        urlConnection.setRequestMethod("GET");
        int statusCode = urlConnection.getResponseCode();


        if (statusCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection
                    .getInputStream()));
            StringBuffer responceBuffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                responceBuffer.append(line);
            }
            reader.close();
            JSONObject response = new JSONObject(responceBuffer.toString());
            Log.d(TAG, "Download successful, URL: " + url);
            return response;


        } else {
            Log.d(TAG, "download failed with response " + statusCode);
        }
        return null;
    }


    private boolean persistToDb(DisplayBookObject displayBook, String srcLanguage, JSONObject
            srcLanguageBook, String swychLanguage, JSONObject swychLanguageBook, JSONObject
            mappings, JSONObject phrases) throws JSONException, ParseException {



        //        Initialize DAO' here
        DaoSession session = MyApplication.getNewSession();
        BookDao bookDao = session.getBookDao();
        Long libraryItemId;


        Book book = new Book(null,displayBook.getTitle(),(long)1, displayBook.getNativeVersion()
                .getAuthor(),new Date(),displayBook.getImageUrl());

        Long bookId;
        try {
            bookId = session.insert(book);
            Log.d(TAG, "Book persisted with bookId= " + bookId);
        } catch (Exception e) {
            bookId = bookDao.queryBuilder().where(BookDao.Properties.Title.eq(displayBook
                    .getTitle())).list().get(0).getId();
            book = bookDao.load(bookId);
            book.setLast_modified_date(new Date());
            book.update();
            Log.d(TAG, "Book " + displayBook.getTitle() + " saved with ID: " + bookId);
        }


        long startTime = System.currentTimeMillis();

        // create source version
        Long srcVersionId = addVersionToDB(session,bookId,srcLanguage,srcLanguageBook,displayBook
                .getVersion(Language.getLongVersion(srcLanguage)));

        Long swychVersionId=null;
        if(swychLanguageBook != null) {
            swychVersionId = addVersionToDB(session,bookId,swychLanguage,swychLanguageBook,displayBook
                    .getVersion(Language.getLongVersion(swychLanguage)));
        }

        Library item = new Library(null, displayBook.getImageUrl(), displayBook.isMode1Present(),
                displayBook.isMode2Present(),displayBook.getTitle(), displayBook
                .getVersion(Language.getLongVersion(srcLanguage)).getTitle(), displayBook
                .getVersion(Language.getLongVersion(srcLanguage)).getAuthor(), srcLanguage,
                        swychLanguage, srcVersionId, swychVersionId, new Date());
        try {
            session.insert(item);
        } catch (Exception e) {
            Log.e(TAG, "Book already present");
            return true;
        }
        libraryItemId = item.getId();

        if(swychLanguageBook!=null && mappings!=null) {
            addMappingsToDB(session,libraryItemId,srcVersionId,swychVersionId,srcLanguage,
                    swychLanguage,mappings);
        }

        if (phrases != null) {
            addPhrasesToDB(session,libraryItemId,srcVersionId,swychLanguage,phrases);

        }

        Log.i(TAG, "took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds for " +
                "book insert");


        return true;
    }


    private void addPhrasesToDB(final DaoSession session, long libraryItemId, long srcVersionId,
                                String swychLanguage, JSONObject phraseReplacements) throws
            JSONException, ParseException {
        Date replacementsLastModifiedDateonServer = df.parse(phraseReplacements.getString
                ("date_modified"));
        PhraseReplacement phraseReplacementsOnPhone = checkIfPhraseTranslationsExists(session,
                libraryItemId);
        if (phraseReplacementsOnPhone != null) {
            if (phraseReplacementsOnPhone.getLast_modified_date().compareTo
                    (replacementsLastModifiedDateonServer) > 0) {
                return;
            } else {
                phraseReplacementsOnPhone.delete();
            }
        }

        PhraseReplacement replacement = new PhraseReplacement(null, new Date(), swychLanguage,
                phraseReplacements.get("phrase_replacements").toString(), srcVersionId,
                libraryItemId);
        session.insert(replacement);

    }

    private void addMappingsToDB(final DaoSession session, long libraryItemId, long srcVersionId,
                                 long swychVersionId, String srcLanguage, String targetLanguage,
                                 JSONObject mappings) throws JSONException, ParseException {
        Date mappingLastModifiedDateonServer = df.parse(mappings.getString("date_modified"));
        Mapping mappingOnPhone = checkIfMappingExists(session, libraryItemId);
        if (mappingOnPhone != null) {
            if (mappingOnPhone.getLast_modified_date().compareTo(mappingLastModifiedDateonServer)
                    > 0) {
                return;
            } else {
                mappingOnPhone.delete();
            }
        }

        String parsedMappings = Deserializer.parseMappings(mappings.getString("mapping"),
                srcLanguage, targetLanguage);
        Mapping mapping = new Mapping(null, parsedMappings, new Date(), srcVersionId,
                swychVersionId, libraryItemId);
        session.insert(mapping);

    }


    private Long addVersionToDB(final DaoSession session,long bookId, String language,
                                JSONObject versionFromServer, DisplayBookObject.Version
                                        versionMetaData) throws JSONException,
            ParseException{
        Long versionId = null;
        if(df==null){
            df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        }

        Date lastModifiedDateOnServer = df.parse(versionFromServer.getString("date_modified"));
        Version ExistingVersionOnPhone = checkIfVersionExists(session, bookId, language);
        if(ExistingVersionOnPhone!=null) {
            versionId = ExistingVersionOnPhone.getId();
            if (lastModifiedDateOnServer.compareTo(ExistingVersionOnPhone.getLast_modified_date()) <= 0) {

                return ExistingVersionOnPhone.getId();
            } else if (lastModifiedDateOnServer.compareTo(ExistingVersionOnPhone.getLast_modified_date()) > 0) {
                ExistingVersionOnPhone.setLast_modified_date(new Date());
                ExistingVersionOnPhone.update();
                deleteVersionStructureandSentences(ExistingVersionOnPhone, session);
            }
        }
        else{
            Version srcVersion = new Version(null, language, new Date(), versionMetaData
                    .getDescription(), bookId, versionMetaData.getTitle(), versionMetaData.getAuthor());
            versionId = session.insert(srcVersion);
        }

        addStructureandSentencesToDB(session, versionId, versionFromServer.getJSONArray("structure"));

        return versionId;
    }
    private void addStructureandSentencesToDB(final DaoSession session, final long VersionId, final JSONArray
            structureList) {
        final StructureDao structureDao = session.getStructureDao();
        final SentenceDao sentenceDao = session.getSentenceDao();
        session.runInTx(new Runnable() {
            public void run() {
                try {
                    int structureType;
                    int sentenceId = -1;
                    JSONObject sentenceJson;
                    for (int i = 0; i < structureList.length(); i++) {
                        JSONObject structureJson = structureList.getJSONObject(i);
                        structureType = StructType.valueOf(structureJson.getString("type"))
                                .getCode();
                        if (!structureJson.isNull("sentence")) {
                            sentenceJson = structureJson.getJSONObject("sentence");
                            sentenceId = sentenceJson.getInt("sentence_id");
                            String sentString = sentenceJson.getString("content");
                            Sentence sentence = new Sentence(null, sentenceId, sentString,
                                    VersionId);
                            sentenceDao.insert(sentence);
                        }
                        Structure structure = new Structure(null, (long) i, (long) sentenceId,
                                structureType, VersionId);
                        structureDao.insert(structure);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Exception occured while adding structure to db.");
                }
            }
        });

    }



    private boolean checkIfPresentInLibrary(String title, String srcLanguage, String
            swychLanguage,final DaoSession session){
        LibraryDao libraryDao = session.getLibraryDao();
        QueryBuilder qb = libraryDao.queryBuilder();
        int t = qb.where(LibraryDao.Properties.Book_title.eq(title), LibraryDao.Properties
                .SrcLanguage.eq(srcLanguage), LibraryDao.Properties.SwychLanguage.eq
                (swychLanguage)).list().size();
        return t > 0;
    }


    private Version checkIfVersionExists(final DaoSession session,long bookId, String
            language){
        VersionDao versionDao = session.getVersionDao();
        QueryBuilder qb = versionDao.queryBuilder();
        List<Version> versions = qb.where(VersionDao.Properties.Book_id.eq(bookId), VersionDao
                .Properties.Language.eq(language)).list();
        if(versions.size()>0){
            return versions.get(0);
        }

        return null;
    }

    private Mapping checkIfMappingExists(final DaoSession session, long libraryItemId){
        MappingDao mappingDao = session.getMappingDao();
        QueryBuilder qb = mappingDao.queryBuilder();
        List<Mapping> mappings = qb.where(MappingDao.Properties.Library_item_mapping.eq
                (libraryItemId)).list();
        if(mappings.size()>0){
            return mappings.get(0);
        }
        return null;
    }

    private PhraseReplacement checkIfPhraseTranslationsExists(final DaoSession session, long libraryItemId){
        PhraseReplacementDao phraseReplacementDao = session.getPhraseReplacementDao();
        QueryBuilder qb = phraseReplacementDao.queryBuilder();
        List<PhraseReplacement> replacements = qb.where(PhraseReplacementDao.Properties.Library_id.eq
                (libraryItemId)).list();
        if(replacements.size()>0){
            return replacements.get(0);
        }
        return null;
    }

}
