package com.ti.neurow.ble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.activity_main.scan_button
import kotlinx.android.synthetic.main.activity_main.scan_results_recycler_view
import org.jetbrains.anko.alert
//Timber is a log API which is responsible for the
import timber.log.Timber
// For uuid parsing
import android.os.ParcelUuid
import com.ti.neurow.BuildConfig
import com.ti.neurow.R

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2
// For the extra bluetooth scan and connect permissions now
private const val ENABLE_BLUETOOTHSCAN_REQUEST_CODE = 3


class MainActivity : AppCompatActivity() {

    /*******************************************
     * Properties
     *******************************************/

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        // This casts whatever is returned by getSystemService as type BluetoothManager
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
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
    /* To this is a later initialization that runs the lambda function
    in the body, which returns the ScanResultAdapter to the value of 'scanResultAdapter'
    Which has it's own lambda function in the constructor  of ScanResultAdapter
    the parameter result will check if it isscanning and if so stopBleScan
    Then, 'with(value)' do function block {}
    The last expression in the lambda is considered the return value */

    /* ScanResultAdapter is a recycler view
    * scanResultAdapter : RecyclerView.Adapter
    * This is a recyclerview
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
        setContentView(R.layout.activity_main)
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
//        if (!bluetoothAdapter.isEnabled) {
//            promptEnableBluetooth()
//        }
        //Placing our own custom bluetooth enable
        if(!bluetoothAdapter.isEnabled) {

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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bleScanner.startScan(scanFilters, scanSettings, scanCallback)
            isScanning = true
        }
    }
//    private fun startBleScan() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
//            requestLocationPermission()
//        } else {
//            scanResults.clear()
//            scanResultAdapter.notifyDataSetChanged()
//            //For any discoverable devices uncomment
//            //bleScanner.startScan(null, scanSettings, scanCallback)
//            //with parsing for PM5
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return
//            }
//            bleScanner.startScan(scanFilters, scanSettings, scanCallback)
//            isScanning = true
//        }
//    }

    private fun stopBleScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
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
    }

    /*******************************************
     * Callback bodies
     *******************************************/

    //call back for overrides on the scanCallBack
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // searches chan scanResults for a device address result
            // the predicate takes a collection element ad compares it
            // returns true and if so returns the list in w/e item matches
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    Timber.i("Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
                //since they are inserted ay end and starting is at 0
                scanResultAdapter.notifyItemInserted(scanResults.size - 1)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("onScanFailed: code $errorCode")
        }
    }

    // What is .apply?
    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onConnectionSetupComplete = { gatt ->
                Intent(this@MainActivity, BleOperationsActivity::class.java).also {
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

