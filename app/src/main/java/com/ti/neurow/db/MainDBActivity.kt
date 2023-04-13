package com.ti.neurow.db

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ti.neurow.R

//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
//import kotlinx.coroutines

class MainDBActivity : AppCompatActivity() {
    // references of buttons and other controls on the layout

    lateinit var et_username: EditText
    lateinit var et_password: EditText
    lateinit var btn_login: Button

    lateinit var btn_create_account: Button
    lateinit var btn_delete_account: Button
    lateinit var btn_user_info: Button
    lateinit var btn_class_testing: Button
    lateinit var btn_update_password: Button
    lateinit var btn_update_FTP: Button
    lateinit var btn_delete_tables: Button
    lateinit var btn_history_error_tables: Button
    lateinit var btn_view_history_error_tables: Button
    lateinit var btn_prediction: Button


    override fun onCreate(savedInstanceState: Bundle?) { //starts the application
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db_main)

        //find data in app
        et_username = findViewById(R.id.username)
        et_password = findViewById(R.id.password)
        btn_login = findViewById(R.id.btnLogin)
        btn_create_account = findViewById(R.id.btnCreateAccount)
        btn_delete_account = findViewById(R.id.btnDelete)
        btn_user_info = findViewById(R.id.btnUserInfo)
        btn_class_testing = findViewById(R.id.btnClassTesting)
        btn_update_password = findViewById(R.id.btnUpdatePassword)
        btn_update_FTP = findViewById(R.id.btnUpdateFTP)
        btn_delete_tables = findViewById(R.id.btnDeleteTables)
        btn_history_error_tables = findViewById(R.id.btnHistoryErrorTables)
        btn_view_history_error_tables = findViewById(R.id.btnViewHistoryErrorTables)
        btn_prediction = findViewById(R.id.btnPredictions)

        //Test Data
        val FTP = 1
        val pz_1 = 1
        val pz_2 = 2
        val pz_3 = 3
        val pz_4 = 4
        val pz_5 = 5
        val pz_6 = 6
        val pz_7 = 7
        val time_33 = 1.0
        val interval = 2
        val power = 3
        val total_cal = 4
        val split_pace = 5.0
        val split_power = 6
        val split_cal = 7.0
        val last_split_time = 8.0
        val last_split_dist = 9.0
        val time_35 = 10.0
        val dist = 11.0
        val drive_len = 12.0
        val drive_time = 13.0
        val stroke_rec_time = 14.0
        val stroke_dist = 15.0
        val peak_drive_force = 16.0
        val avg_drive_force = 17.0
        val work_per_stroke = 18.0
        val stroke_count = 19
        val workout_num = "workout1"
        val error = 1
        val avg_power = 2.3

        //testing
        //testing

        //button listeners for the add and view all buttons

        //testing

