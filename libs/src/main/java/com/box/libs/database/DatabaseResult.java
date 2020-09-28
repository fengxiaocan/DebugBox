package com.box.libs.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.box.libs.util.Config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseResult {

    /**
     * Maximum length of a BLOB field before we stop trying to interpret it and just
     * return {@link #UNKNOWN_BLOB_LABEL}
     */
    private static final int MAX_BLOB_LENGTH = 512;

    /**
     * Label to use when a BLOB column cannot be converted to a string.
     */
    private static final String UNKNOWN_BLOB_LABEL = "{blob}";

    public List<String> columnNames;

    public List<List<String>> values;

    public Error sqlError;

    private static List<List<String>> wrapRows(Cursor cursor) {
        List<List<String>> result = new ArrayList<>();
        final int stringLength = Config.getDATABASE_STRING_LENGTH();
        final int numColumns = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            ArrayList<String> flatList = new ArrayList<>();
            for (int column = 0; column < numColumns; column++) {
                switch (cursor.getType(column)) {
                    case Cursor.FIELD_TYPE_NULL:
                        flatList.add(null);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        flatList.add(String.valueOf(cursor.getLong(column)));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        flatList.add(String.valueOf(cursor.getDouble(column)));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        flatList.add(blobToString(cursor.getBlob(column)));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                    default:
                        String string = cursor.getString(column);
                        if (string != null && string.length() > stringLength) {
                            string = string.substring(0, stringLength) + "...";
                        }
                        flatList.add(string);
                        break;
                }
            }
            result.add(flatList);
        }
        return result;
    }

    private static String blobToString(byte[] blob) {
        if (blob.length <= MAX_BLOB_LENGTH) {
            if (fastIsAscii(blob)) {
                return new String(blob, Charset.forName("US-ASCII"));
            }
        }
        return UNKNOWN_BLOB_LABEL;
    }

    private static boolean fastIsAscii(byte[] blob) {
        for (byte b : blob) {
            if ((b & ~0x7f) != 0) {
                return false;
            }
        }
        return true;
    }

    public void transformRawQuery() throws SQLiteException {
    }

    public void transformSelect(Cursor result) throws SQLiteException {
        columnNames = Arrays.asList(result.getColumnNames());
        values = wrapRows(result);
    }

    public void transformInsert(long insertedId) throws SQLiteException {
    }

    public void transformUpdateDelete(int count) throws SQLiteException {
    }

    public static class Error {
        public String message;
        public int code;
    }
}
