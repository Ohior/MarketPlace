package com.example.marketplace.shop

import com.example.marketplace.tool.Constant
import com.example.marketplace.tool.VendorDataClass
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class FirebaseManager() {
    private lateinit var database_refrence: DatabaseReference
    private lateinit var firebase_database: FirebaseDatabase
    private var DB_NAME:String? = null

    init {
        firebase_database  = FirebaseDatabase.getInstance()
        database_refrence = firebase_database.getReference()
    }

    fun createDatabase(dbname: String): Unit {
        database_refrence = firebase_database.getReference(dbname)
        if (database_refrence.root.equals(dbname)) {
            DB_NAME = dbname
        }
    }

    fun getVendorData(paths: String, function:(VendorDataClass)->Unit){
//        var vendor: VendorDataClass? = null
        val dbref = firebase_database.getReference(paths)
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
                function(vendor)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        dbref.addValueEventListener(fblistener)
    }

    fun getDB_NAME(): String? {
        return DB_NAME
    }

    fun addToDatabase(childpath:String, vendordata:VendorDataClass): Task<Void>{
        return database_refrence.push().child(
            database_refrence.root.toString()+childpath).setValue(vendordata)
    }

    fun updateToDatabase(vendordata:VendorDataClass): Task<Void>{
        val hashmap = HashMap<String, Any>()
        hashmap[Constant.USERNAME] = vendordata.username
        hashmap[Constant.PASSWORD] = vendordata.password
        hashmap[Constant.STORENAME] = vendordata.storename
        hashmap[Constant.ADDRESS] = vendordata.address
        hashmap[Constant.IMAGEURI] = vendordata.imguri
        hashmap[Constant.PHONENUMBER] = vendordata.phonenumber
        return database_refrence.child(vendordata.username).updateChildren(hashmap)
    }
    fun removeFromDatabase(key:String): Task<Void>{
        return database_refrence.child(key).removeValue()
    }


}