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
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ti.neurow.R
import com.ti.neurow.ble.pm5Utility

/* A testing ground for the new functions to use in the application */
class TestingActivity: AppCompatActivity() {
    private lateinit var device: BluetoothDevice


    override fun onCreate(savedInstanceState: Bundle?) {
        //ConnectionManager.registerListener(connectionEventListener)
        super.onCreate(savedInstanceState)

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

        /*Setting the buttons */
        val btn33start = findViewById(R.id.button33Start) as Button
        btn33start.setOnClickListener({ PM5.start33()})

        val btn35start = findViewById(R.id.button35Start) as Button
        btn35start.setOnClickListener({ PM5.start35()})

        val btn3Dstart = findViewById(R.id.button3DStart) as Button
        btn3Dstart.setOnClickListener({ PM5.start3D()})

        val btn33End = findViewById(R.id.button33End) as Button
        btn33End.setOnClickListener({PM5.end33()})

        val btn35End = findViewById(R.id.button35End) as Button
        btn35End.setOnClickListener({PM5.end33()})

        val btn3DEnd = findViewById(R.id.button3DEnd) as Button
        btn3DEnd.setOnClickListener({PM5.end33()})
    }
    /* TODO: Implement an eventlistener for the ConnectionManager for callbacks */
}

