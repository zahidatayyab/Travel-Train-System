package com.app.tts

class Users // an object used for pushing user details to firebase
{
    var name : String
    var phone : String
    var rating : Double
    var phoneVerified: String
    var accountType : String

    constructor(name: String, phone: String, rating: Double, phoneVerified: String, accountType: String) {
        this.name = name
        this.phone = phone
        this.rating = rating
        this.phoneVerified = phoneVerified
        this.accountType = accountType
    }
}