package com.xuanyuetech.tocoach.data

/**
 * the student class
 */
class Student(){

    //region properties

    var id = -1
    var name = ""
    var notes = ""
    var profileImagePath = ""
    var backgroundImagePath = ""

    var maxNameLength = 10

    //endregion

    //region constructor

    constructor(id : Int, name : String, notes: String, profileImagePath : String, backgroundImagePath : String) : this() {
        this.id = id
        this.name = name
        this.notes = notes
        this.profileImagePath = profileImagePath
        this.backgroundImagePath = backgroundImagePath
    }

    //endregion
}