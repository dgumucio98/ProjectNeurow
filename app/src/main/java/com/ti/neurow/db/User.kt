package com.ti.neurow.db

class User {
    var username: String? = null
    var password: String? = null
    var fTP = 0
    var pz_1 = 0
    var pz_2 = 0
    var pz_3 = 0
    var pz_4 = 0
    var pz_5 = 0
    var pz_6 = 0
    var pz_7 = 0

    constructor(
        username: String?,
        password: String?,
        FTP: Int,
        pz_1: Int,
        pz_2: Int,
        pz_3: Int,
        pz_4: Int,
        pz_5: Int,
        pz_6: Int,
        pz_7: Int
    ) {
        this.username = username
        this.password = password
        fTP = FTP
        this.pz_1 = pz_1
        this.pz_2 = pz_2
        this.pz_3 = pz_3
        this.pz_4 = pz_4
        this.pz_5 = pz_5
        this.pz_6 = pz_6
        this.pz_7 = pz_7
    }


    //toString is necessary for printing the contents of a class object
    override fun toString(): String {
        return "User{" +
                "Username='" + username + '\'' +
                ", Password='" + password + '\'' +
                ", FTP=" + fTP +
                ", pz_1=" + pz_1 +
                ", pz_2=" + pz_2 +
                ", pz_3=" + pz_3 +
                ", pz_4=" + pz_4 +
                ", pz_5=" + pz_5 +
                ", pz_6=" + pz_6 +
                ", pz_7=" + pz_7 +
                '}'
    }

    //go getters

    @JvmName("getUsername1")
    fun getUsername(): String? {
        return username
    }

    @JvmName("getPassword1")
    fun getPassword(): String? {
        return password
    }

    @JvmName("getFTP1")
    fun getFTP(): Int {
        return fTP
    }

    @JvmName("getPz_11")
    fun getPz_1(): Int {
        return pz_1
    }

    @JvmName("getPz_21")
    fun getPz_2(): Int {
        return pz_2
    }

    @JvmName("getPz_31")
    fun getPz_3(): Int {
        return pz_3
    }

    @JvmName("getPz_41")
    fun getPz_4(): Int {
        return pz_4
    }

    @JvmName("getPz_51")
    fun getPz_5(): Int {
        return pz_5
    }

    @JvmName("getPz_61")
    fun getPz_6(): Int {
        return pz_6
    }

    @JvmName("getPz_71")
    fun getPz_7(): Int {
        return pz_7
    }

}