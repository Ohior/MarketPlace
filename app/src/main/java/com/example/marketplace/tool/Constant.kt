package com.example.marketplace.tool

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.marketplace.data.*
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

object Constant {
    const val LOACTION_SERVICE_ID = 175
    const val ACTION_START_LOCATION_SERVICE = "startlocationservice"
    const val ACTION_STOP_LOCATION_SERVICE = "stoplocationservice"
    const val SHOP_DB_NAME = "shopdatabase"
    const val SHOP_Product_DB_NAME = "shopproductdatabase"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    val COL_COUNT = 2
    const val USER_TYPE = "usertype"
    const val IMAGE_URI = "imageuri"
    const val PRODUCT = "product"
    const val ADDRESS = "address"
    const val STORENAME = "storename"
    const val PHONENUMBER = "phonenumber"
    const val PASSWORD = "password"
    const val USERNAME = "username"
    const val REQUEST_IMAGE_CAMERA = 142
    const val REQUEST_IMAGE_GALLERY = 132
    const val EMAIL_HACK = "@sabogindaora.com"
    const val CUSTOMER = "customer"
    const val VENDOR = "vendor"
    const val CLICK_USER = "clickuser"
    const val CLICK_PASSWORD = "clickpassword"
    const val REQUEST_LOCATION_PERMISSION: Int = 1000
    const val USER_DB_NAME = "UserDatabase"
    const val PRODUCT_DB_NAME = "ProductDatabase"
    const val CURRENT_LATITUDE = "currentlatitude"
    const val CURRENT_LONGITUDE = "currentlongitude"
    private lateinit var sharedPreferences: SharedPreferences


    fun getImageBitMap(activity: Activity,imageuri: Uri): Bitmap? {
        val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, imageuri)
        return bitmap
    }

    fun getEmailHack(name: String, email: String = EMAIL_HACK): String {
        return name+email
    }

    fun getImageFromFirebase(fbimgdir:String, imgid: ImageView){
        Picasso.get().load(fbimgdir).into(imgid)
    }

    fun setString(context: Context, key: String, data: String?){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val spe = sharedPreferences.edit()
        spe.putString(key, data)
        spe.apply()
    }

    fun getString( context:Context,  key:String): String? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val data = sharedPreferences.getString(key, null)
        return data
    }

    fun setSetString(context:Context, key:String, data:ArrayList<String>){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val spe = sharedPreferences.edit()
        val hashset = HashSet<String>(data)
        spe.putStringSet(key, hashset)
        spe.apply()
    }

    fun getSetString( context:Context,  key:String):ArrayList<String>{
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val hashset = sharedPreferences.getStringSet(key, null) as HashSet<String>
        return ArrayList(hashset)
    }

    fun showShortToast(context:Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(context:Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun debugMessage(mess: String, tag:String="DEBUG") {
        Log.e(tag, mess )
    }

    fun getImageUri(cont: Context, bitmap:Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(cont.contentResolver, bitmap,"title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromUri(activity: Activity, context: Context, uri: Uri): String {
        var path = ""
        if (activity.contentResolver != null){
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            if(cursor != null){
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    fun getDrawableUri(resources: Resources, drawable: Int): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + resources.getResourcePackageName(drawable)
                + '/' + resources.getResourceTypeName(drawable)
                + '/' + resources.getResourceEntryName(drawable))
    }

    fun checkProductEmptyFields(pclass: ProductDataClass): Boolean {
        return (pclass.price.isNotEmpty()
                && pclass.product.isNotEmpty())
    }

    fun checkVendorEmptyFields(vclass: VendorDataClass): Boolean {
        return (vclass.username.isEmpty()
                && vclass.storename.isEmpty()
                && vclass.password.isEmpty()
                && vclass.phonenumber.isEmpty()
                && vclass.address.isEmpty())
    }

    fun showProgressBar(context: Context, message: String="Loading.... Please wait", function:(ProgressDialog)->Unit){
        val progressbar = ProgressDialog(context)
        progressbar.setTitle(message)
        progressbar.show()

        function(progressbar)
    }

}
