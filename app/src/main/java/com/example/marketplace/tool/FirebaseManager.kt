package com.example.marketplace.tool

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.marketplace.data.VendorDataClass
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseManager{
    private var bool_holder = false
    private var database_refrence: DatabaseReference
    private var firebase_database: FirebaseDatabase
    private var storage_refrence: StorageReference
    private var firebase_storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var context: Context
    private lateinit var activity: Activity
    private var DB_NAME:String? = null
    var list_of_users:ArrayList<List<String>>? = null

    init {
        storage_refrence = firebase_storage.reference
        firebase_database  = FirebaseDatabase.getInstance()
        database_refrence = firebase_database.reference
    }

    constructor(con: Context){
        context = con
    }

    constructor(active: Activity){
        activity = active
    }

    constructor(con: Context, active: Activity){
        context = con
        activity = active
    }


    fun createDatabase(dbname: String){
        database_refrence = firebase_database.getReference(dbname)
        if (database_refrence.root.equals(dbname)) {
            DB_NAME = dbname
        }
    }

    fun getVendorDataBool(path: String, function:(VendorDataClass, Boolean)->Unit){
//        var vendor: VendorDataClass? = null
        val dbref = firebase_database.getReference(path)
        val fblistener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vendor = VendorDataClass(
                    imguri = snapshot.child("imguri").value.toString(),
                    username = snapshot.child("username").value.toString(),
                    password = snapshot.child("password").value.toString(),
                    phonenumber = snapshot.child("phonenumber").value.toString(),
                    storename = snapshot.child("storename").value.toString(),
                    address = snapshot.child("address").value.toString(),
                )
                function(vendor, snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        dbref.addValueEventListener(fblistener)
    }

    fun getVendorData(path: String, function:(VendorDataClass)->Unit){
        val dbref = firebase_database.getReference(path)
        val fblistener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vendor = VendorDataClass(
                    imguri = snapshot.child("imguri").value.toString(),
                    username = snapshot.child("username").value.toString(),
                    password = snapshot.child("password").value.toString(),
                    phonenumber = snapshot.child("phonenumber").value.toString(),
                    storename = snapshot.child("storename").value.toString(),
                    address = snapshot.child("address").value.toString(),
                    latitude = snapshot.child("latitude").value.toString(),
                    longitude = snapshot.child("longitude").value.toString(),
                )
                function(vendor)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        dbref.addValueEventListener(fblistener)
    }

    // DELETE FROM FIREBASE REALTIME DATABASE
    fun deleteFromDatabase(
        path: String,
        imgname: String?,
        function: (dbref:Task<Void>) -> Unit
    ){
        val patht = "$path/$imgname"
        Tool.debugMessage(patht, "CHECKING")
        val dbrefrence = database_refrence.child(patht)

        function(dbrefrence.removeValue())
    }

    // DELETE FROM FIREBASE STORAGE
    fun deleteFromStorageDatabase(
        pathuri: String,
        function:(Task<Void>)-> Unit
    ){
        val strefrence = firebase_storage.getReferenceFromUrl(pathuri)
        function(strefrence.delete())
    }

    // DELETE FROM FIREBASE STORAGE
    fun deleteFromStorageDatabase(pathuri: String): Boolean {
        val strefrence = firebase_storage.getReferenceFromUrl(pathuri)
        strefrence.delete()
            .addOnSuccessListener {
                bool_holder = true
            }
            .addOnFailureListener {
                bool_holder = false
            }
        val bholder = bool_holder
        return bholder
    }

    fun getProductData(path: String, function:(Iterable<DataSnapshot>)->Unit){
        val dbref = firebase_database.getReference(path)
        val fblistener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                function(snapshot.children)
            }

            override fun onCancelled(error: DatabaseError) {
                Constant.showShortToast(context, "There was an error")
            }
        }
        dbref.addValueEventListener(fblistener)
    }

    fun getFirebaseDatas(path: String, function:(Iterable<DataSnapshot>)->Unit){
        val dbref = firebase_database.getReference(path)
        val fblistener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                function(snapshot.children)
            }

            override fun onCancelled(error: DatabaseError) {
                Constant.showShortToast(context, "There was an error")
            }
        }
        dbref.addValueEventListener(fblistener)
    }

    fun getDB_NAME(): String? {
        return DB_NAME
    }

    fun getdataFromFireBaseDatabase(path: String, function:(data: Iterable<DataSnapshot>)->Unit): Boolean {
        var getdata = false
        val dbref = firebase_database.getReference(path)

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                function(snapshot.children)
                getdata = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                getdata = false
            }
        })
        return getdata
    }

    fun addToFirebaseDatabase(
        realtimedatabasepath: String,
        data: Any): Task<Void> {
        val complete = database_refrence.child(realtimedatabasepath).setValue(data)
        return complete
    }
}