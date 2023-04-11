package com.ti.neurow.ble

import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import org.jetbrains.anko.alert
import timber.log.Timber
import java.util.*

/* A utility class for the Concept2 PM5 BLE device
Works in conjunction with the ConnectionManager object to provide abstraction for
BLE operations to poll data, rest, and close connections
 */
class pm5Utility(device: BluetoothDevice) {

    //Characteristics for the PM5
    private val PM5 = device

    private val characteristics by lazy {
        /* get the services and perform the lambdad function
        and return the results in a single list*/
        ConnectionManager.servicesOnDevice(PM5)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }

    /* Values need to create the on demand variables */
    private val uuidString33 = "ce060033-43e5-11e4-916c-0800200c9a66"
    private val char33UUID: UUID = UUID.fromString(uuidString33)

    private val uuidString35 = "ce060035-43e5-11e4-916c-0800200c9a66"
    private val char35UUID: UUID = UUID.fromString(uuidString35)

    private val uuidString3D = "ce06003D-43e5-11e4-916c-0800200c9a66"
    private val char3DUUID: UUID = UUID.fromString(uuidString3D)

    //Getting the UUID to pull the characteristic
    private val uuidString22 = "ce060022-43e5-11e4-916c-0800200c9a66"
    private val char22UUID: UUID = UUID.fromString(uuidString22)

    // Characteristics to be used with Connection manager operations
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

    //Begin util functions
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

    fun read22() {
        ConnectionManager.readCharacteristic(PM5, char22)
        Timber.i("We are tyring to perform the read operation on the char22")
    }

    fun resetDevice() {
        //TODO: Implement the reset using the writes to and from the device
        //UUID: 21(write) & 22 (read)
    }


    // What is .apply? A fast way to apply properties to a constructor methods or the object that
    // returns an object and then apply the properties / function overrides
    // This event listener is completed it launches the second activity
    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            /* There are two callbacks being defined with this listener
            *onconnectionSetupComplete, which takes the BluetoothGatt object and launches the second
            * activity, BleOperationsActivity
             */
            /*
            onConnectionSetupComplete = { gatt ->
                //Intent(this@MainActivity, BleOperationsActivity::class.java).also {
                Intent(this@MainActivity, TestingActivity::class.java).also {
                    //Intent(this@MainActivity, MainUIActivity::class.java).also {
                    it.putExtra(BluetoothDevice.EXTRA_DEVICE, gatt.device)
                    startActivity(it)
                }
                ConnectionManager.unregisterListener(this)
            }
            onDisconnect = {
                runOnUiThread {
                    alert {
                        title = "Disconnected"
                        message = "Disconnected or unable to connect to device."
                        positiveButton("OK") {}
                    }.show()
                }
            }
             */
        }
    }
}