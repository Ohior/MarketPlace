package com.example.marketplace.tool

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.marketplace.data.VendorDataClass
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class FirebaseManager{
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
                    snapshot.child("imguri").value.toString(),
                    snapshot.child("username").value.toString(),
                    snapshot.child("password").value.toString(),
                    snapshot.child("phonenumber").value.toString(),
                    snapshot.child("storename").value.toString(),
                    snapshot.child("address").value.toString(),
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
//        var vendor: VendorDataClass? = null
        val dbref = firebase_database.getReference(path)
        val fblistener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vendor = VendorDataClass(
                    snapshot.child("imguri").value.toString(),
                    snapshot.child("username").value.toString(),
                    snapshot.child("password").value.toString(),
                    snapshot.child("phonenumber").value.toString(),
                    snapshot.child("storename").value.toString(),
                    snapshot.child("address").value.toString(),
                    snapshot.child("latitude").value.toString(),
                    snapshot.child("longitude").value.toString(),
                )
                function(vendor)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        dbref.addValueEventListener(fblistener)
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
        storeagepath: String,
        realtimedatabasepath: String,
        data: Any,
        uri: Uri? = null,
        function: (UploadTask) -> Unit,
    ): Task<DataSnapshot> {
        var path: Uri? = null
        val filepath = storage_refrence.child(storeagepath)
        val listen = filepath.putFile(uri!!)
        function(listen)
        filepath.downloadUrl.addOnSuccessListener {
            path = it
        }
        database_refrence.push().child(realtimedatabasepath).setValue(data)
        return database_refrence.child(realtimedatabasepath).get()
    }

    fun getDecryptUsers(path: String, delimiters:String="_"): ArrayList<List<String>>? {
        val dbref = firebase_database.getReference(path)
        val fblistener = object: ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                for (snapshot in snapshots.children){
                    Tool.debugMessage(snapshot.key.toString(), "SNAPSHOT")
                    val split = snapshot.key.toString().split(delimiters)
                    list_of_users?.add(split)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constant.showShortToast(context, "There was an error")
            }
        }
        dbref.addValueEventListener(fblistener)
        return list_of_users
    }
}