package com.ti.neurow.wkt


import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ti.neurow.GlobalVariables
import com.ti.neurow.VariableChanges
import com.ti.neurow.db.DatabaseHelper

class workouts : AppCompatActivity() {
    private var ftp = 45
    private var fail_count_1 = 0
    private var fail_count_2 = 0
    private var fail_count_3 = 0
    private var pace_fail_20 = 0
    private var pace_fail_30 = 0
    private var pace_fail_40 = 0

    //private var pow_pull = ArrayList<Int>()

    //TODO update naming convention?
    private var pz_1: Int
    private var pz_2: Int
    private var pz_3: Int
    private var pz_4: Int
    private var pz_5: Int
    private var pz_6: Int
    private var pz_7: Int

    init {
        //default ftp value for all new users
        // fail count interval 1 workout
        // fail count interval 1 workout
        // fail count interval 1 workout
        // fail count 20-min pace workout
        // fail count 30-min pace workout
        // fail count 40-min pace workout
        //pow_pull = ArrayList()
        pz_1 = 0
        pz_2 = 25
        pz_3 = 34
        pz_4 = 40
        pz_5 = 47
        pz_6 = 54
        pz_7 = 67
    }

    // ftp calculator workout method
    fun ftpCalc(myChanges: VariableChanges , db: DatabaseHelper): ArrayList<Double> {
        //ftp calculator code that calculates ftp and defines power zones
        //var sum = 0 //uncomment
        //var length = 0 //uncomment
        //define one arraylist to return from function
        //holds time in odd indices and power in even indices
        val powtimearray = arrayListOf<Double>()

        // [TEST] Testing UI Integration
        val message = "You aren't in power zone 2!"
        myChanges.setMessage(message)

/*        while (db.time_33 < 1200) { //uncomment
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
        }*/
        //val avgPow = sum.toDouble() / length //uncomment
        val avgPow = 50
        //define ftp = 95% of average power
        ftp = (0.95 * avgPow).toInt() //uncomment

        // Load power zones
        pz_1 = 0 //Very Easy: <55% of FTP
        pz_2 = (0.56 * ftp).toInt() //Moderate: 56%-75% of FTP
        pz_3 = (0.76 * ftp).toInt() //Sustainable: 76%-90% of FTP
        pz_4 = (0.91 * ftp).toInt() //Challenging: 91%-105% of FTP
        pz_5 = (1.06 * ftp).toInt() //Hard: 106%-120% of FTP
        pz_6 = (1.21 * ftp).toInt() //Very Hard: 121%-150% of FTP
        pz_7 = (1.51 * ftp).toInt() //Max Effort: >151% of FTP

        //this is just for testing, delete
        pz_1 = 1
        pz_2 = 1
        pz_3 = 1
        pz_4 = 1
        pz_5 = 1
        pz_6 = 1
        pz_7 = 1
        ftp = 45;

        powtimearray.add(1.0)
        powtimearray.add(1.0)
        powtimearray.add(2.0)
        powtimearray.add(2.0)
        powtimearray.add(3.0)
        powtimearray.add(3.0)
        powtimearray.add(4.0)
        powtimearray.add(4.0)

        db.updateuserFTP(GlobalVariables.loggedInUsername, this.ftp, this.pz_1, this.pz_2, this.pz_3, this.pz_4, this.pz_5, this.pz_6, this.pz_7);

        //return arraylist of time and power
        return powtimearray
    }

