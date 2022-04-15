package com.example.marketplace.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProductDatabase(
    context: Context, dbname: String,
): SQLiteOpenHelper(context, dbname, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE ProductDatabase(" +
                "uid PRIMARY KEY, imageuri TEXT, product TEXT, " +
                "price TEXT, detail TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ProductDatabase");
    }

    fun insertUserData(function:(ContentValues)-> ContentValues): Boolean {
        val contentvalues = ContentValues()
        val db = this.writableDatabase
        val contentv = function(contentvalues)
        val result = db.insert("ProductDatabase", null, contentv)
        return result != -1L
    }

    fun updateUserDataById(uid:Int, function:(ContentValues)-> ContentValues): Boolean {
        val contentvalues = ContentValues()
        val db = this.writableDatabase
        val contentv = function(contentvalues)
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        val cursor = db.rawQuery("Select * from ProductDatabase where uid = ? ",
            arrayOf(uid.toString()))
        //check if data is in database have some data
        if (cursor.count > 0) {
            val result = db.update("ProductDatabase", contentv, "uid=?", arrayOf(uid.toString())).toLong()
            return result != -1L
        }
        cursor.close()
        return false
    }

    fun updateUserDataByName(product:String, function:(ContentValues)-> ContentValues): Boolean {
        val contentvalues = ContentValues()
        val db = this.writableDatabase
        val contentv = function(contentvalues)
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        val cursor = db.rawQuery("Select * from ProductDatabase where product = ? ",
            arrayOf(product))
        //check if data is in database have some data
        if (cursor.count > 0) {
            val result = db.update("ProductDatabase", contentv, "product=?", arrayOf(product)).toLong()
            return result != -1L
        }
        cursor.close()
        return false
    }


    fun deleteUserDataByName(product: String): Boolean {
        val db = this.writableDatabase
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        val cursor = db.rawQuery("Select * from ProductDatabase where product = ? ", arrayOf(product))
        //check if tata is in database have some data
        if (cursor.count > 0) {
            val result = db.delete("ProductDatabase", "product=?", arrayOf(product)).toLong()
            return result != -1L
        }
        cursor.close()
        return false
    }

    fun deleteUserDataById(uid: Int): Boolean {
        val db = this.writableDatabase
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        val cursor = db.rawQuery("Select * from ProductDatabase where uid = ? ", arrayOf(uid.toString()))
        //check if tata is in database have some data
        if (cursor.count > 0) {
            val result = db.delete("ProductDatabase", "uid=?", arrayOf(uid.toString())).toLong()
            return result != -1L
        }
        cursor.close()
        return false
    }

    fun getUserDataByUser(product: String): Cursor? {
        val db = this.writableDatabase
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        return db.rawQuery("Select * from ProductDatabase where product=? ", arrayOf(product))
    }

    fun getUserDataById(uid: Int): Cursor? {
        val db = this.writableDatabase
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        return db.rawQuery("Select * from ProductDatabase where uid=? ", arrayOf(uid.toString()))
    }

    fun getUserData(): Cursor? {
        val db = this.writableDatabase
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        return db.rawQuery("Select * from ProductDatabase", null)
    }

    fun deleteAllUserData(): Boolean {
        val db = this.writableDatabase
        //cursor is selecting the row. what ever is selected is loaded in the cursor
        val cursor = db.rawQuery("Select * from ProductDatabase", null)
        //check if tata is in database have some data
        if (cursor.count > 0) {
            val result = db.delete("ProductDatabase", null, null).toLong()
            return result != -1L
        }
        cursor.close()
        return false
    }
}