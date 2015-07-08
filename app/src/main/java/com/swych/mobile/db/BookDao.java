package com.swych.mobile.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.swych.mobile.db.Book;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table BOOK.
*/
public class BookDao extends AbstractDao<Book, Long> {

    public static final String TABLENAME = "BOOK";

    /**
     * Properties of entity Book.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Author_id = new Property(2, Long.class, "author_id", false, "AUTHOR_ID");
        public final static Property Author_name = new Property(3, String.class, "author_name", false, "AUTHOR_NAME");
        public final static Property Date = new Property(4, java.util.Date.class, "date", false, "DATE");
        public final static Property ImageUrl = new Property(5, String.class, "imageUrl", false, "IMAGE_URL");
    };

    private DaoSession daoSession;


    public BookDao(DaoConfig config) {
        super(config);
    }
    
    public BookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'BOOK' (" + //
                "'ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TITLE' TEXT," + // 1: title
                "'AUTHOR_ID' INTEGER," + // 2: author_id
                "'AUTHOR_NAME' TEXT," + // 3: author_name
                "'DATE' INTEGER," + // 4: date
                "'IMAGE_URL' TEXT);"); // 5: imageUrl
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_BOOK_AUTHOR_ID ON BOOK" +
                " (AUTHOR_ID);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_BOOK_AUTHOR_NAME ON BOOK" +
                " (AUTHOR_NAME);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_BOOK_DATE ON BOOK" +
                " (DATE);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BOOK'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Book entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        Long author_id = entity.getAuthor_id();
        if (author_id != null) {
            stmt.bindLong(3, author_id);
        }
 
        String author_name = entity.getAuthor_name();
        if (author_name != null) {
            stmt.bindString(4, author_name);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(5, date.getTime());
        }
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(6, imageUrl);
        }
    }

    @Override
    protected void attachEntity(Book entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Book readEntity(Cursor cursor, int offset) {
        Book entity = new Book( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // author_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // author_name
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // date
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // imageUrl
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Book entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAuthor_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setAuthor_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setImageUrl(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Book entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Book entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
