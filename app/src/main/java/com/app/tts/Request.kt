package com.app.tts

class Request {
    var carDropOffLat : String
    var carDropOffLon : String
    var carPickuplat : String
    var carPickuplon : String
    var carPickupTime : String
    var carDropOffTime : String

    var VanDropOffLat : String
    var VanDropOffLon : String
    var VanPickUpLat : String
    var VanPickUpLon : String
    var vanPickUpTime : String
    var vanDropOffTime : String

    var carDropOffLat2 : String
    var carDropOffLon2 : String
    var carPickupTime2 : String
    var carDropOffTime2 : String

    var carFare : String
    var carFare2 : String
    var vanFare : String

    var date : String
    var status : String
    var userId : String
    var companyId :String
    var companyId2 :String

    constructor(
        carDropOffLat: String,
        carDropOffLon: String,
        carPickuplat: String,
        carPickuplon: String,
        carPickupTime : String,
        carDropOffTime : String,

        VanPickUpLat : String,
        VanPickUpLon : String,
        VanDropOffLat : String,
        VanDropOffLon : String,
        vanPickUpTime : String,
        vanDropOffTime : String,

        carDropOffLat2 : String,
        carDropOffLon2 : String,
        carPickupTime2 : String,
        carDropOffTime2 : String,

        carFare : String,
        carFare2 : String,
        vanFare : String,
        date : String,
        status : String,
        userId : String,
        companyId :String,
        companyId2 :String
    ) {
        this.carDropOffLat = carDropOffLat
        this.carDropOffLon = carDropOffLon
        this.carPickuplat = carPickuplat
        this.carPickuplon = carPickuplon
        this.carPickupTime = carPickupTime
        this.carDropOffTime = carDropOffTime

        this.VanPickUpLat = VanPickUpLat
        this.VanPickUpLon = VanPickUpLon
        this.VanDropOffLat = VanDropOffLat
        this.VanDropOffLon = VanDropOffLon
        this.vanPickUpTime = vanPickUpTime
        this.vanDropOffTime = vanDropOffTime

        this.carDropOffLat2 = carDropOffLat2
        this.carDropOffLon2 = carDropOffLon2
        this.carPickupTime2 = carPickupTime2
        this.carDropOffTime2 = carDropOffTime2

        this.carFare = carFare
        this.carFare2 = carFare2
        this.vanFare = vanFare
        this.date = date

        this.status = status
        this.userId =userId
        this.companyId=companyId
        this.companyId2=companyId2
    }
}