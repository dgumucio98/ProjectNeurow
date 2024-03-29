package com.ti.neurow.ble

//Timber is a log API which is responsible for the
// For uuid parsing
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ti.neurow.BuildConfig
import com.ti.neurow.GlobalVariables
import com.ti.neurow.GlobalVariables.globalBleDevice
import com.ti.neurow.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import timber.log.Timber

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2
// For the extra bluetooth scan and connect permissions now
private const val ENABLE_BLUETOOTHSCAN_REQUEST_CODE = 3
// lateinit var globalBleDevice: BluetoothDevice

class MainActivity : AppCompatActivity() {

    //setting global BLE device for use in later activity, garbage collection may be an issue
    //lateinit var globalBleDevice: BluetoothDevice
    /*******************************************
     * Properties
     *******************************************/

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        // This casts whatever is returned by getSystemService as type BluetoothManager
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        // The return value is the adapter for the bluetoothManager
        // The adapter is required for any bluetooth activity
        // will return null if there is no BT, since via Lazy check is already done
        bluetoothManager.adapter
        // The above is the BT Radio
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    //UUID we want to connect to
    private val rower = ParcelUuid.fromString("CE060000-43E5-11E4-916C-0800200C9A66")
    // This will be my filter to the scan results applied to
    // bleScanner.startScan(null, scanSettings, scanCallback)
    // null will be replaced with a 'scanFilter'
    private val filter = ScanFilter.Builder()
        .setServiceUuid(rower)
        .build()
    private var scanFilters = mutableListOf<ScanFilter>(filter)

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { scan_button.text = if (value) "Stop Scan" else "Start Scan" }
        }

    // This is an empty list of objects ScanResult objects
    private val scanResults = mutableListOf<ScanResult>()

    /* ScanResultAdapter is a recycler view Adapter, see class extension definition in file for more
    * This block works be using the ScanResultAdapter Constructor which takes a list of ScanResults
    * and an onclick listener, which in this case is the lambda function as the argument, so when
    *  a user clicks on said item from the adapter, the lambda is ran hence vvvvvvvv
    * This is where we connect to the device selected on screen from adapter in the recylerview
    *
    * The connection is done and the connectionEventListener
    * then runs the onConnectionSetupComplete callback
     */
    private val scanResultAdapter: ScanResultAdapter by lazy {
        ScanResultAdapter(scanResults) { result ->
            if (isScanning) {
                stopBleScan()
            }
            with(result.device) {
                // These two methods are written in Java and we can see their methods
                Timber.w("Connecting to $address")
                ConnectionManager.connect(this, this@MainActivity)
            }
        }
    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    /*******************************************
     * Activity function overrides
     *******************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        //setContentView(R.layout.activity_main)

        //Remove the bars at the top of the scan
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Toast.makeText(getApplicationContext(), "[TEST] DashboardActivity created!", Toast.LENGTH_SHORT).show();

        // Hide Action bar and Status bar, lock orientation to landscape
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE // lock to landscape

        supportActionBar?.hide()


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Works fine for now, but there may be some issues where the scope of these permissions are set.
        val permissions = arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, ENABLE_BLUETOOTHSCAN_REQUEST_CODE)
        } else {
            print("Debug: Permissions for Bluetooth Scan were denied.\n")
            // Permission already granted
            // Do something here
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, ENABLE_BLUETOOTHSCAN_REQUEST_CODE)
        } else {
            print("Debug: Permissions for Bluetooth Connect were denied.\n")
            // Permission already granted
            // Do something here
        }
        scan_button.setOnClickListener { if (isScanning) stopBleScan() else startBleScan() }
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        ConnectionManager.registerListener(connectionEventListener)
        //Reenable this for working prompt, should work even with new API
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    /*******************************************
     * Private functions
     *******************************************/

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    private fun startBleScan() {
        if(!isLocationPermissionGranted)
            requestLocationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            //For any discoverable devices uncomment
            //bleScanner.startScan(null, scanSettings, scanCallback)
            //with parsing for PM5
            bleScanner.startScan(scanFilters, scanSettings, scanCallback)
            isScanning = true
        }
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            alert {
                title = "Location permission required"
                message = "Starting from Android M (6.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }
    }

    private fun setupRecyclerView() {
        scan_results_recycler_view.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = scan_results_recycler_view.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        // If the adapter has data, hide the TextView inside the RecyclerView
        // An addition to make the new screen work
        if (scanResultAdapter.itemCount > 0) {
            findViewById<TextView>(R.id.txtLoading).visibility = View.GONE
        }
    }

    /*******************************************
     * Callback bodies
     *******************************************/

    //call back for overrides on the scanCallBack
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // searches scanResults for a device address result
            // scanResults is defined earlier to be an initial empty set of type ScanResult
            // `it` is the current ScanResult object and the device associated with the object
            // indexOfFirst is a function that is given an expression and returns the first
            // index of the match
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    Timber.i("Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
                //since they are inserted ay end and starting is at 0 we -1 to it
                scanResultAdapter.notifyItemInserted(scanResults.size - 1)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("onScanFailed: code $errorCode")
        }
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
            onConnectionSetupComplete = { gatt ->
                globalBleDevice = gatt.device
                //Intent(this@MainActivity, BleOperationsActivity::class.java).also {
                //Intent(this@MainActivity, TestingActivity::class.java).also {
                runOnUiThread {
                    alert {
                        title = "Connection Successful"
                        message = "The device has been connected, please press the back button"
                        positiveButton("OK") {}
                    }.show()
                    Timber.i("The BLE device has been connected")
                }
                GlobalVariables.BTconnected = true
                scan_button.setText("Connected!")
                scan_button.setBackgroundColor(Color.parseColor("#08cc60"))
                ConnectionManager.unregisterListener(this)
//                Intent(this@MainActivity, MainUIActivity::class.java).also {
//                    it.putExtra(BluetoothDevice.EXTRA_DEVICE, gatt.device)
//                    startActivity(it)
//                }
//                ConnectionManager.unregisterListener(this)
            }
            onDisconnect = {
                runOnUiThread {
                    alert {
                        title = "Disconnected"
                        message = "Disconnected or unable to connect to device."
                        positiveButton("OK") {}
                    }.show()
                    GlobalVariables.BTconnected = false

                }
            }
        }
    }

    /*******************************************
     * Extension functions
     *******************************************/

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
}