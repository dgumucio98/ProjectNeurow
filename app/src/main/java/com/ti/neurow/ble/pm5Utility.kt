package com.ti.neurow.ble

import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import org.jetbrains.anko.alert
import timber.log.Timber
import java.util.*
import kotlin.experimental.and

/* A utility class for the Concept2 PM5 BLE device
Works in conjunction with the ConnectionManager object to provide abstraction for
BLE operations to poll data, rest, and close connections
 */
class pm5Utility(device: BluetoothDevice) {
    /* Legend:
    Characteristics and what they do
    Functions
    Variables for building
     */

    //The PM5 itself referred to in the device
    private val PM5 = device
    //used for the status or state of the PM5
    //private lateinit var state: String
    private var state: String = "Unkown State"
    private val statusMask: Byte = 0b00001111.toByte() //bitmask for last four bits

    //This isn't going to work, we need to just do a quick creation of the device and then
    //call a function for the same functionality for the eventListner to be initialized
    //Setting up the utility
//    init{
//        //Start the polling & register the eventlistener
//        ConnectionManager.registerListener(this.eventListener)
//        this.start22()
//        this.sendToIdle()
//    }

    private val characteristics by lazy {
        /* get the services and perform the lambdad function
        and return the results in a single list*/
        ConnectionManager.servicesOnDevice(PM5)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }



    //Begin util functions
    // Start [33,35,3D]: Simple starts the stream, same for end
    fun start33() {
        Timber.i("PM5 Utilities has started polling for Dataframe 33")
        ConnectionManager.enableNotifications(PM5, char33)
    }
    fun end33() {
        ConnectionManager.disableNotifications(PM5, char33)
        Timber.i("PM5 Utilities has ended polling for Dataframe 33")
    }

    fun start35() {
        Timber.i("PM5 Utilities has started polling for Dataframe 35")
        ConnectionManager.enableNotifications(PM5, char35)
    }

    fun end35() {
        ConnectionManager.disableNotifications(PM5, char35)
        Timber.i("PM5 Utilities has ended polling for Dataframe 35")
    }

    fun start3D() {
        Timber.i("PM5 Utilities has started polling for Dataframe 3D")
        ConnectionManager.enableNotifications(PM5, char3D)
    }

    fun end3D() {
        ConnectionManager.disableNotifications(PM5, char3D)
        Timber.i("PM5 Utilities has ended polling for Dataframe 3D")
    }

    fun start22() {
        ConnectionManager.enableNotifications(PM5, char22)
        Timber.i("We are tyring to perform the read operation on the char22")
    }

    /* Sets the polling speed
    * give string to set speed
    * between:    * slowest    * medium    * fast   * fastest */
    fun setPollSpeed(speed: String) {
        var payload: Byte = when(speed) {
            "SLOWEST" -> 0x00 //Sets for 1 poll per second
            "MEDIUM" -> 0x01 //Sets for 1 poll per 500 ms
            "FAST" -> 0x02 //Sets for 1 poll per 250 ms
            "FASTEST" -> 0x03 //Sets for 1 poll per 100 ms
            else -> 0x01 //default speed
        }
        val byteArray = ByteArray(payload.toInt())
        ConnectionManager.writeCharacteristic(PM5, char34, byteArray)
        Timber.i("Set speed to ${payload} on characteristic 34")
    }

    fun getStatus(): String {
        return state
    }

    //Very important, must do this first before anything else
    fun selfSetUp(): Unit {
        ConnectionManager.registerListener(eventListener)
        this.start22()
        this.sendToIdle()
    }

//    fun resetDevice() {
//        //TODO: Implement the reset using the writes to and from the device
//        //UUID: 21(write) & 22 (read)
//    }

    //Start the workout and sets the machine in INUSE
    fun startWorkOut() {
        checkStatus()
        if(state == "FINISHED") {
            sendToIdle()
        } else
            Timber.i("CURRENT STATE: ${state}\t ATTEMPTED STATE CHANGE: IDLE")
        checkStatus()
        if(state == "IDLE") {
            sendToInUse()
        } else
            Timber.i("CURRENT STATE: ${state}\t ATTEMPTED STATE CHANGE: INUSE")
    }

    //End the workout and sets the machine back to finish
    fun endWorkOut() {
        checkStatus()
        if(state == "IN USE" || state == "PAUSE") {
            sendToFinished()
        } else
            Timber.i("CURRENT STATE: ${state}\t ATTEMPTED STATE CHANGE: FINISHED")
    }

    // Characteristics to be used with Connection manager operations
    private val char21 by lazy {
        // for a given item if it matches the UUID return the index of
        val indexOf21 = characteristics.indexOfFirst { characteristic ->
            characteristic.uuid == char21UUID
        }
        // we assign char33 the to char33 for use in the Connection Manager
        characteristics[indexOf21]
    }

    private val char22 by lazy {
        // for a given item if it matches the UUID return the index of
        val indexOf22 = characteristics.indexOfFirst { characteristic ->
            characteristic.uuid == char22UUID
        }
        // we assign char33 the to char33 for use in the Connection Manager
        characteristics[indexOf22]
    }

    private val char33 by lazy {
        // for a given item if it matches the UUID return the index of
        val indexOf33 = characteristics.indexOfFirst { characteristic ->
            characteristic.uuid == char33UUID
        }
        // we assign char33 the to char33 for use in the Connection Manager
        characteristics[indexOf33]
    }

