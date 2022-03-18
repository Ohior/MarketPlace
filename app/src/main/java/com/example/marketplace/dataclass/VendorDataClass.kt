package com.example.marketplace.dataclass

import android.graphics.Bitmap

data class VendorDataClass(
    var img: Bitmap,
    var username:String,
    var password:String,
    var phonenumber:String,
    var storename:String,
    var address:String
                         )
