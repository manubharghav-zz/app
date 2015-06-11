package com.swych.mobile.db;

import android.app.Application;


import java.sql.Date;

import de.greenrobot.dao.test.AbstractDaoSessionTest;

/**
 * Created by manu on 6/2/15.
 */
public class BookTest extends AbstractDaoSessionTest< DaoMaster, DaoSession> {

    public BookTest(){
        super(DaoMaster.class);
    }

    public Book createTestBook(){
        Book book = new Book();
        long authorId = 1;
        long bookId = 1;
        book.setAuthor_id(authorId);
        book.setAuthor_name("Manu Reddy");
        book.setTitle("Swych");
        book.setId(bookId);
        return book;
    }


    public Version createVersion(String language, long version1_id, long book_id){
        Version bookVersion = new Version();
        bookVersion.setLanguage(language);
        bookVersion.setId(version1_id);
        bookVersion.setBook_id(book_id);
        bookVersion.setDate(new Date(System.currentTimeMillis()));
        bookVersion.setDescription("This is the "+language+ " of the book with book_id:" +book_id);
        bookVersion.setTitle(book_id+"_"+language);
        return bookVersion;
    }

    public void testBookCreation(){
        Book book = createTestBook();
        BookDao bookDao = daoSession.getBookDao();
        daoSession.insert(book);
        long bookIdToRetrieve = 1;

        Book retrievedBook = bookDao.load(bookIdToRetrieve);
        assertEquals(book.getAuthor_name(), retrievedBook.getAuthor_name());
    }

    public void testVersionsOfBook(){
        Book book = createTestBook();
        BookDao bookDao = daoSession.getBookDao();
        daoSession.insert(book);

        Version frenchVersionOfBook = createVersion("fr", 1, book.getId());
        Version spanishVersionOfBook = createVersion("sp", 2, book.getId());
        daoSession.insert(frenchVersionOfBook);
        daoSession.insert(spanishVersionOfBook);

        long bookIdToRetrieve = 1;
        Book retrievedBook = bookDao.load(bookIdToRetrieve);
        assertEquals(2, retrievedBook.getBookVersions().size());


    }
}
