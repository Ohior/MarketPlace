package com.example.marketplace.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor


class UserDatabase(
    private val context: Context, private val db_name: String,
): SQLiteOpenHelper(context, db_name, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        val query =
            "CREATE TABLE IF NOT EXISTS $db_name( " +
                    "uid integer PRIMARY KEY, imguri text, username text," +
                    " password text, phonenumber text, storename text, " +
                    "address text, longitude text, latitude text)"
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        val query = "DROP TABLE IF EXISTS $db_name"
        db.execSQL(query)
        onCreate(db)
    }

    fun insertIntoDatabase(vdc:VendorDataClass):Boolean{
        deleteDatabaseTable()
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("imguri", vdc.imguri);
        values.put("username", vdc.username);
        values.put("password", vdc.password);
        values.put("phonenumber", vdc.phonenumber);
        values.put("storename", vdc.storename);
        values.put("address", vdc.address);
        values.put("longitude", vdc.longitude);
        values.put("latitude", vdc.latitude);
        val insertId = db.insert(db_name, null, values);
        db.close(); // Closing database connection
        return insertId != 1L
    }

    fun getFromDataBase(): VendorDataClass? {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $db_name", null)
        cursor?.moveToFirst()
        val vdc = cursor?.let {
            VendorDataClass(
                imguri = cursor.getString(1),
                username = cursor.getString(2),
                password = cursor.getString(3),
                phonenumber = cursor.getString(4),
                storename = cursor.getString(5),
                address = cursor.getString(6),
                latitude = cursor.getString(7),
                longitude = cursor.getString(8),
            )
        }
        cursor?.close()
        db.close()

        return vdc
    }

    // Deleting database
    fun deleteDatabaseTable() {
        val db = this.writableDatabase
        db.rawQuery("DELETE FROM $db_name", null).close()
        db.delete(db_name,null,null)
        db.close()
    }
}