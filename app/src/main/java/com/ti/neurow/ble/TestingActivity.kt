/*
 * Copyright 2023 Punch Through Design LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ti.neurow.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ti.neurow.R
import com.ti.neurow.VariableChanges
import com.ti.neurow.ble.pm5Utility
import com.ti.neurow.db.DatabaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.testing_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.runOnUiThread

/* A testing ground for the new functions to use in the application */
class TestingActivity: AppCompatActivity() {
    private lateinit var device: BluetoothDevice

    //Variables for setting UI elements
    //private lateinit var myTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var pwrTextElement: TextView
    private lateinit var calTextElement: TextView
    private lateinit var distTextElement: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        //ConnectionManager.registerListener(connectionEventListener)
        super.onCreate(savedInstanceState)
        ConnectionManager.registerListener(testEventListener)

        /* Importing the BLE device we connected to from the main activity
        *  We only need the device and then use the functions from
        * the ConnectionManager to abstract all of the connection stuff in the background
        * so that a person can you just call,
        * checkBLE_device(device) == good, then start33(device)
        * */
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            ?: error("Missing BluetoothDevice from MainActivity!")


        //Create PM5 util obj
        val PM5 = pm5Utility(device)
        setContentView(R.layout.testing_activity)

        /*Setting the buttons & Their listeners */
        val btn33start = findViewById(R.id.button33Start) as Button
        btn33start.setOnClickListener({ PM5.start33()})

        val btn35start = findViewById(R.id.button35Start) as Button
        btn35start.setOnClickListener({ PM5.start35()})

        val btn3Dstart = findViewById(R.id.button35Start) as Button
        btn3Dstart.setOnClickListener({ PM5.start3D()})

        val btn33End = findViewById(R.id.button33End) as Button
        btn33End.setOnClickListener({PM5.end33()})

        val btn35End = findViewById(R.id.button35End) as Button
        btn35End.setOnClickListener({PM5.end33()})

        val btn3DEnd = findViewById(R.id.button3DEnd) as Button
        btn3DEnd.setOnClickListener({PM5.end33()})

        val btn22 = findViewById(R.id.buttonRead22) as Button
        btn22.setOnClickListener({PM5.read22()})

        timeTextView = findViewById<TextView>(R.id.timeTextView)
        //myTextView.text = "Nothing here yet"
        pwrTextElement = findViewById<TextView>(R.id.pwrTextView)
        calTextElement = findViewById<TextView>(R.id.calTextView)
        distTextElement = findViewById<TextView>(R.id.distTextView)

    }

    /*
    fun updateText(newText: String) {
        runOnUiThread { myTextView. }
    }
     */


    /* TODO: Place all code that pertains to the activity here, that means adding to DB
    And parsing the data like so
    Why? because for readability and debuging, the connection manager object should not be
    running code for a specific activity, this event listener runs the additional coder after
    the connection manager one.
     */
    private val testEventListener by lazy {
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
            */
            val context = this@TestingActivity
            val db = DatabaseHelper(context) //making reference to database
            onCharacteristicChanged = {
                bleDevice, bleChar ->
                val uuid = bleChar.uuid
                val value = bleChar.value

                //uuidParsing function code
                if(uuid.toString() == "ce060032-43e5-11e4-916c-0800200c9a66") {
                    //Timber.i("You are reading 0x0032")
                    //Timber.i("Your heart rate is is ${df[6].toUByte()} BPM")
                } else if (uuid.toString() == "ce060033-43e5-11e4-916c-0800200c9a66") {
                    val DF33: DataFrame33 = DataFrame33(value.toUByteArray())
                    runOnUiThread { timeTextView.text = "Time is now ${DF33.elapsedTime} seconds" }
                    runOnUiThread { calTextView.text = "Total calories is now ${DF33.totalCalories} calories" }
                    runOnUiThread { pwrTextView.text = "Average power is now ${DF33.averagePower} watt" }
                    runOnUiThread { distTextView.text = "Last split distance is now ${DF33.lastSplitDist} seconds" }
                    DF33.printAllAtt()
                } else if (uuid.toString() == "ce060035-43e5-11e4-916c-0800200c9a66") {
                    val DF35: DataFrame35 = DataFrame35(value.toUByteArray())

                    DF35.printAllAtt()
                } else if (uuid.toString() == "ce06003d-43e5-11e4-916c-0800200c9a66") {
                    //TODO add listener when 3D table is completed
                    //println("This is where it should read the first message compile the data")
                    val DF3D: DataFrame3D = DataFrame3D(value.toUByteArray())
                    //val newTime = DF3D.elapsedTime
                    //myTime3D.setTime(newTime)
                    DF3D.printAllAtt()
                }
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
}

