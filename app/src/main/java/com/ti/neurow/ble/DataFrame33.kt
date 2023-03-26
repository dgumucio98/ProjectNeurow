package com.ti.neurow.ble

/*
*
* This class is for the data frame associated with the
* PM5 uuid: ce060032-43e5-11e4-916c-0800200c9a66
* In order to access the object's contents you simply call whatever the object's name is and
* access it's property
* For example, in the AidingFunctions.kt, a DataFrame33 object is called `DF33`
* to access the value of the Time, we use the code of:
* DF33.elapsedTime
* For total calories used thus far, we do:
* DF33.totalCalories
 */

class DataFrame33(data: UByteArray) {
    //In seconds Unit: 0.01 sec
    //Position: 0, 2 Bytes
    val elapsedTime: Double = byteToDec(0, 2, data).toDouble() * 0.01

    //Interval Count
    //Position: 3, 3 Bytes
    val intervalCount: Int = byteToDec(3, 3, data).toInt()

    //Average Power Unit: Assumption in watts
    //Position: 4, 5 Bytes
    val averagePower: Int = byteToDec(4, 5, data).toInt()

    //Total Calories Unit: cals
    //Position: 6, 7 Bytes
    val totalCalories: Int = byteToDec(6, 7, data).toInt()

    //Split/Int Avg Pace Unit: 0.01 sec
    //Position: 8, 9 Bytes
    val splitIntAvgPace: Double = byteToDec(8, 9, data).toDouble() * 0.01

    //Split/Int Avg Power Unit: Watts
    //Position: 10, 11 Bytes
    val splitIntAvgPwr: Int = byteToDec(10, 11, data).toInt()

    //Split/Interval Avg Calories Unit: cals/hr
    //Position: 12, 13 Bytes
    val splitIntAvgCal: Int = byteToDec(12, 13, data).toInt()

    //Last Split Time Unit: 0.1 sec
    //Position: 14, 16 Bytes
    val lastSplitTime: Double = byteToDec(14, 16, data).toDouble() * 0.1

    //Last Split Distance Unit: 1 meters
    //Position: 17, 19 Bytes
    val lastSplitDist: Int = byteToDec(17, 19, data).toInt()

    fun printAllAtt(): Unit {
        println("Elapsed time: %.2f seconds(s)".format(this.elapsedTime))
        println("Interval Count: ${this.intervalCount}")
        println("Average Power: ${this.averagePower} watts")
        println("Total Calories: ${this.totalCalories} cal(s)")
        println("Split/Interval Average Pace: %.2f seconds(s)".format(this.splitIntAvgPace))
        println("Split/Interval Average Power: ${this.splitIntAvgPwr} watts")
        println("Split/Interval Average Calories: ${this.splitIntAvgCal} cal(s)")
        println("Last Split Time: %.2f second(s)".format(this.lastSplitTime))
        println("Last Split Distance: ${this.lastSplitDist} meter(s)")
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
        for(i in 0..index-1) {
            //println("The index of this element given ${index} is ${i}")
            bigNum *= base
        }
        return bigNum
    }
}