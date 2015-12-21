package com.studio.dirigible.storage.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;


public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "StorageDB", null, 1);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE Categories("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name TEXT);");

        db.execSQL("CREATE TABLE Items("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "CategoryID INTEGER,"
                + "Name TEXT,"
                + "Description TEXT,"
                + "Weight INTEGER,"
                + "Quantity INTEGER,"
                + "ImagePath TEXT,"
                + "Date DATETIME,"
                + "FOREIGN KEY(CategoryID) REFERENCES Categories(ID));");
    }
}