    // interval_1 workout method, 20 min
    fun interval_1(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, db: DatabaseHelper): ArrayList<Double> {
        //interval_1 code
        var count = 0
        var k1 = 0
        var k2 = 0
        var k3 = 0
        var k4 = 0
        var k5 = 0
        var k6 = 0
        var k7 = 0
        var k8 = 0
        var k9 = 0
        var k10 = 0
        var k11 = 0
        var k12 = 0
        var k13 = 0
        var k14 = 0
        var k15 = 0
        var k16 = 0
        var k17 = 0
        var k18 = 0
        var k19 = 0
        var k20 = 0
        var k21 = 0
        var k22 = 0

        var sum = 0
        var length = 0
        val powtimearray = arrayListOf<Double>()

        var pzMessage = ""
        var fixMessage = ""

        // 5 min at zone 2
        while (db.time_33 <= 300) {
            //this is adding all of the powers to then get average
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k1 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k1 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {    // only consecutive power zone exits increment count
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 340 && db.time_33 > 300) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k2 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k2 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 360 && db.time_33 > 340) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k3 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k3 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 400 && db.time_33 > 360) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k4 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k4 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 420 && db.time_33 > 400) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k5 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k5 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 460 && db.time_33 > 420) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k6 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k6 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 480 && db.time_33 > 460) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k7 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k7 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 520 && db.time_33 > 480) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k8 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k8 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 540 && db.time_33 > 520) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k9 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k9 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 580 && db.time_33 > 540) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k10 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k10 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 600 && db.time_33 > 580) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k11 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k11 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 640 && db.time_33 > 600) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k12 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k12 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 660 && db.time_33 > 640) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k13 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k13 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 700 && db.time_33 > 660) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k14 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k14 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 720 && db.time_33 > 700) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k15 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k15 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 760 && db.time_33 > 720) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k16 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k16 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 780 && db.time_33 > 760) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k17 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k17 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 820 && db.time_33 > 780) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k18 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k18 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 840 && db.time_33 > 820) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k19 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k19 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 40 sec at zone 5
        while (db.time_33 <= 880 && db.time_33 > 840) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k20 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k20 = 100
            }
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 20 sec at zone 2
        while (db.time_33 <= 900 && db.time_33 > 880) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k21 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k21 = 100
            }
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 1
        while (db.time_33 <= 1200 && db.time_33 > 900) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k22 == 0) {
                pzMessage = "Row in power zone 1"
                pzSetChanges.setMessage(pzMessage)
                k22 = 100
            }
            if (db.power >= pz_2) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 1!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_1++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        val avgPow = sum.toDouble() / length //uncomment
        // TODO add avg power to database
        //}
        //println("fail count: $fail_count_1")
        //interval_predictor("1", fail_count_1)
        //TODO add line to add failcount to database
        fail_count_1 = 25 // for  integration testing
        db.add_history(GlobalVariables.loggedInUsername, "interval1", fail_count_1, avgPow)
        return powtimearray
    }

    // interval_2 workout method, 30 min
    fun interval_2(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, db: DatabaseHelper): ArrayList<Double> {
        //interval_2 code
        println("You are now in the interval_2 workout!")
        var count = 0
        var k1 = 0
        var k2 = 0
        var k3 = 0
        var k4 = 0
        var k5 = 0
        var k6 = 0
        var sum = 0
        var length = 0
        val powtimearray = arrayListOf<Double>()

        var pzMessage = ""
        var fixMessage = ""

        // 6 min at zone 3
        while (db.time_33 <= 360) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k1 == 0) {
                pzMessage = "Row in power zone 3"
                pzSetChanges.setMessage(pzMessage)
                k1 = 100
            }
            if (db.power < pz_3 || db.power >= pz_4) {
                count++
                if (count > 2) {
                    fixMessage = "You aren't in power zone 3!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_2++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 1
        while (db.time_33 <= 660 && db.time_33 > 360) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k2 == 0) {
                pzMessage = "Row in power zone 1"
                pzSetChanges.setMessage(pzMessage)
                k2 = 100
            }
            if (db.power >= pz_2) {
                count++
                if (count > 2) {
                    fixMessage = "You aren't in power zone 1!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_2++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 4
        while (db.time_33 <= 960 && db.time_33 > 660) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k3 == 0) {
                pzMessage = "Row in power zone 4"
                pzSetChanges.setMessage(pzMessage)
                k3 = 100
            }
            //System.out.println("Row in power zone 4");
            if (db.power < pz_4 || db.power >= pz_5) {
                count++
                if (count > 2) {
                    fixMessage = "You aren't in power zone 4!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_2++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 1
        while (db.time_33 <= 1260 && db.time_33 > 960) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k4 == 0) {
                pzMessage = "Row in power zone 1"
                pzSetChanges.setMessage(pzMessage)
                k4 = 100
            }
            if (db.power >= pz_2) {
                count++
                if (count > 2) {
                    fixMessage = "You aren't in power zone 1!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_2++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 4 min at zone 5
        while (db.time_33 <= 1500 && db.time_33 > 1260) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k5 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k5 = 100
            }
            //System.out.println("Row in power zone 5");
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 2) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_2++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 1
        while (db.time_33 <= 1800 && db.time_33 > 1500) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k6 == 0) {
                pzMessage = "Row in power zone 1"
                pzSetChanges.setMessage(pzMessage)
                k6 = 100
            }
            //System.out.println("Row in power zone 1");
            if (db.power >= pz_2) {
                count++
                if (count > 2) {
                    fixMessage = "You aren't in power zone 1!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_2++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        val avgPow = sum.toDouble() / length //uncomment
        // TODO add avg power to database
        //}
        //println("fail count: $fail_count_2")
        //interval_predictor("2", fail_count_2)
        //TODO add fail count to database
        fail_count_2 = 25 //for database integration testing
        db.add_history(GlobalVariables.loggedInUsername, "interval2", fail_count_2, avgPow)
        return powtimearray
    }

    // interval_3 workout method, 40 min
    fun interval_3(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, db: DatabaseHelper): ArrayList<Double> {
        var count = 0
        var k1 = 0
        var k2 = 0
        var k3 = 0
        var k4 = 0
        var k5 = 0
        var k6 = 0
        var k7 = 0
        var k8 = 0
        var k9 = 0
        var k10 = 0
        var k11 = 0
        var sum = 0
        var length = 0
        val powtimearray = arrayListOf<Double>()

        var pzMessage = ""
        var fixMessage = ""

        // 2 min at zone 2
        while (db.time_33 <= 120) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k1 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k1 = 100
            }
            //System.out.println("Row in power zone 2");
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 1 min at zone 5
        while (db.time_33 <= 180 && db.time_33 > 120) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k2 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k2 = 100
            }
            //System.out.println("Row at a fast pace!!");
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 2 min at zone 2
        while (db.time_33 <= 300 && db.time_33 > 180) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k3 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k3 = 100
            }
            //System.out.println("Row in power zone 2");
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 1 min at zone 5
        while (db.time_33 <= 360 && db.time_33 > 300) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k4 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k4 = 100
            }
            //System.out.println("Row at a fast pace!!");
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 2 min at zone 2
        while (db.time_33 <= 480 && db.time_33 > 360) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k5 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k5 = 100
            }
            //System.out.println("Row in power zone 2");
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 1 min at zone 5
        while (db.time_33 <= 540 && db.time_33 > 480) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k6 == 0) {
                pzMessage = "Row in power zone 5"
                pzSetChanges.setMessage(pzMessage)
                k6 = 100
            }
            //System.out.println("Row at a fast pace!!");
            if (db.power < pz_5 || db.power >= pz_6) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 5!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 2 min at zone 2
        while (db.time_33 <= 660 && db.time_33 > 540) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k7 == 0) {
                pzMessage = "Row in power zone 2"
                pzSetChanges.setMessage(pzMessage)
                k7 = 100
            }
            //System.out.println("Row in power zone 2");
            if (db.power < pz_2 || db.power >= pz_3) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 2!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 10 min at zone 4
        while (db.time_33 <= 1260 && db.time_33 > 660) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k8 == 0) {
                pzMessage = "Row in power zone 4"
                pzSetChanges.setMessage(pzMessage)
                k8 = 100
            }
            //System.out.println("Row in power zone 4");
            if (db.power < pz_4 || db.power >= pz_5) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 4!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 1
        while (db.time_33 <= 1560 && db.time_33 > 1260) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k9 == 0) {
                pzMessage = "Row in power zone 1"
                pzSetChanges.setMessage(pzMessage)
                k9 = 100
            }
            //System.out.println("Row in power zone 1");
            if (db.power >= pz_2) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 1!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 10 min at zone 4
        while (db.time_33 <= 2160 && db.time_33 > 1560) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k10 == 0) {
                pzMessage = "Row in power zone 4"
                pzSetChanges.setMessage(pzMessage)
                k10 = 100
            }
            //System.out.println("Row in power zone 4");
            if (db.power < pz_4 || db.power >= pz_5) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 4!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        // 5 min at zone 1
        while (db.time_33 <= 2460 && db.time_33 > 2160) {
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            if (k11 == 0) {
                pzMessage = "Row in power zone 1"
                pzSetChanges.setMessage(pzMessage)
                k11 = 666
            }
            //System.out.println("Row in power zone 1");
            if (db.power >= pz_2) {
                count++
                if (count > 4) {
                    fixMessage = "You aren't in power zone 1!!!!"
                    pzFixChanges.setMessage(fixMessage)
                    fail_count_3++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        val avgPow = sum.toDouble() / length //uncomment

        //interval_predictor("2", fail_count_2)
        fail_count_3 = 66 //for database integration testing
        db.add_history(GlobalVariables.loggedInUsername, "interval3", fail_count_3, avgPow)
        return powtimearray
    }

    fun pace(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, length: Int, db: DatabaseHelper): ArrayList<Double> {
        //pace code
        var count = 0
        val j = 0
        var sum = 0
        var length = 0
        val powtimearray = arrayListOf<Double>()

        var pzMessage = ""
        var fixMessage = ""

        if (length == 20) {
            pzMessage = "Begin Rowing!"
            pzSetChanges.setMessage(pzMessage)
            while (db.time_33 <= 1200) { // less than 20-min
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.power - db.pastPower) > 4) {
                    count++
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent! Try to improve pacing!"
                        pzFixChanges.setMessage(fixMessage)
                        pace_fail_20++
                        count = 0
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            //}
            pace_fail_20 = 202020 //for integration testing
            //pace_predictor("20", pace_fail_20)
            val avgPow = sum.toDouble() / length //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace20", pace_fail_20, avgPow)
        } else if (length == 30) {
            pzMessage = "Begin Rowing!"
            pzSetChanges.setMessage(pzMessage)
            while (db.time_33 <= 1800) { // less than 30-min
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.power - db.pastPower) > 4) {
                    count++
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent! Try to improve pacing!"
                        pzFixChanges.setMessage(fixMessage)
                        pace_fail_30++
                        count = 0
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            //}
            pace_fail_30 = 303030 // for integration testing
            //pace_predictor("30", pace_fail_30)
            val avgPow = sum.toDouble() / length //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace30", pace_fail_30, avgPow)
        } else if (length == 40) {
            pzMessage = "Begin Rowing!"
            pzSetChanges.setMessage(pzMessage)
            while (db.time_33 <= 2400) { // less than 40-min
                sum += db.power
                length += 1
                powtimearray.add(db.time_33)
                powtimearray.add(db.power.toDouble())
                //if difference between current stroke and previous stroke is greater than 4 watts
                if (Math.abs(db.power - db.pastPower) > 4) {
                    count++
                    if (count > 2) {
                        fixMessage = "Your power output is inconsistent! Try to improve pacing!"
                        pzFixChanges.setMessage(fixMessage)
                        pace_fail_40++
                        count = 0
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            pace_fail_40 = 404040 //for integration testing
            //pace_predictor("40", pace_fail_40)
            val avgPow = sum.toDouble() / length //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace40", pace_fail_40, avgPow)
        }
        return powtimearray
    }

    // Returns value of predicted power
    fun powerPredictor(power: ArrayList<Double>): Double {
        println("in predictor")
        //TODO clean up code
        //TODO integrate UI into it

        //TODO CHECK INT TO DOUBLE CONVERSION
        //TODO add statements about if list is empty or what to do if just one element
        //TODO two elements is ok?
        println("power array " + power)
        // declare empty x arraylist
        val x = ArrayList<Int>()
        val length = power.size
        // sum up powers and x sequence
        var x_sum = 0
        var y_sum = 0.0
        for (i in power.indices) {
            x.add(i) //adding the sequence of x values into arraylist
            y_sum += power[i]
            x_sum += i
        }
        println("x sum " + x_sum)
        println("y sum " + y_sum)

        //calculate mean of x values
        val x_mean: Double = x_sum.toDouble() / length
        println("x mean " + x_mean)
        //calculate mean of y values
        val y_mean = y_sum / length
        println("y mean " + y_mean)

        //numerator of slope equation
        var num_sum = 0.0
        var x_diff = 0.0
        var y_diff = 0.0
        for (i in 0..length-1){
            x_diff = x[i] - x_mean
            y_diff = power[i] - y_mean
            num_sum += x_diff * y_diff
        }
        println("num sum " + num_sum)
        //denominator of slope equation
        var den_sum = 0.0
        var x_diff_den = 0.0
        for (i in 0..length-1){
            x_diff_den = x[i] - x_mean
            den_sum += x_diff_den * x_diff_den
        }
        println("den sum " + den_sum)

        //compute slope of line of best fit
        val slope = num_sum / den_sum
        println("slope " + slope)
        //compute y intercept of line of best fit
        val y_int = y_mean - slope * x_mean
        println("y int " + y_int)
        // in five more workouts your power output might be...
        // y = mx + b
        val predic = (slope * (length + 4)) + y_int // TODO is that length + 4 correct?
        // TODO does the predicted power need to be an integer or not
        // TODO what if prediction is negative? how to handle
        println("In 5 more workouts your power could be " + predic + " watts")
        println("end predictor")

        return predic
    }


    companion object {
        /*    static void two_predictor() {
        // TODO wait until later to work with UI on this
        //2k predictor code
        System.out.println("You are now in the 2k predictor workout!");
        //
    }*/
        // TODO finish both graphing functions once diego knows how he wants packaged
        /*    public ArrayList<DataPoint> power_pull() {
        //power of pull graph for all workouts
        // had to have sequence be Doubles bc DataPoint class uses double
        System.out.println("You are in power of pull graph method!");

        String file = "C:/Users/alyso/TAMU/2022Fall/ECEN_403_907/Psuedo_Data/pow_pull.csv";
        CSVReaderTest.read_pow_pull(file, pow_pull);
        ArrayList<DataPoint> data = new ArrayList<DataPoint>();
        Double k = 0.0;
        // TODO need to know how many entries in table?
        // TODO does this need to be a new function
        // TODO but the table will have multiple graphs and we are averaging?
        // TODO or is this one graph at a time
        for (int j = 0; j < pow_pull.size(); j++) {
            data.add(new DataPoint(k, pow_pull.get(j)));
            k++;
        }
       return data;
    }*/
        /*    public ArrayList<DataPoint> power_vs_time() {
        // TODO wait until later to work with UI on this
        //power vs time graph for pace workout
        //time_33 and power
        System.out.println("You are in power vs time graph method!");
        DatabaseHelper db = new DatabaseHelper(workouts.this);

        ArrayList<DataPoint> data = new ArrayList<DataPoint>();
        // TODO need to know length of table of times?
        for (int j = 0; j < time_33.size(); j++) {
            double time = (double) db.getTime_33();
            data.add(new DataPoint(time, db.getPower()));
        }
        return data;
    }*/


        fun interval_predictor(interval_num: String, fail_count: Int) {
            println()
            println("You are in interval predictor method!")
            // TODO incorporate UI
            // TODO toast or println function
            //finish, did i finish?
            //interval method 1 predictions
            if (interval_num === "1") {
                if (fail_count > 20) {
                    println("You left your power zone a lot! Try redoing your FTP Calculator workout")
                } else {
                    println("You did well! Go again or try a longer interval workout!")
                }
            } else if (interval_num === "2") {
                if (fail_count > 20 && fail_count < 50) {
                    println("You left your power zone often. Try doing the Interval 1 workout")
                } else if (fail_count >= 50) {
                    println("You left your power zone a lot! Try redoing your FTP Calculator workout")
                } else {
                    println("You did well! Go again or try a longer interval workout!")
                }
            } else if (interval_num === "3") {
                if (fail_count > 20 && fail_count < 50) {
                    println("You left your power zone often. Try doing the Interval 2 workout")
                } else if (fail_count >= 50) {
                    println("You left your power zone a lot! Try redoing your FTP Calculator workout")
                } else {
                    println("You did well! Go again or try redoing your FTP Calculator workout!")
                }
            }
        }

        fun pace_predictor(pace_num: String, fail_count: Int) {
            println()
            println("You are in pace predictor method!")
            // TODO integrate UI
            // TODO toast or println functions
            //interval method 1 predictions
            //finish, did i finish?
            if (pace_num === "20") {
                if (fail_count > 14) {
                    println("Your pace was very inconsistent. Try again or do an interval workout to work on fitness")
                } else {
                    println("You did well! Go again or try a longer pace workout!")
                }
            } else if (pace_num === "30") {
                if (fail_count > 14) {
                    println("Your pace was very inconsistent. Try doing the 20 min pace workout instead")
                } else {
                    println("You did well! Go again or try a longer pace workout!")
                }
            } else if (pace_num === "40") {
                if (fail_count > 14) {
                    println("Your pace was very inconsistent. Try doing the 30 min pace workout instead")
                } else {
                    println("You did well! Nice work!")
                }
            }
        }
    }
}