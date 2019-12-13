package com.box.libs.database.protocol;

import android.database.sqlite.SQLiteException;

import com.box.libs.database.DatabaseResult;

import java.util.List;

/**
 * Created by linjiang on 29/05/2018.
 * <p>
 * Database driver：SQLite、ContentProvider
 */

public interface IDriver<T extends IDescriptor> {
    List<T> getDatabaseNames();

    List<String> getTableNames(T databaseDesc) throws SQLiteException;

    void executeSQL(T databaseDesc, String query, DatabaseResult result) throws SQLiteException;
}
