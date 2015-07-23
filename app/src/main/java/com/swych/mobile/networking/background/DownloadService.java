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
import com.swych.mobile.db.Book;
import com.swych.mobile.db.BookDao;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.Library;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.db.Mapping;
import com.swych.mobile.db.MappingDao;
import com.swych.mobile.db.Sentence;
import com.swych.mobile.db.SentenceDao;
import com.swych.mobile.db.Structure;
import com.swych.mobile.db.StructureDao;
import com.swych.mobile.db.Version;
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
import java.util.Date;
import java.util.Iterator;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

/**
 * Created by manu on 6/21/15.
 */
public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

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

    private JSONObject downloadData(String requestUrl) throws IOException, JSONException {
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuffer responceBuffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                responceBuffer.append(line);
            }
            reader.close();
            JSONObject response = new JSONObject(responceBuffer.toString());
            return response;


        } else {
            Log.d(TAG, "download failed with response " + statusCode);
        }
        return null;
    }

    private void downloadBook(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        DisplayBookObject displayBook = (DisplayBookObject) intent.getSerializableExtra("book");
        String bookName = displayBook.getTitle();
        String srcLanguage = intent.getStringExtra("nativeLanguage");
        String swychLanguage = intent.getStringExtra("foreignLanguage");


        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            JSONObject srcLangBook = downloadData(URLs.VERSION + bookName + "/" + srcLanguage);
            JSONObject tarLangBook = downloadData(URLs.VERSION + bookName + "/" + swychLanguage);
            JSONObject mappings = downloadData(URLs.MAPPING + bookName + "/" + srcLanguage + "/" + swychLanguage);
            //persist these objects to database;
            if (srcLangBook.length() > 0) {
                persistToDb(displayBook, srcLanguage, srcLangBook, swychLanguage, tarLangBook, mappings);

//                BookDao bookDao = daoSession.getBookDao();
//                Book book = new Book();


                receiver.send(STATUS_FINISHED, bundle);
            }
        } catch (JSONException | IOException e) {
            Log.d(TAG, "Error downloading the book");
            Log.d(TAG, e.toString());
        }
    }


    private boolean persistToDb(DisplayBookObject displayBook, String srcLanguage, JSONObject srcLanguageBook, String swychLanguage, JSONObject trgtLanguageBook, JSONObject mappings) throws JSONException {

//        Initialize DAO' here
        DaoSession session = MyApplication.getNewSession();
        BookDao bookDao = session.getBookDao();

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
            Log.e(TAG, "Error inserting the book.");
        } finally {
            bookId = bookDao.queryBuilder().where(BookDao.Properties.Title.eq(displayBook.getTitle())).list().get(0).getId();
            Log.d(TAG, "Book already saved. Id is " + bookId);
        }

        // create source version

        DisplayBookObject.Version version = displayBook.getVersion(Language.getLongVersion(srcLanguage));
        String title = version.getTitle();
        Version srcVersion = new Version(null, srcLanguage, new Date(), version.getDescription(), bookId, version.getTitle(), version.getAuthor());
        long srcVersionId = session.insert(srcVersion);

        StructureDao structureDao = session.getStructureDao();
        SentenceDao sentenceDao = session.getSentenceDao();

        long startTime = System.currentTimeMillis();
        JSONArray structureList = srcLanguageBook.getJSONArray("structure");
        addStructureToDB(session, structureDao, srcVersionId, structureList);

        JSONObject sentences = srcLanguageBook.getJSONObject("sentences");
        addSentenceToDB(session, sentenceDao, sentences, srcVersionId);

        version = displayBook.getVersion(Language.getLongVersion(swychLanguage));
        Version swychVersion = new Version(null, swychLanguage, new Date(), version.getDescription(), bookId, version.getTitle(), version.getAuthor());
        long swychVersionId = session.insert(swychVersion);

        structureList = trgtLanguageBook.getJSONArray("structure");
        addStructureToDB(session, structureDao, swychVersionId, structureList);

        sentences = trgtLanguageBook.getJSONObject("sentences");
        addSentenceToDB(session, sentenceDao, sentences, swychVersionId);

        // handle mappings.
//        MappingDao mappingDao = session.getMappingDao();
        assert srcLanguage == mappings.getString("first_lang");
        Mapping mapping = new Mapping(null, mappings.getString("mapping"), srcVersionId, swychVersionId, (long) mappings.getInt("revision_number"));
        session.insert(mapping);

//        LibraryDao libraryDao = session.getLibraryDao();
        Library item = new Library(null,srcVersionId,swychVersionId,srcLanguage,swychLanguage,title);
        session.insert(item);

        Log.i(TAG, "took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds for book insert");


        return true;
    }

    private void addSentenceToDB(final DaoSession session, final SentenceDao sentenceDao, final JSONObject sentences, final long srcVersionId) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                Iterator<String> iterator = sentences.keys();
                String key;
                try {
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        Sentence sentence = new Sentence(null, Integer.parseInt(key), sentences.getString(key), srcVersionId);
                        sentenceDao.insert(sentence);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing json in the addsentencestoDb method");
                }
            }
        });

    }

    private void addStructureToDB(final DaoSession session, final StructureDao structureDao, final long srcVersionId, final JSONArray structureList) {
        session.runInTx(new Runnable() {
            public void run() {
                // Everything in run will be executed in a single transaction.
                try {
                    for (int i = 0; i < structureList.length(); i++) {

                        Structure structure = new Structure(null, (long) i, structureList.getString(i), srcVersionId);
                        structureDao.insert(structure);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Exception json in structure read");
                }
            }
        });

    }
}
