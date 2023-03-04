package com.ti.neurow.db

class data35 {
    var time_35 = 0.0
    var dist = 0.0
    var drive_len = 0.0
    var drive_time = 0.0
    var stroke_rec_time = 0.0
    var stroke_dist = 0.0
    var peak_drive_force = 0.0
    var avg_drive_force = 0.0
    var work_per_stroke = 0.0
    var stroke_count = 0

    constructor(
        time_35: Double,
        dist: Double,
        drive_len: Double,
        drive_time: Double,
        stroke_rec_time: Double,
        stroke_dist: Double,
        peak_drive_force: Double,
        avg_drive_force: Double,
        work_per_stroke: Double,
        stroke_count: Int
    ) {
        this.time_35 = time_35
        this.dist = dist
        this.drive_len = drive_len
        this.drive_time = drive_time
        this.stroke_rec_time = stroke_rec_time
        this.stroke_dist = stroke_dist
        this.peak_drive_force = peak_drive_force
        this.avg_drive_force = avg_drive_force
        this.work_per_stroke = work_per_stroke
        this.stroke_count = stroke_count
    }


    override fun toString(): String {
        return "dataframe35{" +
                "time_35=" + time_35 +
                ", dist=" + dist +
                ", drive_len=" + drive_len +
                ", drive_time=" + drive_time +
                ", stroke_rec_time=" + stroke_rec_time +
                ", stroke_dist=" + stroke_dist +
                ", peak_drive_force=" + peak_drive_force +
                ", avg_drive_force=" + avg_drive_force +
                ", work_per_stroke=" + work_per_stroke +
                ", stroke_count=" + stroke_count +
                '}'
    }

    @JvmName("getTime_351")
    fun getTime_35(): Double {
        return time_35
    }

    @JvmName("getDist1")
    fun getDist(): Double {
        return dist
    }

    @JvmName("getDrive_len1")
    fun getDrive_len(): Double {
        return drive_len
    }

    @JvmName("getDrive_time1")
    fun getDrive_time(): Double {
        return drive_time
    }

    @JvmName("getStroke_rec_time1")
    fun getStroke_rec_time(): Double {
        return stroke_rec_time
    }

    @JvmName("getStroke_dist1")
    fun getStroke_dist(): Double {
        return stroke_dist
    }

    @JvmName("getPeak_drive_force1")
    fun getPeak_drive_force(): Double {
        return peak_drive_force
    }

    @JvmName("getAvg_drive_force1")
    fun getAvg_drive_force(): Double {
        return avg_drive_force
    }

    @JvmName("getWork_per_stroke1")
    fun getWork_per_stroke(): Double {
        return work_per_stroke
    }

    @JvmName("getStroke_count1")
    fun getStroke_count(): Int {
        return stroke_count
    }
}