package com.swych.mobile.networking.background;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.swych.mobile.MyApplication;
import com.swych.mobile.activity.util.SystemUiHider;
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
import com.swych.mobile.db.Sentence;
import com.swych.mobile.db.SentenceDao;
import com.swych.mobile.db.Structure;
import com.swych.mobile.db.StructureDao;
import com.swych.mobile.db.Version;
import com.swych.mobile.networking.Deserializer;
import com.swych.mobile.networking.Details;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

/**
 * Created by manu on 6/21/15.
 */
public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_DUPLICATE = 3;

    private DownloadType downloadType;


    private static final String TAG = "DownloadService";

    public DownloadService() {
        super(DownloadService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Download started");
        downloadType = (DownloadType) intent.getSerializableExtra("DownloadType");

        switch (downloadType) {
            case BOOK:
                downloadBook(intent);
                break;


        }
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
        LibraryDao libraryDao = session.getLibraryDao();
        List<Library> list = libraryDao.queryBuilder().where(LibraryDao.Properties.Title.eq
                (bookName)).where(LibraryDao.Properties.SrcLanguage.eq(srcLanguage)).where
                (LibraryDao.Properties.SwychLanguage.eq(swychLanguage)).list();

        if (list.size() > 0) {
            Log.d(TAG, "Book" + bookName + "already present in the library.");
            receiver.send(STATUS_DUPLICATE, bundle);
            return;
        }


        try {
            JSONObject srcLangBook = downloadData(URLs.VERSION + bookName + "/" + srcLanguage);
            JSONObject tarLangBook = downloadData(URLs.VERSION + bookName + "/" + swychLanguage);
            JSONObject mappings = null;
            JSONObject phrases = null;
            if (isMode2Present) {
                mappings = downloadData(URLs.MAPPING + bookName + "/" + srcLanguage + "/" +
                        swychLanguage);
            }
            if (isMode1Present) {

                phrases = downloadData(URLs.PHRASES + bookName + "/" + srcLanguage + "/" +
                        swychLanguage);
            }
            //persist these objects to database;
            if (srcLangBook.length() > 0) {
                Log.d(TAG,"persisting the book to database");
                persistToDb(displayBook, srcLanguage, srcLangBook, swychLanguage, tarLangBook,
                        mappings, phrases);

                //                BookDao bookDao = daoSession.getBookDao();
                //                Book book = new Book();


                receiver.send(STATUS_FINISHED, bundle);
            }
        } catch (JSONException | IOException | ParseException e) {
            Log.d(TAG, "Error downloading the book");
            Log.d(TAG, e.toString());
        }
    }

    private JSONObject downloadData(String requestUrl) throws IOException, JSONException {
        // utility method to download book.
        //todo switch to volley if possible. Since its from a service we could do without volley
        // for now.
        Log.d(TAG, "Downloading from " + requestUrl);
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

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
            String line = "";

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


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        //        Initialize DAO' here
        DaoSession session = MyApplication.getNewSession();
        BookDao bookDao = session.getBookDao();
        StructureDao structureDao = session.getStructureDao();
        SentenceDao sentenceDao = session.getSentenceDao();

        Book book = new Book();
        book.setImageUrl(displayBook.getImageUrl());
        book.setTitle(displayBook.getTitle());
        book.setAuthor_id((long) 1);
        book.setAuthor_name(displayBook.getNativeVersion().getAuthor());
        Long bookId;
        try {
            bookId = session.insert(book);
            Log.d(TAG, "Book persisted with bookId= " + bookId);
        } catch (Exception e) {
            //TODO change this
            Log.e(TAG, "Error inserting book " + displayBook.getTitle());
        } finally {
            bookId = bookDao.queryBuilder().where(BookDao.Properties.Title.eq(displayBook
                    .getTitle())).list().get(0).getId();
            Log.d(TAG, "Book" + displayBook.getTitle() + " saved with ID: " + bookId);
        }


        long startTime = System.currentTimeMillis();
        // create source version

        DisplayBookObject.Version srcDispVersion = displayBook.getVersion(Language.getLongVersion
                (srcLanguage));
        String title = srcDispVersion.getTitle();
        Date SrclastModifiedDate = df.parse(srcLanguageBook.getString("date_modified"));
        Version srcVersion = new Version(null, srcLanguage, SrclastModifiedDate, srcDispVersion
                .getDescription(), bookId, srcDispVersion.getTitle(), srcDispVersion.getAuthor());
        long srcVersionId = session.insert(srcVersion);


        DisplayBookObject.Version swychDispVersion = displayBook.getVersion(Language
                .getLongVersion(swychLanguage));
        Date swychVerLastModifiedDate = df.parse(swychLanguageBook.getString("date_modified"));
        Version swychVersion = new Version(null, swychLanguage, swychVerLastModifiedDate,
                swychDispVersion.getDescription(), bookId, swychDispVersion.getTitle(),
                swychDispVersion.getAuthor());
        long swychVersionId = session.insert(swychVersion);


        Library item = new Library(null, displayBook.getImageUrl(), displayBook.isMode1Present(),
                displayBook.isMode2Present(), srcDispVersion.getTitle(), srcDispVersion.getAuthor
                (), srcLanguage, swychLanguage, srcVersionId, swychVersionId, new Date());
        try {
            session.insert(item);
        } catch (Exception e) {
            Log.e(TAG, "Book already present");
            return true;
        }

        long libraryId = item.getId();

        // add structure and sentence to db.
        JSONArray srcVersionStructure = srcLanguageBook.getJSONArray("structure");
        addStructureToDB(session, structureDao, sentenceDao, srcVersionId, srcVersionStructure);


        JSONArray swychVersionStructureList = swychLanguageBook.getJSONArray("structure");
        addStructureToDB(session, structureDao, sentenceDao, swychVersionId,
                swychVersionStructureList);


        // handle mappings.
        //        MappingDao mappingDao = session.getMappingDao();
        Date mappingLastModifiedDate = df.parse(mappings.getString("date_modified"));
        String parsedMappingString = Deserializer.parseMappings(mappings.getString("mapping"));

        Log.d(TAG, parsedMappingString);

        Mapping mapping = new Mapping(null, parsedMappingString, mappingLastModifiedDate,
                srcVersionId, swychVersionId, libraryId);
        session.insert(mapping);

        System.out.println(phrases);
        if (phrases != null) {
            Date phrasesLastModifiedDate = df.parse(phrases.getString("date_modified"));
            PhraseReplacement replacement = new PhraseReplacement(null, swychLanguage, phrases
                    .get("phrase_replacements").toString(), srcVersionId, swychVersionId,
                    libraryId);
            session.insert(replacement);
        }

        Log.i(TAG, "took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds for " +
                "book insert");


        return true;
    }

    private void addSentenceToDB(final DaoSession session, final SentenceDao sentenceDao, final
    JSONObject sentences, final long srcVersionId) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                Iterator<String> iterator = sentences.keys();
                String key;
                try {
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        Sentence sentence = new Sentence(null, Integer.parseInt(key), sentences
                                .getString(key), srcVersionId);
                        sentenceDao.insert(sentence);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing json in the addsentencestoDb method");
                }
            }
        });

    }

    private void addStructureToDB(final DaoSession session, final StructureDao structureDao,
                                  final SentenceDao sentenceDao, final long srcVersionId, final
                                  JSONArray structureList) {
        session.runInTx(new Runnable() {
            public void run() {
                // Everything in run will be executed in a single transaction.
                try {
                    for (int i = 0; i < structureList.length(); i++) {
                        JSONObject structureJson = structureList.getJSONObject(i);
                        int structureType = StructType.valueOf(structureJson.getString("type"))
                                .getCode();

                        int sentenceId = -1;

                        if (!structureJson.isNull("sentence")) {
                            JSONObject sentenceJson = structureJson.getJSONObject("sentence");
                            sentenceId = sentenceJson.getInt("sentence_id");
                            String sentString = sentenceJson.getString("content");
                            Sentence sentence = new Sentence(null, sentenceId, sentString,
                                    srcVersionId);
                            sentenceDao.insert(sentence);
                        }
                        Structure structure = new Structure(null, (long) i, (long) sentenceId,
                                structureType, srcVersionId);
                        structureDao.insert(structure);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Exception occured while adding structure to db.");
                }
            }
        });

    }
}
