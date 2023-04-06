package com.ti.neurow.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ti.neurow.R
import kotlinx.android.synthetic.main.activity_ble_operations.characteristics_recycler_view
import kotlinx.android.synthetic.main.activity_ble_operations.log_scroll_view
import kotlinx.android.synthetic.main.activity_ble_operations.log_text_view
import kotlinx.android.synthetic.main.activity_ble_operations.mtu_field
import kotlinx.android.synthetic.main.activity_ble_operations.request_mtu_button
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.selector
import org.jetbrains.anko.yesButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.ti.neurow.db.DatabaseHelper// access database
import com.ti.neurow.db.data33       // access data33 class
import com.ti.neurow.db.data35       // access data33 class
import timber.log.Timber




var callNum : Int = 0 //How many times does this onlcick listener run

class BleOperationsActivity : AppCompatActivity() {
    //val db = DatabaseHelper(this@BleOperationsActivity) //making reference to database
    lateinit var add_to_db: Button

    private lateinit var device: BluetoothDevice
    private val dateFormatter = SimpleDateFormat("MMM d, HH:mm:ss", Locale.US)

/*    fun writeDB33(elapsedTime: Double, intervalCount: Int, averagePower: Int, totalCalories: Int, splitIntAvgPace: Double, splitIntAvgPwr: Int, splitIntAvgCal: Int, lastSplitTime: Double, lastSplitDist: Int) {
        val db = DatabaseHelper(this@BleOperationsActivity) //making reference to database
        var realdata33 = data33(
            globalTime33,
            globalIntCnt,
            globalAvgPwr33,
            globalTotCal33,
            globalSpltIntAvgPace33,
            globalSpltIntAvgPwr33,
            globalSpltIntAvgCal33.toDouble(),
            globalLstSpltTime33,
            globalLstSpltDist33.toDouble()
        )
        var success = db.add_dataframe33(realdata33)
        if (success == true) {
            Toast.makeText(
                this@BleOperationsActivity,
                "Successfully entered table",
                Toast.LENGTH_SHORT
            ).show() //Testing
        } else {
            Toast.makeText(
                this@BleOperationsActivity,
                "Did not enter table",
                Toast.LENGTH_SHORT
            ).show() //Testing
        }
    }*/


    private val characteristics by lazy {
        ConnectionManager.servicesOnDevice(device)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }
    private val characteristicProperties by lazy {
        characteristics.map { characteristic ->
            characteristic to mutableListOf<CharacteristicProperty>().apply {
                if (characteristic.isNotifiable()) add(CharacteristicProperty.Notifiable)
                if (characteristic.isIndicatable()) add(CharacteristicProperty.Indicatable)
                if (characteristic.isReadable()) add(CharacteristicProperty.Readable)
                if (characteristic.isWritable()) add(CharacteristicProperty.Writable)
                if (characteristic.isWritableWithoutResponse()) {
                    add(CharacteristicProperty.WritableWithoutResponse)
                }
            }.toList()
        }.toMap()
    }
    private val characteristicAdapter: CharacteristicAdapter by lazy {
        CharacteristicAdapter(characteristics) { characteristic ->
            showCharacteristicOptions(characteristic)
        }
    }
    private var notifyingCharacteristics = mutableListOf<UUID>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        ConnectionManager.registerListener(connectionEventListener)
        super.onCreate(savedInstanceState)
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            ?: error("Missing BluetoothDevice from MainActivity!")

        setContentView(R.layout.activity_ble_operations)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = getString(R.string.ble_playground)
        }
        // When adding things we need our layout first to assign an ID
        add_to_db = findViewById(R.id.add_to_db)
        setupRecyclerView()
        request_mtu_button.setOnClickListener {
            if (mtu_field.text.isNotEmpty() && mtu_field.text.isNotBlank()) {
                mtu_field.text.toString().toIntOrNull()?.let { mtu ->
                    log("Requesting for MTU value of $mtu")
                    ConnectionManager.requestMtu(device, mtu)
                } ?: log("Invalid MTU value: ${mtu_field.text}")
            } else {
                log("Please specify a numeric value for desired ATT MTU (23-517)")
            }
            hideKeyboard()
        }


        /*
        * This function is not working leads to hangups in the code do not use this
        * the while loop stalls the entire application
        * TO DO: Rewrite this with listeners
         */
        add_to_db.setOnClickListener {
            Timber.d("The `ADD TO DB` button has been pressed!\n")
            Timber.d("The onclick for this button has been called $callNum times before.\n")
            Timber.d("Adding one more to the `callNum` variable")
            callNum += 1 // to increment our count.
            val db = DatabaseHelper(this@BleOperationsActivity) //making reference to database
            if(dataFrame33Queue.isEmpty()) {
                Timber.d("The data 33 Queue is empty\n")
            } else {
                Timber.d("The data 33 Queue has item(s) in it.\n")
                //It's not empty let's add to the databasea
                //Most recent DF from stack
                var df = dataFrame33Queue.poll()
                while(df != null) {
                    val inputDB = data33(
                        df.elapsedTime, df.intervalCount, df.averagePower,
                        df.totalCalories, df.splitIntAvgPace, df.splitIntAvgPwr,
                        df.splitIntAvgCal.toDouble(), df.lastSplitTime, df.lastSplitDist.toDouble()
                    )
                    var success = db.add_dataframe33(inputDB)

                    df = dataFrame33Queue.poll()
                }
            }
            //For 35
            if(dataFrame35Queue.isEmpty()) {
                Timber.d("The data 35 Queue is empty\n")
            } else {
                Timber.d("The data 35 Queue has item(s) in it.\n")
                //It's not empty let's add to the databasea
                //Most recent DF from stack
                var df = dataFrame35Queue.poll()
                while(df != null) {
                    val inputDB = data35(
                        df.elapsedTime, df.distance, df.driveLength,
                        df.driveTime, df.strokeRecTime, df.strokeDistance,
                        df.peakDriveForce, df.averageDriveForce,
                        df.workPerStroke, df.strokeCount
                    )
                    var success = db.add_dataframe35(inputDB)

                    df = dataFrame35Queue.poll()
                }
            }
        }
    }
