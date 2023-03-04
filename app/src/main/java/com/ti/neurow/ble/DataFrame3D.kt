package com.ti.neurow.ble

@OptIn(ExperimentalStdlibApi::class)
class DataFrame3D(data: UByteArray) {
    //The # of Characteristics to expect
    val messages: Int = (data[0].rotateRight(4).and(0b00001111U)).toInt()

    //The number of data points
    val words: Int = (data[0].and(0b00001111U)).toInt()

    //Message number or sequence so, if it 1st of 6 or 3rd of 6, etc..
    val sequenceNumber: Int = data[1].toInt()

    //Force values with size word datapoints
    val forceVals = ArrayList<Int>()

    //Creation of an array to hold the various force values
    init {
        if(((words * 2) + 2) == data.size) {
            for (i in 1..words) {
                //since two bytes come together for a 'word'
                val force = byteToDec((2 * i), (2 * i) + 1, data)
                forceVals.add(force.toInt())
            }
        } else {
            val force = -1
            forceVals.add(force)
            //This indicates an invalid frame
        }
    }

    fun printAllAtt(): Unit {
        println("There are ${this.messages} messages in this force curve")
        println("Which contains ${this.words} datapoints in this dataframe")
        println("This is the ${this.sequenceNumber} of ${this.messages}")
        println("The force values in sequential order are:\n${forceVals}")
    }
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