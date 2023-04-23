package com.ti.neurow.ble

//Importing the aiding functions
import androidx.core.content.ContextCompat
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.ti.neurow.GlobalVariables
import com.ti.neurow.VariableChanges
import com.ti.neurow.db.DatabaseHelper
import com.ti.neurow.db.data33
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

import com.ti.neurow.ble.BleOperationsActivity

// For the test code to write to the file

private const val GATT_MIN_MTU_SIZE = 23
/** Maximum BLE MTU size as defined in gatt_api.h. */
private const val GATT_MAX_MTU_SIZE = 517

object ConnectionManager {
    //lateinit var context: Context // declare a property
    private var listeners: MutableSet<WeakReference<ConnectionEventListener>> = mutableSetOf()

    private val deviceGattMap = ConcurrentHashMap<BluetoothDevice, BluetoothGatt>()
    private val operationQueue = ConcurrentLinkedQueue<BleOperationType>()
    private var pendingOperation: BleOperationType? = null

    val dataFrame33Queue: Queue<DataFrame33> = LinkedList<DataFrame33>()
    val dataFrame35Queue: Queue<DataFrame35> = LinkedList<DataFrame35>()
    val dataFrame3DQueue: Queue<DataFrame3D> = LinkedList<DataFrame3D>()
    val queSize: Int = 10 // Our example target size can be made arbitrarily

    fun servicesOnDevice(device: BluetoothDevice): List<BluetoothGattService>? =
        deviceGattMap[device]?.services

    fun listenToBondStateChanges(context: Context) {
        context.applicationContext.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
    }

    fun registerListener(listener: ConnectionEventListener) {
        if (listeners.map { it.get() }.contains(listener)) { return }
        listeners.add(WeakReference(listener))
        listeners = listeners.filter { it.get() != null }.toMutableSet()
        Timber.d("Added listener $listener, ${listeners.size} listeners total")
    }

    fun unregisterListener(listener: ConnectionEventListener) {
        // Removing elements while in a loop results in a java.util.ConcurrentModificationException
        var toRemove: WeakReference<ConnectionEventListener>? = null
        listeners.forEach {
            if (it.get() == listener) {
                toRemove = it
            }
        }
        toRemove?.let {
            listeners.remove(it)
            Timber.d("Removed listener ${it.get()}, ${listeners.size} listeners total")
        }
    }

    fun connect(device: BluetoothDevice, context: Context) {
        if (device.isConnected()) {
            Timber.e("Already connected to ${device.address}!")
        } else {
            enqueueOperation(Connect(device, context.applicationContext))
        }
    }

    fun teardownConnection(device: BluetoothDevice) {
        if (device.isConnected()) {
            enqueueOperation(Disconnect(device))
        } else {
            Timber.e("Not connected to ${device.address}, cannot teardown connection!")
        }
    }

    fun readCharacteristic(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic) {
        if (device.isConnected() && characteristic.isReadable()) {
            enqueueOperation(CharacteristicRead(device, characteristic.uuid))
        } else if (!characteristic.isReadable()) {
            Timber.e("Attempting to read ${characteristic.uuid} that isn't readable!")
        } else if (!device.isConnected()) {
            Timber.e("Not connected to ${device.address}, cannot perform characteristic read")
        }
    }

    fun writeCharacteristic(
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic,
        payload: ByteArray
    ) {
        val writeType = when {
            characteristic.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic.isWritableWithoutResponse() -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            else -> {
                Timber.e("Characteristic ${characteristic.uuid} cannot be written to")
                return
            }
        }
        if (device.isConnected()) {
            enqueueOperation(CharacteristicWrite(device, characteristic.uuid, writeType, payload))
        } else {
            Timber.e("Not connected to ${device.address}, cannot perform characteristic write")
        }
    }

