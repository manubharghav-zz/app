package com.swych.mobile.db;

import android.app.Application;

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

    public void testBookCreation(){
        Book book = new Book();
        BookDao bookDao = daoSession.getBookDao();
        long authorId = 1;
        long bookId = 1;
        book.setAuthor_id(authorId);
        book.setAuthor_name("Manu Reddy");
        book.setTitle("Swych");
        book.setId(bookId);
        daoSession.insert(book);

        Book retrievedBook = bookDao.load(bookId);
        assertEquals(book.getAuthor_name(), retrievedBook.getAuthor_name());
    }

    public void testVersionsOfBook(){

    }
}
