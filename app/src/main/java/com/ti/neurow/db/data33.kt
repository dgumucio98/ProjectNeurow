package com.ti.neurow.db

class data33 {
    var time_33 = 0.0
    var interval = 0
    var power = 0
    var total_cal = 0
    var split_pace = 0.0
    var split_power = 0
    var split_cal = 0.0
    var last_split_time = 0.0
    var last_split_dist = 0.0

    constructor(
        time_33: Double,
        interval: Int,
        power: Int,
        total_cal: Int,
        split_pace: Double,
        split_power: Int,
        split_cal: Double,
        last_split_time: Double,
        last_split_dist: Double
    ) {
        this.time_33 = time_33
        this.interval = interval
        this.power = power
        this.total_cal = total_cal
        this.split_pace = split_pace
        this.split_power = split_power
        this.split_cal = split_cal
        this.last_split_time = last_split_time
        this.last_split_dist = last_split_dist
    }


    override fun toString(): String {
        return "dataframe33{" +
                "time_33=" + time_33 +
                ", interval=" + interval +
                ", power=" + power +
                ", total_cal=" + total_cal +
                ", split_pace=" + split_pace +
                ", split_power=" + split_power +
                ", split_cal=" + split_cal +
                ", last_split_time=" + last_split_time +
                ", last_split_dist=" + last_split_dist +
                '}'
    }

    @JvmName("getTime_331")
    fun getTime_33(): Double {
        return time_33
    }

    @JvmName("getInterval1")
    fun getInterval(): Int {
        return interval
    }

    @JvmName("getPower1")
    fun getPower(): Int {
        return power
    }

    @JvmName("getTotal_cal1")
    fun getTotal_cal(): Int {
        return total_cal
    }

    @JvmName("getSplit_pace1")
    fun getSplit_pace(): Double {
        return split_pace
    }

    @JvmName("getSplit_power1")
    fun getSplit_power(): Int {
        return split_power
    }

    @JvmName("getSplit_cal1")
    fun getSplit_cal(): Double {
        return split_cal
    }

    @JvmName("getLast_split_time1")
    fun getLast_split_time(): Double {
        return last_split_time
    }

    @JvmName("getLast_split_dist1")
    fun getLast_split_dist(): Double {
        return last_split_dist
    }
}