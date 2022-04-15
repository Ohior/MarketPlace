package com.example.marketplace.data

class MessageDataClass{
    var message: String = ""
    var name: String = ""
    var password: String = ""
    constructor(){}
    constructor(name: String, message: String, password:String=""){
        this.message = message
        this.name = name
        this.password = password
    }

}class ChatListDataClass{
    var message: String = ""
    var name: String = ""
    var password: String = ""
    constructor(){}
    constructor(name: String, message: String, password:String=""){
        this.message = message
        this.name = name
        this.password = password
    }
}