        //button listeners for the add and view all buttons
        btn_login.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()
            val user_exists = db.user_exists(usernameTXT)
            if (user_exists == true) {
                Toast.makeText(this@MainDBActivity, "Username in System", Toast.LENGTH_SHORT).show()
                val password_match = passwordTXT == db.getPassword(usernameTXT)
                if (password_match) {
                    Toast.makeText(this@MainDBActivity, "Password Matched", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@MainDBActivity,
                        "Password Does Not Matched",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(this@MainDBActivity, "Username not in System", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_create_account.setOnClickListener {
            val user: User
            user = try {
                User(
                    et_username.text.toString(),
                    et_password.text.toString(),
                    FTP,
                    pz_1,
                    pz_2,
                    pz_3,
                    pz_4,
                    pz_5,
                    pz_6,
                    pz_7
                ) // Fill in class constructor
                //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_SHORT).show(); //Testing
            } catch (e: Exception) {
                Toast.makeText(this@MainDBActivity, "Error Creating Account", Toast.LENGTH_SHORT)
                    .show()
                User("error", "error", FTP, pz_1, pz_2, pz_3, pz_4, pz_5, pz_6, pz_7)
            }

            //add User information in table "User_Info"
            val databaseHelper = DatabaseHelper(this@MainDBActivity) //making reference to database
            val success = databaseHelper.add_account(user!!)
            if (success == true) {
                Toast.makeText(this@MainDBActivity, "Account Created", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainDBActivity, "Account is not Created", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_delete_account.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()
            val checkdeletedata = db.delete_account(usernameTXT, passwordTXT)
            if (checkdeletedata == true) Toast.makeText(
                this@MainDBActivity,
                "Account Deleted",
                Toast.LENGTH_SHORT
            ).show() else Toast.makeText(
                this@MainDBActivity,
                "Account not Deleted ",
                Toast.LENGTH_SHORT
            ).show()
        }

        btn_user_info.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()

            //Testing db's go getters
            val FTP = db.getFTP(usernameTXT)
            Toast.makeText(this@MainDBActivity, "FTP: $FTP", Toast.LENGTH_SHORT).show()

            //testing user_info table go getters
            val pz1 = db.getPZ_1(usernameTXT)
            val pz2 = db.getPZ_2(usernameTXT)
            val pz3 = db.getPZ_3(usernameTXT)
            val pz4 = db.getPZ_4(usernameTXT)
            val pz5 = db.getPZ_5(usernameTXT)
            val pz6 = db.getPZ_6(usernameTXT)
            val pz7 = db.getPZ_7(usernameTXT)
            Toast.makeText(
                this@MainDBActivity,
                "pz: $pz1 $pz2 $pz3 $pz4 $pz5 $pz6 $pz7",
                Toast.LENGTH_SHORT
            ).show()

            //testing dataframe33_info table go getters
            //int interval = db.getInterval();
            val time33 = db.time_33
            val interval = db.interval
            val power = db.power
            val total_cal = db.total_cal
            val split_pace = db.split_pace
            val split_power = db.split_power
            val split_cal = db.split_cal
            val last_split_time = db.last_split_time
            val last_split_dist = db.last_split_dist
            Toast.makeText(
                this@MainDBActivity,
                "dataframe33: $time33 $interval $power $total_cal $split_pace $split_power $split_cal $last_split_time $last_split_dist",
                Toast.LENGTH_SHORT
            ).show()

            //testing dataframe35_info table go getters
            val time35 = db.time_35
            val dist = db.dist
            val drive_len = db.drive_len
            val drive_time = db.drive_time
            val stroke_dist = db.stroke_dist
            val peak_drive_force = db.peak_drive_force
            val avg_drive_force = db.avg_drive_force
            val work_per_stroke = db.work_per_stroke
            val stroke_count = db.stroke_count
            Toast.makeText(
                this@MainDBActivity,
                "dataframe35: $time35 $dist $drive_len $drive_time $stroke_dist $peak_drive_force $avg_drive_force $work_per_stroke $stroke_count",
                Toast.LENGTH_SHORT
            ).show()
        }


        btn_class_testing.setOnClickListener {
            //Testing User class
            val user = User(
                et_username.text.toString(),
                et_password.text.toString(),
                FTP,
                pz_1,
                pz_2,
                pz_3,
                pz_4,
                pz_5,
                pz_6,
                pz_7
            ) // Fill in class constructor
            Toast.makeText(this@MainDBActivity, user.toString(), Toast.LENGTH_SHORT).show()


            //Testing Real Time data classes
            val databaseHelper = DatabaseHelper(this@MainDBActivity) //making reference to database

            //Testing dataframe33
            val realdata1 = data33(
                time_33,
                interval,
                power,
                total_cal,
                split_pace,
                split_power,
                split_cal,
                last_split_time,
                last_split_dist
            )
            Toast.makeText(this@MainDBActivity, realdata1.toString(), Toast.LENGTH_SHORT)
                .show() //Testing
            val success1 = databaseHelper.add_dataframe33(realdata1)
            if (success1 == true) {
                Toast.makeText(this@MainDBActivity, "Successfully entered table", Toast.LENGTH_SHORT)
                    .show() //Testing
            } else {
                Toast.makeText(this@MainDBActivity, "Did not enter table", Toast.LENGTH_SHORT)
                    .show() //Testing
            }


            //Testing dataframe35
            val realdata2 = data35(
                time_35,
                dist,
                drive_len,
                drive_time,
                stroke_rec_time,
                stroke_dist,
                peak_drive_force,
                avg_drive_force,
                work_per_stroke,
                stroke_count
            )
            Toast.makeText(this@MainDBActivity, realdata2.toString(), Toast.LENGTH_SHORT)
                .show() //Testing
            val success2 = databaseHelper.add_dataframe35(realdata2)
            if (success2 == true) {
                Toast.makeText(this@MainDBActivity, "Successfully entered table", Toast.LENGTH_SHORT)
                    .show() //Testing
            } else {
                Toast.makeText(this@MainDBActivity, "Did not enter table", Toast.LENGTH_SHORT)
                    .show() //Testing
            }
        }

        btn_update_password.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()

            //Testing update password
            val success = db.updateuserPassword(et_username.text.toString(), passwordTXT)
            if (success == true) {
                Toast.makeText(this@MainDBActivity, "Password Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainDBActivity, "Password is not Updated", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_update_FTP.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()

            //Testing Updating FTP
            val success2 =
                db.updateuserFTP(et_username.text.toString(), 8, 9, 10, 11, 12, 13, 14, 15)
            if (success2 == true) {
                Toast.makeText(this@MainDBActivity, "FTP Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainDBActivity, "FTP is not Updated", Toast.LENGTH_SHORT).show()
            }
        }


        btn_delete_tables.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val success1 = db.delete_dataframe33_table()
            Toast.makeText(this@MainDBActivity, "dataframe33 table Deleted", Toast.LENGTH_SHORT)
                .show()
            val success2 = db.delete_dataframe35_table()
            Toast.makeText(this@MainDBActivity, "dataframe35 table Deleted", Toast.LENGTH_SHORT)
                .show()
            val success3 = db.delete_table3D()!!
            Toast.makeText(this@MainDBActivity, "table3D table Deleted", Toast.LENGTH_SHORT).show()
        }

        btn_history_error_tables.setOnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()

            //Testing adding to history table
            val success1 = db.add_history(usernameTXT, workout_num, error, avg_power)
            if (success1 == true) {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout not added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            }

            //Adding multiple workouts at once

            //Testing adding to history table
            val success2 = db.add_history(usernameTXT, "workout_1", error, avg_power)
            if (success2 == true) {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout not added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            }

            //Testing adding to history table
            val success3 = db.add_history(usernameTXT, "workout_2", error, avg_power)
            if (success3 == true) {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout not added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            }

            //Testing adding to history table
            val success4 = db.add_history(usernameTXT, "workout_1", error, avg_power)
            if (success4 == true) {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainDBActivity,
                    "workout not added to history table",
                    Toast.LENGTH_SHORT
                ).show()
            }


            /*                //Testing adding to error table
                            int error = 5;
                            boolean success2 = db.add_error(usernameTXT,error);
                            if (success2 == true){
                                Toast.makeText(MainActivity.this, "error added to history table", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "error not added to history table", Toast.LENGTH_SHORT).show();
                            }*/
        }

        btn_view_history_error_tables.setOnClickListener(View.OnClickListener {
            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT = et_username.text.toString()
            val passwordTXT = et_password.text.toString()

            //display history table user specific
            val res = db.get_history(usernameTXT)
            if (res.count == 0) {
                Toast.makeText(this@MainDBActivity, "No History Exists", Toast.LENGTH_SHORT).show()
                res.close()
                return@OnClickListener
            }
            val buffer = StringBuffer()
            while (res.moveToNext()) {
                //buffer.append("timestamp :"+res.getString(2)+"\n");
                //buffer.append("Workout :"+res.getString(3)+"\n");
                buffer.append(
                    """${res.getString(2)}    Workout :${res.getString(3)}    Error :${
                        res.getString(
                            4
                        )
                    }
"""
                )
            }
            val builder = AlertDialog.Builder(this@MainDBActivity)
            builder.setCancelable(true)
            builder.setTitle("$usernameTXT History")
            builder.setMessage(buffer.toString())
            builder.show()
            res.close()


            /*                //display error table user specific
                            Cursor res2 = db.get_error(usernameTXT);
                            if(res2.getCount() == 0) {
                                Toast.makeText(MainActivity.this,"No Error Exists", Toast.LENGTH_SHORT).show();
                                res2.close();
                                return;
                            }
                            StringBuffer buffer2 = new StringBuffer();
                            while(res2.moveToNext()) {
                                buffer2.append(res2.getString(2)+"   "+"Errors: "+res2.getString(3)+"\n");
                            }
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                            builder2.setCancelable(true);
                            builder2.setTitle(usernameTXT+" Errors");
                            builder2.setMessage(buffer2.toString());
                            builder2.show();
                            res2.close();*/
        })


        btn_prediction.setOnClickListener(View.OnClickListener {

            val db = DatabaseHelper(this@MainDBActivity) //making reference to database
            val usernameTXT: String = et_username.getText().toString()
            val workout_type = "workout_1"
            val allPower: ArrayList<Double> = db.getAllPower(usernameTXT, workout_type) as ArrayList<Double>
            //Toast.makeText(MainActivity.this, allPower, Toast.LENGTH_SHORT).show();
            val buffer = StringBuffer()
            for (i in allPower.indices) {
                buffer.append(allPower[i].toString() + ' ')
            }
            val builder = AlertDialog.Builder(this@MainDBActivity)
            builder.setCancelable(true)
            builder.setTitle(usernameTXT + "Past Power")
            builder.setMessage(buffer.toString())
            builder.show()


            //Adding to 3D table

            val pol = 1
            val message: ArrayList<Int> = arrayListOf(1,1)

            val message_string = message.joinToString(separator = " ")
            //val message = "123456"
            val success = db.add_3Dmessage(pol, message_string)
            if (success == true) {
                Toast.makeText(this@MainDBActivity, "message added to 3D table", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@MainDBActivity,
                    "message not added to 3D table",
                    Toast.LENGTH_SHORT
                ).show()
            }

/*            val avg_y_string: ArrayList<Double>  = db.get3D_avg_y()
            val buffer2 = StringBuffer()
            for (i in avg_y_string.indices) {
                buffer.append(avg_y_string.get(i).toString()+ "----")
            val avg_y_string: List<Double> = db.get3D_avg_y()
            val buffer2 = StringBuffer()
            for (i in avg_y_string.indices) {
                buffer.append(avg_y_string.get(i).toString()+ ' ')
            }
            val builder2 = AlertDialog.Builder(this@MainDBActivity)
            builder.setCancelable(true)
            builder.setTitle(usernameTXT + "Avg Y Values")
            builder.setMessage(buffer.toString())
            builder.show()*/

        })


    }
}