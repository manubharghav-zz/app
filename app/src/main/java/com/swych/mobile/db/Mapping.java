package com.swych.mobile.db;

import com.swych.mobile.db.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table MAPPING.
 */
public class Mapping {

    private Long id;
    private String strMapping;
    /** Not-null value. */
    private java.util.Date last_modified_date;
    private Long version1_id;
    private Long version2_id;
    private Long library_item_mapping;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MappingDao myDao;

    private Version nativeVersion;
    private Long nativeVersion__resolvedKey;

    private Version foreignVersion;
    private Long foreignVersion__resolvedKey;

    private Library libraryItem;
    private Long libraryItem__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Mapping() {
    }

    public Mapping(Long id) {
        this.id = id;
    }

    public Mapping(Long id, String strMapping, java.util.Date last_modified_date, Long version1_id, Long version2_id, Long library_item_mapping) {
        this.id = id;
        this.strMapping = strMapping;
        this.last_modified_date = last_modified_date;
        this.version1_id = version1_id;
        this.version2_id = version2_id;
        this.library_item_mapping = library_item_mapping;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMappingDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStrMapping() {
        return strMapping;
    }

    public void setStrMapping(String strMapping) {
        this.strMapping = strMapping;
    }

    /** Not-null value. */
    public java.util.Date getLast_modified_date() {
        return last_modified_date;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLast_modified_date(java.util.Date last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public Long getVersion1_id() {
        return version1_id;
    }

    public void setVersion1_id(Long version1_id) {
        this.version1_id = version1_id;
    }

    public Long getVersion2_id() {
        return version2_id;
    }

    public void setVersion2_id(Long version2_id) {
        this.version2_id = version2_id;
    }

    public Long getLibrary_item_mapping() {
        return library_item_mapping;
    }

    public void setLibrary_item_mapping(Long library_item_mapping) {
        this.library_item_mapping = library_item_mapping;
    }

    /** To-one relationship, resolved on first access. */
    public Version getNativeVersion() {
        Long __key = this.version1_id;
        if (nativeVersion__resolvedKey == null || !nativeVersion__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VersionDao targetDao = daoSession.getVersionDao();
            Version nativeVersionNew = targetDao.load(__key);
            synchronized (this) {
                nativeVersion = nativeVersionNew;
            	nativeVersion__resolvedKey = __key;
            }
        }
        return nativeVersion;
    }

    public void setNativeVersion(Version nativeVersion) {
        synchronized (this) {
            this.nativeVersion = nativeVersion;
            version1_id = nativeVersion == null ? null : nativeVersion.getId();
            nativeVersion__resolvedKey = version1_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Version getForeignVersion() {
        Long __key = this.version2_id;
        if (foreignVersion__resolvedKey == null || !foreignVersion__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VersionDao targetDao = daoSession.getVersionDao();
            Version foreignVersionNew = targetDao.load(__key);
            synchronized (this) {
                foreignVersion = foreignVersionNew;
            	foreignVersion__resolvedKey = __key;
            }
        }
        return foreignVersion;
    }

    public void setForeignVersion(Version foreignVersion) {
        synchronized (this) {
            this.foreignVersion = foreignVersion;
            version2_id = foreignVersion == null ? null : foreignVersion.getId();
            foreignVersion__resolvedKey = version2_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Library getLibraryItem() {
        Long __key = this.library_item_mapping;
        if (libraryItem__resolvedKey == null || !libraryItem__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LibraryDao targetDao = daoSession.getLibraryDao();
            Library libraryItemNew = targetDao.load(__key);
            synchronized (this) {
                libraryItem = libraryItemNew;
            	libraryItem__resolvedKey = __key;
            }
        }
        return libraryItem;
    }

    public void setLibraryItem(Library libraryItem) {
        synchronized (this) {
            this.libraryItem = libraryItem;
            library_item_mapping = libraryItem == null ? null : libraryItem.getId();
            libraryItem__resolvedKey = library_item_mapping;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
