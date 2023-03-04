package com.ti.neurow.ble

class DataFrame35(data: UByteArray) {
    //In seconds Unit: 0.01 sec
    //Position: 0, 2 Bytes
    val elapsedTime: Double = byteToDec(0, 2, data).toDouble() * 0.01

    //Distance Unit: 0.1 meters
    //Position: 3, 5 Bytes
    val distance: Double = byteToDec(3, 5, data).toDouble() * 0.1

    //Drive Length Unit: 0.01 meters, max 2.55m
    //Position: 6, 6 Bytes
    val driveLength: Double = byteToDec(6, 6, data).toDouble()

    //Drive Time Unit: 0.01 sec, max 2.55 sec
    //Position: 7, 7 Bytes
    val driveTime: Double = byteToDec(7, 7, data).toDouble()

    //Stroke Recovery Time Unit: 0.01sec, max 655.35 sec
    //Position 8, 9 Bytes
    val strokeRecTime: Double = byteToDec(8, 9, data).toDouble() * 0.01

    //Stroke Distance Unit: 0.01m, max 655.35m
    //Position: 10, 11 Bytes
    val strokeDistance: Double = byteToDec(10, 11, data).toDouble() * 0.01

    //Peak Drive Force Unit: 0.1 lbs Force, max 6553.5 lbs
    //Position: 12, 13 Bytes
    val peakDriveForce: Double = byteToDec(12, 13, data).toDouble() * 0.1

    //Average Drive Force Unit: 0.1 lbs, max 6553.5 lbs
    // Position: 14, 15 Bytes
    val averageDriveForce: Double = byteToDec(14, 15, data).toDouble() * 0.1

    //Work Per Stroke Unit: 0.1 Joules, max 6553.5 Joules
    //Position: 16, 17 Bytes
    val workPerStroke: Double = byteToDec(16, 17, data).toDouble() * 0.1

    //Stroke count Unit: 1 Strokes
    //Position 18, 19 Bytes
    val strokeCount: Int = byteToDec(18, 19, data).toInt()

    fun printAllAtt(): Unit {
        println("Elapsed Time: %.2f seconds(s)".format(this.elapsedTime))
        println("Distance: %.2f meter(s)".format(this.distance))
        println("Drive Length: %.2f meter(s)".format(this.driveLength))
        println("Drive Time: %.2f second(s)".format(this.driveTime))
        println("Stroke Recovery time: %.2f second(s)".format(this.strokeRecTime))
        println("Stroke Distance: %.2f meter(s)".format(this.strokeDistance))
        println("Peak Drive Force: %.2f lbs".format(this.peakDriveForce))
        println("Average Drive Force: %.2f lbs".format(this.averageDriveForce))
        println("Work Per Stroke: %.2f Joule(s)".format(this.workPerStroke))
        println("Stroke Count: %d".format(this.strokeCount))
    }

    private fun byteToDec(start: Int, end: Int, data: UByteArray): UInt {
        /*
        Converts the value from the indices of the UByteArray Little E style
        From: start to end indices, inclusive
         */
        var conversion: UInt = 0U
        var power: Int = 0
        for (i in start..end) {
            // debug statement println("Index: ${i}")
            conversion += data[i] * longPow(256U, power)
            power += 1
        }
        return conversion
    }

    private fun longPow(base: UInt, index: Int): UInt {
        var bigNum: UInt = 1U
        for (i in 0..index - 1) {
            //println("The index of this element given ${index} is ${i}")
            bigNum *= base
        }
        return bigNum
    }
}