    fun readDescriptor(device: BluetoothDevice, descriptor: BluetoothGattDescriptor) {
        if (device.isConnected() && descriptor.isReadable()) {
            enqueueOperation(DescriptorRead(device, descriptor.uuid))
        } else if (!descriptor.isReadable()) {
            Timber.e("Attempting to read ${descriptor.uuid} that isn't readable!")
        } else if (!device.isConnected()) {
            Timber.e("Not connected to ${device.address}, cannot perform descriptor read")
        }
    }

    fun writeDescriptor(
        device: BluetoothDevice,
        descriptor: BluetoothGattDescriptor,
        payload: ByteArray
    ) {
        if (device.isConnected() && (descriptor.isWritable() || descriptor.isCccd())) {
            enqueueOperation(DescriptorWrite(device, descriptor.uuid, payload))
        } else if (!device.isConnected()) {
            Timber.e("Not connected to ${device.address}, cannot perform descriptor write")
        } else if (!descriptor.isWritable() && !descriptor.isCccd()) {
            Timber.e("Descriptor ${descriptor.uuid} cannot be written to")
        }
    }

    fun enableNotifications(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic) {
        if (device.isConnected() &&
            (characteristic.isIndicatable() || characteristic.isNotifiable())
        ) {
            enqueueOperation(EnableNotifications(device, characteristic.uuid))
        } else if (!device.isConnected()) {
            Timber.e("Not connected to ${device.address}, cannot enable notifications")
        } else if (!characteristic.isIndicatable() && !characteristic.isNotifiable()) {
            Timber.e("Characteristic ${characteristic.uuid} doesn't support notifications/indications")
        }
    }

    fun disableNotifications(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic) {
        if (device.isConnected() &&
            (characteristic.isIndicatable() || characteristic.isNotifiable())
        ) {
            enqueueOperation(DisableNotifications(device, characteristic.uuid))
        } else if (!device.isConnected()) {
            Timber.e("Not connected to ${device.address}, cannot disable notifications")
        } else if (!characteristic.isIndicatable() && !characteristic.isNotifiable()) {
            Timber.e("Characteristic ${characteristic.uuid} doesn't support notifications/indications")
        }
    }

    fun requestMtu(device: BluetoothDevice, mtu: Int) {
        if (device.isConnected()) {
            enqueueOperation(MtuRequest(device, mtu.coerceIn(GATT_MIN_MTU_SIZE, GATT_MAX_MTU_SIZE)))
        } else {
            Timber.e("Not connected to ${device.address}, cannot request MTU update!")
        }
    }

    // - Beginning of PRIVATE functions

    @Synchronized
    private fun enqueueOperation(operation: BleOperationType) {
        operationQueue.add(operation)
        if (pendingOperation == null) {
            doNextOperation()
        }
    }

    @Synchronized
    private fun signalEndOfOperation() {
        Timber.d("End of $pendingOperation")
        pendingOperation = null
        if (operationQueue.isNotEmpty()) {
            doNextOperation()
        }
    }

