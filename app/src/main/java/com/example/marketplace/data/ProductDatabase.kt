package com.example.marketplace.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProductDatabase(
    private val context: Context, private val db_name: String,
): SQLiteOpenHelper(context, db_name, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        val query =
            "CREATE TABLE IF NOT EXISTS $db_name( uid integer PRIMARY KEY, imguri text, productname text, price text, detail text)"
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        val query = "DROP TABLE IF EXISTS $db_name"
        db.execSQL(query)
        onCreate(db)
    }

    fun insertIntoDatabase(pdc:ProductDataClass):Boolean{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("imguri", pdc.imguri);
        values.put("productname", pdc.product);
        values.put("price", pdc.price);
        values.put("detail", pdc.detail);
        val insertId = db.insert(db_name, null, values);
        db.close(); // Closing database connection
        return insertId != 1L
    }

    fun getFromDataBase(uid: Int): ProductDataClass? {
        val uuid = uid+1
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $db_name WHERE uid = $uuid", null)
//        val cursor = db.query(
//            db_name,
//            arrayOf("imguri", "productname", "price", "detail"),
//            "uid = ?",
//            arrayOf(uid.toString()),
//            null,
//            null,
//            null,
//        )
        cursor?.moveToFirst()
        val pdc = cursor?.let {
            ProductDataClass(
                imguri = cursor.getString(1),
                product = cursor.getString(2),
                price = cursor.getString(3),
                detail = cursor.getString(4),
            )
        }
        cursor?.close()
        return pdc
    }

    // Deleting database
    fun deleteDatabaseTable() {
        val db = this.writableDatabase
        db.rawQuery("DELETE FROM $db_name", null).close()
        db.delete(db_name,null,null)
        db.close()
    }

    //Delete specific
    fun deleteDatabaseById(uid: Int): ProductDataClass? {
        val data = getFromDataBase(uid)
        val db = this.writableDatabase
        //db.rawQuery("DELETE * FROM $db_name WHERE uid = ?", arrayOf(uid.toString())).close()
        db.delete(db_name, "uid = ?", arrayOf(uid.toString()))
        db.close()
        return data
    }

    // Getting database Count
    fun getDatabaseCount(): Int {
        val countQuery = "SELECT  * FROM $db_name"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val size = cursor.count
        cursor.close()
        // return count
        return size
    }
}