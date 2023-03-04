package com.ti.neurow.ble
import java.util.*

val dataFrame33Queue: Queue<DataFrame33> = LinkedList<DataFrame33>()
val dataFrame35Queue: Queue<DataFrame35> = LinkedList<DataFrame35>()
val dataFrame3DQueue: Queue<DataFrame3D> = LinkedList<DataFrame3D>()
val queSize: Int = 10 // Our example target size can be made arbitrarily


fun uuidParsing(uuidString: String, df: UByteArray, onToggle: Boolean): Unit {
    if(uuidString == "ce060032-43e5-11e4-916c-0800200c9a66") {
        //Timber.i("You are reading 0x0032")
        //Timber.i("Your heart rate is is ${df[6].toUByte()} BPM")
    } else if (uuidString == "ce060033-43e5-11e4-916c-0800200c9a66") {
        val DF33: DataFrame33 = DataFrame33(df)
        DF33.printAllAtt()
        /*
        if(onToggle) { //onCharacter change
            if(dataFrame33Queue.size < queSize) {
                if(dataFrame33Queue.offer(DF33) != null) {
                    println("An item has been placed in the 33 Queue.")
                } else {
                    println("An item failed to be placed in the 33 Queue.")
                }
            } else {
                println("The queue full: ${dataFrame33Queue.size} items")
                println("Poping the most recent dataframe.")
                dataFrame33Queue.poll().printAllAtt()
                println("The queue will now clear.")
                dataFrame33Queue.clear()
            }
        }
        else //meaning it's onRead
            DF33.printAllAtt()
        */
    } else if (uuidString == "ce060035-43e5-11e4-916c-0800200c9a66") {
        val DF35: DataFrame35 = DataFrame35(df)
        //DF35.printAllAtt()
        if(onToggle) {
            if(dataFrame35Queue.size < queSize) {
                if(dataFrame35Queue.offer(DF35) != null) {
                    println("An item has been placed in the 35 Queue.")
                } else {
                    println("An item failed to be placed in the 35 Queue.")
                }
            } else {
                println("The queue full: ${dataFrame35Queue.size} items")
                println("Popping the most recent dataframe.")
                dataFrame35Queue.poll().printAllAtt()
                println("The queue will now clear.")
                dataFrame35Queue.clear()
            }
        } else { DF35.printAllAtt() }
    } else if (uuidString == "ce06003d-43e5-11e4-916c-0800200c9a66") {
        //println("This is where it should read the first message compile the data")
        val DF3D: DataFrame3D = DataFrame3D(df)
        DF3D.printAllAtt()
        /*
        if(onToggle) {
            if(dataFrame3DQueue.size < queSize) {
                if(dataFrame3DQueue.offer(DF3D) != null) {
                    println("An item has been placed in the 3D Queue.")
                } else {
                    println("An item failed to be placed in the 3D Queue.")
                }
            } else {
                println("The queue full: ${dataFrame3DQueue.size} items")
                println("Poping the most recent dataframe.")
                dataFrame3DQueue.poll().printAllAtt()
                println("The queue will now clear.")
            }
        } else { DF3D.printAllAtt() }
         */
    }
}