    /**
     * Perform a given [BleOperationType]. All permission checks are performed before an operation
     * can be enqueued by [enqueueOperation].
     */
    @Synchronized
    private fun doNextOperation() {
        if (pendingOperation != null) {
            Timber.e("doNextOperation() called when an operation is pending! Aborting.")
            return
        }

        val operation = operationQueue.poll() ?: run {
            Timber.v("Operation queue empty, returning")
            return
        }
        pendingOperation = operation

        // Handle Connect separately from other operations that require device to be connected
        if (operation is Connect) {
            with(operation) {
                Timber.w("Connecting to ${device.address}")
                device.connectGatt(context, false, callback)
            }
            return
        }

        // Check BluetoothGatt availability for other operations
        val gatt = deviceGattMap[operation.device]
            ?: this@ConnectionManager.run {
                Timber.e("Not connected to ${operation.device.address}! Aborting $operation operation.")
                signalEndOfOperation()
                return
            }

        // TODO: Make sure each operation ultimately leads to signalEndOfOperation()
        // TODO: Refactor this into an BleOperationType abstract or extension function
        when (operation) {
            is Disconnect -> with(operation) {
                Timber.w("Disconnecting from ${device.address}")
                gatt.close()
                deviceGattMap.remove(device)
                listeners.forEach { it.get()?.onDisconnect?.invoke(device) }
                signalEndOfOperation()
            }
            is CharacteristicWrite -> with(operation) {
                gatt.findCharacteristic(characteristicUuid)?.let { characteristic ->
                    characteristic.writeType = writeType
                    characteristic.value = payload
                    gatt.writeCharacteristic(characteristic)
                } ?: this@ConnectionManager.run {
                    Timber.e("Cannot find $characteristicUuid to write to")
                    signalEndOfOperation()
                }
            }
            is CharacteristicRead -> with(operation) {
                gatt.findCharacteristic(characteristicUuid)?.let { characteristic ->
                    gatt.readCharacteristic(characteristic)
                } ?: this@ConnectionManager.run {
                    Timber.e("Cannot find $characteristicUuid to read from")
                    signalEndOfOperation()
                }
            }
            is DescriptorWrite -> with(operation) {
                gatt.findDescriptor(descriptorUuid)?.let { descriptor ->
                    descriptor.value = payload
                    gatt.writeDescriptor(descriptor)
                } ?: this@ConnectionManager.run {
                    Timber.e("Cannot find $descriptorUuid to write to")
                    signalEndOfOperation()
                }
            }
            is DescriptorRead -> with(operation) {
                gatt.findDescriptor(descriptorUuid)?.let { descriptor ->
                    gatt.readDescriptor(descriptor)
                } ?: this@ConnectionManager.run {
                    Timber.e("Cannot find $descriptorUuid to read from")
                    signalEndOfOperation()
                }
            }
            is EnableNotifications -> with(operation) {
                gatt.findCharacteristic(characteristicUuid)?.let { characteristic ->
                    val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_UUID)
                    val payload = when {
                        characteristic.isIndicatable() ->
                            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        characteristic.isNotifiable() ->
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        else ->
                            error("${characteristic.uuid} doesn't support notifications/indications")
                    }

                    characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                        if (!gatt.setCharacteristicNotification(characteristic, true)) {
                            Timber.e("setCharacteristicNotification failed for ${characteristic.uuid}")
                            signalEndOfOperation()
                            return
                        }

                        cccDescriptor.value = payload
                        gatt.writeDescriptor(cccDescriptor)
                    } ?: this@ConnectionManager.run {
                        Timber.e("${characteristic.uuid} doesn't contain the CCC descriptor!")
                        signalEndOfOperation()
                    }
                } ?: this@ConnectionManager.run {
                    Timber.e("Cannot find $characteristicUuid! Failed to enable notifications.")
                    signalEndOfOperation()
                }
            }
            is DisableNotifications -> with(operation) {
                gatt.findCharacteristic(characteristicUuid)?.let { characteristic ->
                    val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_UUID)
                    characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                        if (!gatt.setCharacteristicNotification(characteristic, false)) {
                            Timber.e("setCharacteristicNotification failed for ${characteristic.uuid}")
                            signalEndOfOperation()
                            return
                        }

                        cccDescriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(cccDescriptor)
                    } ?: this@ConnectionManager.run {
                        Timber.e("${characteristic.uuid} doesn't contain the CCC descriptor!")
                        signalEndOfOperation()
                    }
                } ?: this@ConnectionManager.run {
                    Timber.e("Cannot find $characteristicUuid! Failed to disable notifications.")
                    signalEndOfOperation()
                }
            }
            is MtuRequest -> with(operation) {
                gatt.requestMtu(mtu)
            }
        }
    }

    private val callback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Timber.w("onConnectionStateChange: connected to $deviceAddress")
                    deviceGattMap[gatt.device] = gatt
                    Handler(Looper.getMainLooper()).post {
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Timber.e("onConnectionStateChange: disconnected from $deviceAddress")
                    teardownConnection(gatt.device)
                }
            } else {
                Timber.e("onConnectionStateChange: status $status encountered for $deviceAddress!")
                if (pendingOperation is Connect) {
                    signalEndOfOperation()
                }
                teardownConnection(gatt.device)
            }
        }

        /* This function is responsible for the sending the success signal that transfers
        us over to the new activity
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Timber.w("Discovered ${services.size} services for ${device.address}.")
                    printGattTable()
                    requestMtu(device, GATT_MAX_MTU_SIZE)
                    // The code below says for each of the listeners, take the onConn callback
                    // with the invoke(this), this being the bluetooth gatt object
                    // which then runs that code which starts the second activity
                    listeners.forEach { it.get()?.onConnectionSetupComplete?.invoke(this) }
                } else {
                    Timber.e("Service discovery failed due to status $status")
                    teardownConnection(gatt.device)
                }
            }

            if (pendingOperation is Connect) {
                signalEndOfOperation()
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Timber.w("ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")
            listeners.forEach { it.get()?.onMtuChanged?.invoke(gatt.device, mtu) }

            if (pendingOperation is MtuRequest) {
                signalEndOfOperation()
            }
        }

        override fun onCharacteristicRead(
            //TODO add listener things to this too?
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        //Okay this where we get the fields from the characteristic read
                        //The values are in little endian, needed to be stored LSV is at [0]
                        //Ex Time Lo [0], Time Mid [1], Time High [2]
                        Timber.i("Read characteristic $uuid | value: ${value.toHexString()}")
                        //This allows for the conversion to the objects
                        //TODO: Move this code to the listener where the activity will run, this shouldn't be here
                        //Just place the changes in the uuid parsing, no need to make this messier than it is

                        val myTime33 = VariableChanges()
                        val myTime35 = VariableChanges()
                        val myTime3D = VariableChanges()

                        // [TEST] Test variable change with BLE time33
                        myTime33.setTimeListener(object : VariableChanges.TimeListener {
                            override fun onTimeChanged(newTime: Double) {
                                //finish implementing
                                Timber.i("[TEST] onCharacteristicRead time33: %s", newTime.toString())
                            }
                        })

                        // [TEST] Test variable change with BLE time35
                        myTime35.setTimeListener(object : VariableChanges.TimeListener {
                            override fun onTimeChanged(newTime: Double) {
                                //finish implementing
                                Timber.i("[TEST] onCharacteristicRead time35: %s", newTime.toString())
                            }
                        })

                        //uuidParsing function code
                        if(uuid.toString() == "ce060032-43e5-11e4-916c-0800200c9a66") {
                            //Timber.i("You are reading 0x0032")
                            //Timber.i("Your heart rate is is ${df[6].toUByte()} BPM")
                        } else if (uuid.toString() == "ce060033-43e5-11e4-916c-0800200c9a66") {
                            val DF33: DataFrame33 = DataFrame33(value.toUByteArray())
                            val newTime = DF33.elapsedTime
                            myTime33.setTime(newTime)
                            DF33.printAllAtt()
                        } else if (uuid.toString() == "ce060035-43e5-11e4-916c-0800200c9a66") {
                            val DF35: DataFrame35 = DataFrame35(value.toUByteArray())
                            val newTime = DF35.elapsedTime
                            myTime35.setTime(newTime)
                            //DF35.printAllAtt()
                            if(false) {
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
                        } else if (uuid.toString() == "ce06003d-43e5-11e4-916c-0800200c9a66") {
                            //println("This is where it should read the first message compile the data")
                            val DF3D: DataFrame3D = DataFrame3D(value.toUByteArray())
                            DF3D.printAllAtt()
                        }
                        //uuidParsing(myTime1, uuid.toString(), value.toUByteArray(), false)
                        listeners.forEach { it.get()?.onCharacteristicRead?.invoke(gatt.device, this) }
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Timber.e("Read not permitted for $uuid!")
                    }
                    else -> {
                        Timber.e("Characteristic read failed for $uuid, error: $status")
                    }
                }
            }

            if (pendingOperation is CharacteristicRead) {
                signalEndOfOperation()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Timber.i("Wrote to characteristic $uuid | value: ${value.toHexString()}")
                        listeners.forEach { it.get()?.onCharacteristicWrite?.invoke(gatt.device, this) }
                    }
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Timber.e("Write not permitted for $uuid!")
                    }
                    else -> {
                        Timber.e("Characteristic write failed for $uuid, error: $status")
                    }
                }
            }

            if (pendingOperation is CharacteristicWrite) {
                signalEndOfOperation()
            }
        }


        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                // The value when subscribed to a characteristic
                Timber.i("Characteristic $uuid changed | value: ${value.toHexString()}")
                val context = this
                //val myTime33 = VariableChanges()
                //val myTime35 = VariableChanges()
                val myTime3D = VariableChanges()
                //val db = DatabaseHelper(context = this@ConnectionManager) //making reference to database
                //val appContext = ContextCompat.getApplicationContext(context)

                //val db = DatabaseHelper.getInstance(getApplicationContext())

/*                // [TEST] Test variable change with BLE time33
                myTime33.setTimeListener(object : VariableChanges.TimeListener {
                    override fun onTimeChanged(newTime: Double) {
                        //finish implementing
                        Timber.i("[TEST] onCharacteristicChanged time33: %s", newTime.toString())

                    }
                })

                // [TEST] Test variable change with BLE time35
                myTime35.setTimeListener(object : VariableChanges.TimeListener {
                    override fun onTimeChanged(newTime: Double) {
                        //finish implementing
                        Timber.i("[TEST] onCharacteristicChanged time35: %s", newTime.toString())
                    }
                })*/

                //uuidParsing function code
                if(uuid.toString() == "ce060032-43e5-11e4-916c-0800200c9a66") {
                    //Timber.i("You are reading 0x0032")
                    //Timber.i("Your heart rate is is ${df[6].toUByte()} BPM")
                } else if (uuid.toString() == "ce060033-43e5-11e4-916c-0800200c9a66") {
                    val DF33: DataFrame33 = DataFrame33(value.toUByteArray())
                    //Set's all df33 global variables to respective DF33 variables
                    GlobalVariables.elapsedTime33 = DF33.elapsedTime
                    GlobalVariables.intervalCount33 = DF33.intervalCount
                    GlobalVariables.averagePower33 = DF33.averagePower
                    GlobalVariables.totalCalories33 = DF33.totalCalories
                    GlobalVariables.splitIntAvgPace33 = DF33.splitIntAvgPace
                    GlobalVariables.splitIntAvgPwr33 = DF33.splitIntAvgPwr
                    GlobalVariables.splitIntAvgCal33 = DF33.splitIntAvgCal
                    GlobalVariables.lastSplitTime33 = DF33.lastSplitTime
                    GlobalVariables.lastSplitDist33 = DF33.lastSplitDist
                    //sets time listener with newTime
                    //lets Interval20Activity know global variables have changed
                    GlobalVariables.globalTimeInstance33.setTime(DF33.elapsedTime)

                    DF33.printAllAtt()
                } else if (uuid.toString() == "ce060035-43e5-11e4-916c-0800200c9a66") {
                    val DF35: DataFrame35 = DataFrame35(value.toUByteArray())
                    //Set's all df35 global variables to respective DF35 variables
                    GlobalVariables.elapsedTime35 = DF35.elapsedTime
                    GlobalVariables.distance35 = DF35.distance
                    GlobalVariables.driveLength35 = DF35.driveLength
                    GlobalVariables.driveTime35 = DF35.driveTime
                    GlobalVariables.strokeRecTime35 = DF35.strokeRecTime
                    GlobalVariables.strokeDistance35 = DF35.strokeDistance
                    GlobalVariables.peakDriveForce35 = DF35.peakDriveForce
                    GlobalVariables.averageDriveForce35 = DF35.averageDriveForce
                    GlobalVariables.workPerStroke35 = DF35.workPerStroke
                    GlobalVariables.strokeCount35 = DF35.strokeCount
                    //sets time listener with newTime
                    //lets Interval20Activity know global variables have changed
                    GlobalVariables.globalTimeInstance35.setTime(DF35.elapsedTime)

                    DF35.printAllAtt()
/*                    if(false) {
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
                    } else { DF35.printAllAtt() }*/
                } else if (uuid.toString() == "ce06003d-43e5-11e4-916c-0800200c9a66") {
                    //TODO add listener when 3D table is completed
                    //println("This is where it should read the first message compile the data")
                    val DF3D: DataFrame3D = DataFrame3D(value.toUByteArray())
                    GlobalVariables.pol3D = DF3D.messages
                    GlobalVariables.message3D = DF3D.forceVals.joinToString(separator = " ")
                    GlobalVariables.globalTimeInstance3D.setMessage(DF3D.forceVals.toString())
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

                //uuidParsing(myTime2, uuid.toString(), value.toUByteArray(), true)
                listeners.forEach { it.get()?.onCharacteristicChanged?.invoke(gatt.device, this) }
            }
        }


        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            with(descriptor) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Timber.i("Read descriptor $uuid | value: ${value.toHexString()}")
                        listeners.forEach { it.get()?.onDescriptorRead?.invoke(gatt.device, this) }
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Timber.e("Read not permitted for $uuid!")
                    }
                    else -> {
                        Timber.e("Descriptor read failed for $uuid, error: $status")
                    }
                }
            }

            if (pendingOperation is DescriptorRead) {
                signalEndOfOperation()
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            with(descriptor) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Timber.i("Wrote to descriptor $uuid | value: ${value.toHexString()}")

                        if (isCccd()) {
                            onCccdWrite(gatt, value, characteristic)
                        } else {
                            listeners.forEach { it.get()?.onDescriptorWrite?.invoke(gatt.device, this) }
                        }
                    }
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Timber.e("Write not permitted for $uuid!")
                    }
                    else -> {
                        Timber.e("Descriptor write failed for $uuid, error: $status")
                    }
                }
            }

            if (descriptor.isCccd() &&
                (pendingOperation is EnableNotifications || pendingOperation is DisableNotifications)
            ) {
                signalEndOfOperation()
            } else if (!descriptor.isCccd() && pendingOperation is DescriptorWrite) {
                signalEndOfOperation()
            }
        }

        private fun onCccdWrite(
            gatt: BluetoothGatt,
            value: ByteArray,
            characteristic: BluetoothGattCharacteristic
        ) {
            val charUuid = characteristic.uuid
            val notificationsEnabled =
                value.contentEquals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) ||
                        value.contentEquals(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
            val notificationsDisabled =
                value.contentEquals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)

            when {
                notificationsEnabled -> {
                    Timber.w("Notifications or indications ENABLED on $charUuid")
                    listeners.forEach {
                        it.get()?.onNotificationsEnabled?.invoke(
                            gatt.device,
                            characteristic
                        )
                    }
                }
                notificationsDisabled -> {
                    Timber.w("Notifications or indications DISABLED on $charUuid")
                    listeners.forEach {
                        it.get()?.onNotificationsDisabled?.invoke(
                            gatt.device,
                            characteristic
                        )
                    }
                }
                else -> {
                    Timber.e("Unexpected value ${value.toHexString()} on CCCD of $charUuid")
                }
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            with(intent) {
                if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                    val device = getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val previousBondState = getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)
                    val bondState = getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                    val bondTransition = "${previousBondState.toBondStateDescription()} to " +
                            bondState.toBondStateDescription()
                    Timber.w("${device?.address} bond state changed | $bondTransition")
                }
            }
        }

        private fun Int.toBondStateDescription() = when (this) {
            BluetoothDevice.BOND_BONDED -> "BONDED"
            BluetoothDevice.BOND_BONDING -> "BONDING"
            BluetoothDevice.BOND_NONE -> "NOT BONDED"
            else -> "ERROR: $this"
        }
    }

    private fun BluetoothDevice.isConnected() = deviceGattMap.containsKey(this)
}