    private val char35 by lazy {
        // for a given item if it matches the UUID return the index of
        val indexOf35 = characteristics.indexOfFirst { characteristic ->
            characteristic.uuid == char35UUID
        }
        // we assign char33 the to char33 for use in the Connection Manager
        characteristics[indexOf35]
    }

    private val char3D by lazy {
        // for a given item if it matches the UUID return the index of
        val indexOf3D = characteristics.indexOfFirst { characteristic ->
            characteristic.uuid == char3DUUID
        }
        // we assign char33 the to char33 for use in the Connection Manager
        characteristics[indexOf3D]
    }

    private val char34 by lazy {
        // for a given item if it matches the UUID return the index of
        val indexOf34 = characteristics.indexOfFirst { characteristic ->
            characteristic.uuid == char34UUID
        }
        // we assign char33 the to char33 for use in the Connection Manager
        characteristics[indexOf34]
    }

    //Event listener
    private val eventListener by lazy {
        ConnectionEventListener().apply {
            onCharacteristicChanged = {_, characteristic ->
                Timber.i("Value changed on ${characteristic.uuid}: ${characteristic.value.toHexString()}")
                if(characteristic.uuid == char22UUID) {
                    /* When the device is asked for a command regarding status check
                    sets the status value which is used to set up the device from
                    PM5 CSAFE state machine
                     */
                    if(characteristic.value.size == 4) {
                        val status = characteristic.value[1] and statusMask
                        state = when (status) {
                            0x00.toByte() -> "ERROR"
                            0x01.toByte() -> "READY"
                            0x02.toByte() -> "IDLE"
                            0x03.toByte() -> "HAVE ID"
                            //Note 0x04 is not used: 0x04.toByte() ->
                            0x05.toByte() -> "IN USE"
                            0x06.toByte() -> "PAUSE"
                            0x07.toByte() -> "FINISH"
                            0x08.toByte() -> "MANUAL"
                            0x09.toByte() -> "OFF LINE"
                            else -> "Unknown Status"
                        }
                        Timber.i("The current state is: ${state}")
                    }
                }
            }
        }
    }


    /* Values need to create the on demand variables */
    //Getting the UUID to pull the characteristic
    //Data stream 33
    private val uuidString33 = "ce060033-43e5-11e4-916c-0800200c9a66"
    private val char33UUID: UUID = UUID.fromString(uuidString33)

    //Data stream 35
    private val uuidString35 = "ce060035-43e5-11e4-916c-0800200c9a66"
    private val char35UUID: UUID = UUID.fromString(uuidString35)

    //Character to set data polling write for subscriptions
    private val uuidString34 = "ce060034-43e5-11e4-916c-0800200c9a66"
    private val char34UUID: UUID = UUID.fromString(uuidString34)

    //Data stream 3D
    private val uuidString3D = "ce06003D-43e5-11e4-916c-0800200c9a66"
    private val char3DUUID: UUID = UUID.fromString(uuidString3D)

    //Read CSAFE format
    private val uuidString21 = "ce060021-43e5-11e4-916c-0800200c9a66"
    private val char21UUID: UUID = UUID.fromString(uuidString21)

    //Command write in CSAFE format
    private val uuidString22 = "ce060022-43e5-11e4-916c-0800200c9a66"
    private val char22UUID: UUID = UUID.fromString(uuidString22)

    //CSAFE Commands & frame elements
    private val startFlag: Byte = 0xF1.toByte()
    private val endFlag: Byte = 0xF2.toByte()
    private val statusCMD: Byte = 0x80.toByte()
    private val resetByte: Byte = 0x81.toByte()
    private val goIdleByte: Byte = 0x82.toByte()
    private val goHaveIdBye: Byte = 0x83.toByte()
    private val goInUseByte: Byte = 0x85.toByte()
    private val goFinishedByte: Byte = 0x86.toByte()
    private val goReadyByte: Byte = 0x87.toByte()
    private val badIdByte: Byte = 0x88.toByte()


    private val checkSTatusCMD: ByteArray = byteArrayOf(startFlag, statusCMD, statusCMD, endFlag)
    private val setIdleCMD: ByteArray = byteArrayOf(startFlag, goIdleByte, goIdleByte, endFlag)
    private val goHaveIdCMD: ByteArray = byteArrayOf(startFlag, goHaveIdBye, goHaveIdBye, endFlag)
    private val goInUseCMD: ByteArray = byteArrayOf(startFlag, goInUseByte, goInUseByte, endFlag)
    private val goFinishedCMD: ByteArray = byteArrayOf(startFlag, goFinishedByte, goFinishedByte, endFlag)
    private val goReadyCMD: ByteArray = byteArrayOf(startFlag, goReadyByte, goReadyByte, endFlag)
    private val goResetCMD: ByteArray = byteArrayOf(startFlag, resetByte, resetByte, endFlag)

    // Private functions
    // Writes command to the CSAFE MACHINE
    private fun write21(command: ByteArray) {
        ConnectionManager.writeCharacteristic(PM5, char21, command)
    }

    //Sends the command to send the PM5 to IDLE state
    private fun checkStatus() {
        write21(checkSTatusCMD)
    }

    //Sends the command to send the PM5 to IDLE state
    private fun sendToIdle() {
        write21(setIdleCMD)
    }

    //Sends the command to send the PM5 to InUse state
    private fun sendToInUse() {
        write21(goInUseCMD)
    }

    //Sends the command to send the PM5 to InUse state
    private fun sendToFinished() {
        write21(goFinishedCMD)
    }

}