//commented out so that you can leave BLE screen without disconnecting
/*    override fun onDestroy() {
        ConnectionManager.unregisterListener(connectionEventListener)
        ConnectionManager.teardownConnection(device)
>>>>>>> origin/alyson
        super.onDestroy()
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        characteristics_recycler_view.apply {
            adapter = characteristicAdapter
            layoutManager = LinearLayoutManager(
                this@BleOperationsActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = characteristics_recycler_view.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    // This section right here is what prints the values onto the recyclerview once
    // it is given the messages
    @SuppressLint("SetTextI18n")
    private fun log(message: String) {
        val formattedMessage = String.format("%s: %s", dateFormatter.format(Date()), message)
        runOnUiThread {
            val currentLogText = if (log_text_view.text.isEmpty()) {
                "Beginning of log."
            } else {
                log_text_view.text
            }
            log_text_view.text = "$currentLogText\n$formattedMessage"
            log_scroll_view.post { log_scroll_view.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun showCharacteristicOptions(characteristic: BluetoothGattCharacteristic) {
        characteristicProperties[characteristic]?.let { properties ->
            selector("Select an action to perform", properties.map { it.action }) { _, i ->
                when (properties[i]) {
                    CharacteristicProperty.Readable -> {
                        log("Reading from ${characteristic.uuid}")
                        ConnectionManager.readCharacteristic(device, characteristic)
                    }
                    CharacteristicProperty.Writable, CharacteristicProperty.WritableWithoutResponse -> {
                        showWritePayloadDialog(characteristic)
                    }
                    CharacteristicProperty.Notifiable, CharacteristicProperty.Indicatable -> {
                        if (notifyingCharacteristics.contains(characteristic.uuid)) {
                            log("Disabling notifications on ${characteristic.uuid}")
                            ConnectionManager.disableNotifications(device, characteristic)
                        } else {
                            log("Enabling notifications on ${characteristic.uuid}")
                            ConnectionManager.enableNotifications(device, characteristic)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showWritePayloadDialog(characteristic: BluetoothGattCharacteristic) {
        val hexField = layoutInflater.inflate(R.layout.edittext_hex_payload, null) as EditText
        alert {
            customView = hexField
            isCancelable = false
            yesButton {
                with(hexField.text.toString()) {
                    if (isNotBlank() && isNotEmpty()) {
                        val bytes = hexToBytes()
                        log("Writing to ${characteristic.uuid}: ${bytes.toHexString()}")
                        ConnectionManager.writeCharacteristic(device, characteristic, bytes)
                    } else {
                        log("Please enter a hex payload to write to ${characteristic.uuid}")
                    }
                }
            }
            noButton {}
        }.show()
        hexField.showKeyboard()
    }

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onDisconnect = {
                runOnUiThread {
                    alert {
                        title = "Disconnected"
                        message = "Disconnected from device."
                        positiveButton("OK") { onBackPressed() }
                    }.show()
                }
            }

            onCharacteristicRead = { _, characteristic ->
                log("Read from ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }

            onCharacteristicWrite = { _, characteristic ->
                log("Wrote to ${characteristic.uuid}")
            }

            onMtuChanged = { _, mtu ->
                log("MTU updated to $mtu")
            }

            onCharacteristicChanged = { _, characteristic ->
                log("Value changed on ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }

            onNotificationsEnabled = { _, characteristic ->
                log("Enabled notifications on ${characteristic.uuid}")
                notifyingCharacteristics.add(characteristic.uuid)
            }

            onNotificationsDisabled = { _, characteristic ->
                log("Disabled notifications on ${characteristic.uuid}")
                notifyingCharacteristics.remove(characteristic.uuid)
            }
        }
    }

    private enum class CharacteristicProperty {
        Readable,
        Writable,
        WritableWithoutResponse,
        Notifiable,
        Indicatable;

        val action
            get() = when (this) {
                Readable -> "Read"
                Writable -> "Write"
                WritableWithoutResponse -> "Write Without Response"
                Notifiable -> "Toggle Notifications"
                Indicatable -> "Toggle Indications"
            }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun EditText.showKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        requestFocus()
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun String.hexToBytes() =
        this.chunked(2).map { it.toUpperCase(Locale.US).toInt(16).toByte() }.toByteArray()
}
