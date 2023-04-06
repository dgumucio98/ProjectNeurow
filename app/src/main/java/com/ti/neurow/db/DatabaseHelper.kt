package com.ti.neurow.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//import android.annotation.SuppressLint;
//import android.util.Log;
//import java.text.SimpleDateFormat;
//import java.util.Date;
class DatabaseHelper  //val 3D_table =
//        "Create TABLE $3D_INFO ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_SEQUENCENUMBER INT, $COLUMN_1_FORCEVALS TEXT, $COLUMN_2_FORCEVALS TEXT, $COLUMN_3_FORCEVALS TEXT, $COLUMN_4_FORCEVALS TEXT, $COLUMN_5_FORCEVALS TEXT, $COLUMN_6_FORCEVALS TEXT, $COLUMN_7_FORCEVALS TEXT, $COLUMN_8_FORCEVALS TEXT, $COLUMN_9_FORCEVALS TEXT, $COLUMN_10_FORCEVALS TEXT)"
//Constructor
    (context: Context?) :
    SQLiteOpenHelper(context, "Smart_Rower_Tables.db", null, 18) {
    //methods that must be implemented
    //this is called the first time a database is accessed. There should be code in here to create a new database
    override fun onCreate(db: SQLiteDatabase) {
        val user_table =
            "Create TABLE " + USER_INFO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_NAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_FTP + " INT, " + COLUMN_PZ_1 + " INT, " + COLUMN_PZ_2 + " INT, " + COLUMN_PZ_3 + " INT, " + COLUMN_PZ_4 + " INT, " + COLUMN_PZ_5 + " INT, " + COLUMN_PZ_6 + " INT, " + COLUMN_PZ_7 + " INT)"
        val dataframe33_table =
            "Create TABLE " + DATAFRAME33_INFO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TIME_33 + " DOUB, " + COLUMN_INTERVAL + " INT, " + COLUMN_POWER + " INT, " + COLUMN_TOTAL_CAL + " INT, " + COLUMN_SPLIT_PACE + " INT, " + COLUMN_SPLIT_POWER + " INT, " + COLUMN_SPLIT_CAL + " INT, " + COLUMN_LAST_SPLIT_TIME + " INT, " + COLUMN_LAST_SPLIT_DIST + " INT)"
        val dataframe35_table =
            "Create TABLE " + DATAFRAME35_INFO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TIME_35 + " DOUB, " + COLUMN_DIST + " DOUB, " + COLUMN_DRIVE_LEN + " DOUB, " + COLUMN_DRIVE_TIME + " DOUB, " + COLUMN_STROKE_REC_TIME + " DOUB, " + COLUMN_STROKE_DIST + " DOUB, " + COLUMN_PEAK_DRIVE_FORCE + " DOUB, " + COLUMN_AVG_DRIVE_FORCE + " DOUB, " + COLUMN_WORK_PER_STROKE + " DOUB, " + COLUMN_STROKE_COUNT + " INT)"
        val history_table =
            "Create TABLE " + HISTORY_INFO + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER + " TEXT, " + COLUMN_TIMESTAMP + " TEXT default (datetime('now','localtime')), " + COLUMN_WORKOUT + " TEXT, " + COLUMN_ERROR + " INT, " + COLUMN_AVGPOWER + " DOUB)"
        val emptyList = ArrayList<Int>()
        val table3D =
            "Create TABLE " + INFO3D + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MESSAGE + " INT, " + COLUMN_FORCEVALS1 + " TEXT, " + COLUMN_FORCEVALS2 + " TEXT, " + COLUMN_FORCEVALS3 + " TEXT, " + COLUMN_FORCEVALS4 + " TEXT, " + COLUMN_FORCEVALS5 + " TEXT, " + COLUMN_FORCEVALS6 + " TEXT, " + COLUMN_FORCEVALS7 + " TEXT, " + COLUMN_FORCEVALS8 + " TEXT, " + COLUMN_FORCEVALS9 + " TEXT, " + COLUMN_FORCEVALS10 + " TEXT)"
        db.execSQL(user_table)
        db.execSQL(dataframe33_table)
        db.execSQL(dataframe35_table)
        db.execSQL(history_table)
        db.execSQL(table3D)
        //db.execSQL(error_table);
    }

    //this is called if the database version number changes. It prevents users apps from breaking when you change the database design.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("Drop Table IF EXISTS USER_INFO")
        db.execSQL("Drop Table IF EXISTS DATAFRAME33_INFO")
        db.execSQL("Drop Table IF EXISTS DATAFRAME35_INFO")
        db.execSQL("Drop Table IF EXISTS HISTORY_INFO")
        db.execSQL("Drop Table IF EXISTS INFO3D")
        //db.execSQL("Drop Table IF EXISTS ERROR_INFO");
        onCreate(db)
    }

    //Methods!!!
    //add to tables
    fun add_3Dmessage(message: Int, forceVals: String?): Boolean {
        val db = this.writableDatabase
        //If username exists return false
        val cursor = db.rawQuery(
            "Select * from info3D where COLUMN_MESSAGE = ?",
            arrayOf(message.toString())
        ) //Find sequenceNumber in info3D
        return if (cursor.count > 0) {
            cursor.moveToFirst()
            val cv = ContentValues()
            var columnIndex = -1
            // loop through the columns in the row and find the next null column
            for (i in 2..cursor.columnCount) {
                if (i >= 12) {
                    break
                }
                if (cursor.getString(i) == null) {
                    columnIndex = i
                    break
                }
            }
            if (columnIndex != -1) {
                // update the null column with new force values
                cv.put(cursor.getColumnName(columnIndex), forceVals)
                val result =
                    db.update(INFO3D, cv, "COLUMN_MESSAGE = ?", arrayOf(message.toString()))
                        .toLong()
                cursor.close()
                result != -1L
            } else {
                // no null column found, cannot update the row
                cursor.close()
                false
            }
        } else {
            val cv = ContentValues()
            cv.put(COLUMN_MESSAGE, message)
            cv.put(COLUMN_FORCEVALS1, forceVals)

            //ID is a auto increment in the database
            val insert = db.insert(INFO3D, null, cv)
            cursor.close()
            insert != -1L
        }
    }

    fun add_account(user: User): Boolean {
        val db = this.writableDatabase
        //If username exists return false
        val cursor = db.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(user.getUsername())
        ) //Find user in user_table
        return if (cursor.count > 0) {
            cursor.close()
            false
        } else {
            val cv = ContentValues()
            cv.put(COLUMN_USER_NAME, user.getUsername())
            cv.put(COLUMN_PASSWORD, user.getPassword())
            cv.put(COLUMN_FTP, user.getFTP())
            cv.put(COLUMN_PZ_1, user.getPz_1())
            cv.put(COLUMN_PZ_2, user.getPz_2())
            cv.put(COLUMN_PZ_3, user.getPz_3())
            cv.put(COLUMN_PZ_4, user.getPz_4())
            cv.put(COLUMN_PZ_5, user.getPz_5())
            cv.put(COLUMN_PZ_6, user.getPz_6())
            cv.put(COLUMN_PZ_7, user.getPz_7())
            //ID is a auto increment in the database
            val insert = db.insert(USER_INFO, null, cv)
            cursor.close()
            insert != -1L
        }
    }

    fun add_dataframe33(dataframe33: data33): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TIME_33, dataframe33.getTime_33())
        cv.put(COLUMN_INTERVAL, dataframe33.getInterval())
        cv.put(COLUMN_POWER, dataframe33.getPower())
        cv.put(COLUMN_TOTAL_CAL, dataframe33.getTotal_cal())
        cv.put(COLUMN_SPLIT_PACE, dataframe33.getSplit_pace())
        cv.put(COLUMN_SPLIT_POWER, dataframe33.getSplit_power())
        cv.put(COLUMN_SPLIT_CAL, dataframe33.getSplit_cal())
        cv.put(COLUMN_LAST_SPLIT_TIME, dataframe33.getLast_split_time())
        cv.put(COLUMN_LAST_SPLIT_DIST, dataframe33.getLast_split_dist())

        //ID is a auto increment in the database
        val insert = db.insert(DATAFRAME33_INFO, null, cv)
        return insert != -1L
    }

    fun add_dataframe35(dataframe35: data35): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TIME_35, dataframe35.getTime_35())
        cv.put(COLUMN_DIST, dataframe35.getDist())
        cv.put(COLUMN_DRIVE_LEN, dataframe35.getDrive_len())
        cv.put(COLUMN_DRIVE_TIME, dataframe35.getDrive_time())
        cv.put(COLUMN_STROKE_REC_TIME, dataframe35.getStroke_rec_time())
        cv.put(COLUMN_STROKE_DIST, dataframe35.getStroke_dist())
        cv.put(COLUMN_PEAK_DRIVE_FORCE, dataframe35.getPeak_drive_force())
        cv.put(COLUMN_AVG_DRIVE_FORCE, dataframe35.getAvg_drive_force())
        cv.put(COLUMN_WORK_PER_STROKE, dataframe35.getWork_per_stroke())
        cv.put(COLUMN_STROKE_COUNT, dataframe35.getStroke_count())
        //ID is a auto increment in the database
        val insert = db.insert(DATAFRAME35_INFO, null, cv)
        return insert != -1L
    }

    fun add_history(User: String?, workout: String?, error: Int, avg_power: Double): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_USER, User)
        cv.put(COLUMN_WORKOUT, workout)
        cv.put(COLUMN_ERROR, error)
        cv.put(COLUMN_AVGPOWER, avg_power)

        //ID is a auto increment in the database
        val insert = db.insert(HISTORY_INFO, null, cv)
        return insert != -1L
    }

    /*    public boolean add_error(String User, int error) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER, User);
        cv.put(COLUMN_ERROR, error);

        //ID is a auto increment in the database
        long insert = db.insert(ERROR_INFO, null, cv);
        return insert != -1;
    }*/
    //delete methods
    //delete one account from table
    fun delete_account(username: String, password: String): Boolean //Username and password entered
    {
        val DB = this.writableDatabase
        val cursor1 = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data

        //password match
        var correct_password: String? = null
        while (cursor1.moveToNext()) {
            val index = cursor1.getColumnIndex(COLUMN_PASSWORD)
            correct_password = cursor1.getString(index)
        }
        val password_match = password == correct_password
        return if ((cursor1.count > 0) and password_match) {
            //delete account from all tables
            val result1 = DB.delete(USER_INFO, "COLUMN_USER_NAME=?", arrayOf(username)).toLong()
            val result2 = DB.delete(HISTORY_INFO, "COLUMN_USER=?", arrayOf(username)).toLong()
            //long result3 = DB.delete(ERROR_INFO, "COLUMN_USER=?", new String[]{username});  //Deleted Error Table
            cursor1.close()
            //if (result1 == -1 || result2 == -1 || result3 == -1 ) { //deleted Error Table
            if (result1 == -1L || result2 == -1L) {                     //deleted Error Table
                false
            } else {
                true
            }
        } else {
            cursor1.close()
            false
        }
    }

    fun delete_dataframe33_table(): Boolean {
        val db = this.writableDatabase
        db.execSQL("delete from " + DATAFRAME33_INFO)
        return true
    }

    fun delete_dataframe35_table(): Boolean {
        val db = this.writableDatabase
        db.execSQL("delete from " + DATAFRAME35_INFO)
        return true
    }

    fun delete_table3D(): Boolean? {
        val db = this.writableDatabase
        db.execSQL("delete from " + INFO3D)
        return true
    }

    //updating methods
    fun updateuserFTP(
        username: String,
        FTP: Int,
        pz_1: Int,
        pz_2: Int,
        pz_3: Int,
        pz_4: Int,
        pz_5: Int,
        pz_6: Int,
        pz_7: Int
    ): Boolean {
        val DB = this.writableDatabase
        val cv = ContentValues()
        //cv.put(COLUMN_PASSWORD, password);
        cv.put(COLUMN_FTP, FTP)
        cv.put(COLUMN_PZ_1, pz_1)
        cv.put(COLUMN_PZ_2, pz_2)
        cv.put(COLUMN_PZ_3, pz_3)
        cv.put(COLUMN_PZ_4, pz_4)
        cv.put(COLUMN_PZ_5, pz_5)
        cv.put(COLUMN_PZ_6, pz_6)
        cv.put(COLUMN_PZ_7, pz_7)
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) // find in table
        return if (cursor.count > 0) {
            val result = DB.update(USER_INFO, cv, "COLUMN_USER_NAME = ?", arrayOf(username))
                .toLong() //update table
            cursor.close()
            result != -1L
        } else {
            cursor.close()
            false
        }
    }

    fun updateuserPassword(username: String, password: String?): Boolean {
        val DB = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_PASSWORD, password)
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) // find in table
        return if (cursor.count > 0) {
            val result = DB.update(USER_INFO, cv, "COLUMN_USER_NAME = ?", arrayOf(username))
                .toLong() //update table
            cursor.close()
            result != -1L
        } else {
            cursor.close()
            false
        }
    }

    //Other methods
    fun user_exists(username: String): Boolean {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        return if (cursor.count > 0) {
            cursor.close()
            true
        } else {
            cursor.close()
            false
        }
    }

    fun get_history(username: String): Cursor {  //display history table user specific
        val DB = this.readableDatabase
        return DB.rawQuery("Select * from history_info where COLUMN_USER = ?", arrayOf(username))
    }

    fun getAllPower(username: String, workout_type: String): ArrayList<Double> {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from history_info where COLUMN_USER = ? AND + COLUMN_WORKOUT = ?",
            arrayOf(username, workout_type)
        )
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + User.TABLE_NAME + " WHERE "+ User.COL_NAME + " = ? AND " + User.COL_PASS +" = ?", new String[]{UserName , PassWord});
        val allPower = ArrayList<Double>()
        while (cursor.moveToNext()) {
            allPower.add(cursor.getDouble(5))
        }
        cursor.close()
        return allPower
    }

    /*    public Cursor get_error(String username) {  //display history table user specific
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("Select * from error_info where COLUMN_USER = ?", new String[]{username});//Find the data
        return cursor;
    }*/





    //Go getters for User
    fun getPassword(username: String): String //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PASSWORD)
                val password = cursor.getString(index)
                cursor.close()
                return password
            }
        } else {
            cursor.close()
            return "no Password"
        }
        return "no Password"
    }

    fun getFTP(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val FTP_index = cursor.getColumnIndex("COLUMN_FTP")
                val FTP = cursor.getInt(FTP_index)
                cursor.close()
                return FTP
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_1(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_1)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_2(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_2)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_3(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_3)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_4(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_4)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_5(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_5)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_6(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_6)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }

    fun getPZ_7(username: String): Int //Username and password entered
    {
        val DB = this.readableDatabase
        val cursor = DB.rawQuery(
            "Select * from user_info where COLUMN_USER_NAME = ?",
            arrayOf(username)
        ) //Find the data
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(COLUMN_PZ_7)
                val pz = cursor.getInt(index)
                cursor.close()
                return pz
            }
        } else {
            cursor.close()
            return -1
        }
        return -1
    }//Find the data

    //getters for databaseHelper33
    //Username and password entered
    val time_33: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_TIME_33)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastTime_33: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_TIME_33)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val interval: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_INTERVAL)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastInterval: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_INTERVAL)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val power: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_POWER)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastPower: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_POWER)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val total_cal: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_TOTAL_CAL)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastTotal_cal: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_TOTAL_CAL)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val split_pace: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_SPLIT_PACE)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastSplit_pace: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_SPLIT_PACE)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val split_power: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_SPLIT_POWER)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastSplit_power: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_SPLIT_POWER)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val split_cal: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_SPLIT_CAL)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastSplit_cal: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_SPLIT_CAL)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val last_split_time: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_LAST_SPLIT_TIME)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastLast_split_time: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_LAST_SPLIT_TIME)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val last_split_dist: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_LAST_SPLIT_DIST)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //Username and password entered
    val pastLast_split_dist: Int
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe33_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_LAST_SPLIT_DIST)
                    val pz = cursor.getInt(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return -1
            }
            return -1
        }//Find the data

    //getters for databaseHelper35
    //Username and password entered
    val time_35: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_TIME_35)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastTime_35: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_TIME_35)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val dist: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_DIST)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastDist: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_DIST)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val drive_len: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_DRIVE_LEN)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastDrive_len: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_DRIVE_LEN)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val drive_time: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_DRIVE_TIME)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastDrive_time: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_DRIVE_TIME)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val stroke_rec_time: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_STROKE_REC_TIME)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastStroke_rec_time: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_STROKE_REC_TIME)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val stroke_dist: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_STROKE_DIST)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastStroke_dist: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_STROKE_DIST)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val peak_drive_force: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_PEAK_DRIVE_FORCE)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastPeak_drive_force: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_PEAK_DRIVE_FORCE)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val avg_drive_force: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_PEAK_DRIVE_FORCE)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastAvg_drive_force: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_PEAK_DRIVE_FORCE)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val work_per_stroke: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_WORK_PER_STROKE)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastWork_per_stroke: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_WORK_PER_STROKE)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val stroke_count: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_STROKE_COUNT)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }//Find the data

    //Username and password entered
    val pastStroke_count: Double
        get() {
            val DB = this.readableDatabase
            val cursor = DB.rawQuery(
                "Select * from dataframe35_info ORDER BY COLUMN_ID DESC LIMIT 1,1",
                null
            ) //Find the data
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(COLUMN_STROKE_COUNT)
                    val pz = cursor.getDouble(index)
                    cursor.close()
                    return pz
                }
            } else {
                cursor.close()
                return (-1).toDouble()
            }
            return (-1).toDouble()
        }

    companion object {
        const val USER_INFO = "user_info"
        const val COLUMN_USER_NAME = "COLUMN_USER_NAME"
        const val COLUMN_FTP = "COLUMN_FTP"
        const val COLUMN_ID = "COLUMN_ID"
        const val COLUMN_PASSWORD = "COLUMN_PASSWORD"
        const val COLUMN_PZ_1 = "COLUMN_PZ1"
        const val COLUMN_PZ_2 = "COLUMN_PZ2"
        const val COLUMN_PZ_3 = "COLUMN_PZ3"
        const val COLUMN_PZ_4 = "COLUMN_PZ4"
        const val COLUMN_PZ_5 = "COLUMN_PZ5"
        const val COLUMN_PZ_6 = "COLUMN_PZ6"
        const val COLUMN_PZ_7 = "COLUMN_PZ7"
        const val DATAFRAME33_INFO = "dataframe33_info"
        const val COLUMN_TIME_33 = "COLUMN_TIME_33"
        const val COLUMN_INTERVAL = "COLUMN_INTERVAL"
        const val COLUMN_POWER = "COLUMN_POWER"
        const val COLUMN_TOTAL_CAL = "COLUMN_TOTAL_CAL"
        const val COLUMN_SPLIT_PACE = "COLUMN_SPLIT_PACE"
        const val COLUMN_SPLIT_POWER = "COLUMN_SPLIT_POWER"
        const val COLUMN_SPLIT_CAL = "COLUMN_SPLIT_CAL"
        const val COLUMN_LAST_SPLIT_TIME = "COLUMN_LAST_SPLIT_TIME"
        const val COLUMN_LAST_SPLIT_DIST = "COLUMN_LAST_SPLIT_DIST"
        const val DATAFRAME35_INFO = "dataframe35_info"
        const val COLUMN_TIME_35 = "COLUMN_TIME_35"
        const val COLUMN_DIST = "COLUMN_DIST"
        const val COLUMN_DRIVE_LEN = "COLUMN_DRIVE_LEN"
        const val COLUMN_DRIVE_TIME = "COLUMN_DRIVE_TIME"
        const val COLUMN_STROKE_REC_TIME = "COLUMN_STROKE_REC_TIME"
        const val COLUMN_STROKE_DIST = "COLUMN_STROKE_DIST"
        const val COLUMN_PEAK_DRIVE_FORCE = "COLUMN_PEAK_DRIVE_FORCE"
        const val COLUMN_AVG_DRIVE_FORCE = "COLUMN_AVG_DRIVE_FORCE"
        const val COLUMN_WORK_PER_STROKE = "COLUMN_WORK_PER_STROKE"
        const val COLUMN_STROKE_COUNT = "COLUMN_STROKE_COUNT"
        const val HISTORY_INFO = "history_info"
        const val COLUMN_USER = "COLUMN_USER"
        const val COLUMN_TIMESTAMP = "COLUMN_TIMESTAMP"
        const val COLUMN_WORKOUT = "COLUMN_WORKOUT"

        //public static final String ERROR_INFO = "error_info";
        const val COLUMN_ERROR = "COLUMN_ERROR"
        const val COLUMN_AVGPOWER = "COLUMN_AVGPOWER"
        const val INFO3D = "info3D"
        const val COLUMN_MESSAGE = "COLUMN_MESSAGE"
        const val COLUMN_FORCEVALS1 = "COLUMN_FORCEVALS1"
        const val COLUMN_FORCEVALS2 = "COLUMN_FORCEVALS2"
        const val COLUMN_FORCEVALS3 = "COLUMN_FORCEVALS3"
        const val COLUMN_FORCEVALS4 = "COLUMN_FORCEVALS4"
        const val COLUMN_FORCEVALS5 = "COLUMN_FORCEVALS5"
        const val COLUMN_FORCEVALS6 = "COLUMN_FORCEVALS6"
        const val COLUMN_FORCEVALS7 = "COLUMN_FORCEVALS7"
        const val COLUMN_FORCEVALS8 = "COLUMN_FORCEVALS8"
        const val COLUMN_FORCEVALS9 = "COLUMN_FORCEVALS9"
        const val COLUMN_FORCEVALS10 = "COLUMN_FORCEVALS10"
    }
}