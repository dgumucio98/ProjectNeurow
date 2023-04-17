package com.ti.neurow.wkt

import androidx.appcompat.app.AppCompatActivity
import com.ti.neurow.GlobalVariables
import com.ti.neurow.VariableChanges
import com.ti.neurow.db.DatabaseHelper
import timber.log.Timber;

class workouts : AppCompatActivity() {
    private var ftp: Int
    private var pz_1: Int
    private var pz_2: Int
    private var pz_3: Int
    private var pz_4: Int
    private var pz_5: Int
    private var pz_6: Int
    private var pz_7: Int

    init {
        ftp = 45
        pz_1 = 0
        pz_2 = 25
        pz_3 = 34
        pz_4 = 40
        pz_5 = 47
        pz_6 = 54
        pz_7 = 67
    }

    // ftp calculator workout method
    fun ftpCalc(db: DatabaseHelper): ArrayList<Double> {
        //ftp calculator code that calculates ftp and defines power zones
        var sum = 0
        var length = 0
        //define one arraylist to return from function
        //holds time in odd indices and power in even indices
        val powtimearray = arrayListOf<Double>()

        var pastTime = 0.0
        var infiniteCount = 0

        var i = 0
        // [TEST] loop
        while (db.time_33 < 30.0) { //for testing, 3 minutes
            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            pastTime = db.time_33
        }

        //uncomment
/*        while (db.time_33 < 1200) { //uncomment
            //adding break case so no infinite loop
            if (pastTime == db.time_33){
                infiniteCount++
            }
            if (infiniteCount > 5) //how to do a catch for workout infinite loop, have a count if it is above 10 jump?

            sum += db.power
            length += 1
            powtimearray.add(db.time_33)
            powtimearray.add(db.power.toDouble())
            pastTime = db.time_33
        }*/

        val avgPow = sum.toDouble() / length //uncomment

        //define ftp = 95% of average power
        ftp = (0.95 * avgPow).toInt() // calculate ftp
        GlobalVariables.ftp = ftp //set ftp as global so UI can display it

        // Load power zones
        pz_1 = 0 //Very Easy: <55% of FTP
        pz_2 = (0.56 * ftp).toInt() //Moderate: 56%-75% of FTP
        pz_3 = (0.76 * ftp).toInt() //Sustainable: 76%-90% of FTP
        pz_4 = (0.91 * ftp).toInt() //Challenging: 91%-105% of FTP
        pz_5 = (1.06 * ftp).toInt() //Hard: 106%-120% of FTP
        pz_6 = (1.21 * ftp).toInt() //Very Hard: 121%-150% of FTP
        pz_7 = (1.51 * ftp).toInt() //Max Effort: >151% of FTP

        // populate database User table
        db.updateuserFTP(GlobalVariables.loggedInUsername, this.ftp, this.pz_1, this.pz_2, this.pz_3, this.pz_4, this.pz_5, this.pz_6, this.pz_7);

        //return arraylist of time and power
        return powtimearray
    }

    // interval1 workout method, 20 min
    fun interval1(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, db: DatabaseHelper): ArrayList<Double> {
        //interval1 (20 min) method code
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

        var count = 0 //count subsequent errors
        var failCount = 0 //actual fail count
        var sum = 0 //summing up power
        var length = 0 //number of power entries to calc average
        val powtimearray = arrayListOf<Double>() //arraylist to hold time and power

        var pzMessage = "" //declaring power zone message
        var fixMessage = "" //delcaring power zone error message

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
                    failCount++
                    count = 0
                }
            } else {    // only consecutive power zone exits increment count
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
            //TODO: at end of while loop publish variables we want, pzMessage, fixMessage
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        val avgPow = sum.toDouble() / length //uncomment
        //interval_suggestor("1", failCount)
        failCount = 25 // for  integration testing
        GlobalVariables.failCount = failCount
        db.add_history(GlobalVariables.loggedInUsername, "interval1", failCount, avgPow)
        return powtimearray
    }

    // interval2 workout method, 30 min
    fun interval2(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, db: DatabaseHelper): ArrayList<Double> {
        //interval2 (30 min) method code
        var k1 = 0
        var k2 = 0
        var k3 = 0
        var k4 = 0
        var k5 = 0
        var k6 = 0

        var count = 0 //count subsequent errors
        var failCount = 0 //actual fail count
        var sum = 0 //summing up power
        var length = 0 //number of power entries to calc average
        val powtimearray = arrayListOf<Double>() //arraylist to hold time and power

        var pzMessage = "" //declaring power zone message
        var fixMessage = "" //declaring power zone error message

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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        val avgPow = sum.toDouble() / length //uncomment
        //interval_suggestor("2", failCount)
        failCount = 25 //for database integration testing
        GlobalVariables.failCount = failCount
        db.add_history(GlobalVariables.loggedInUsername, "interval2", failCount, avgPow)
        return powtimearray
    }

    // interval3 workout method, 40 min
    fun interval3(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, db: DatabaseHelper): ArrayList<Double> {
        //interval 3 (40 min) method code
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

        var count = 0 //count subsequent errors
        var failCount = 0 //actual fail count
        var sum = 0 //summing up power
        var length = 0 //number of power entries to calc average
        val powtimearray = arrayListOf<Double>() //arraylist to hold time and power

        var pzMessage = "" //declaring power zone message
        var fixMessage = "" //delcaring power zone error message


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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
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
                    failCount++
                    count = 0
                }
            } else {
                fixMessage = "You are in zone!! Keep it up!!"
                pzFixChanges.setMessage(fixMessage)
                count = 0
            }
        }
        val avgPow = sum.toDouble() / length //uncomment

        //interval_suggestor("2", failCount)
        failCount = 66 //for database integration testing
        GlobalVariables.failCount = failCount
        db.add_history(GlobalVariables.loggedInUsername, "interval3", failCount, avgPow)
        return powtimearray
    }

    fun pace(pzSetChanges: VariableChanges, pzFixChanges: VariableChanges, wkrtLength: Int, db: DatabaseHelper): ArrayList<Double> {
        //pace code
        var failCount = 0
        var count = 0
        var sum = 0
        var length = 0
        val powtimearray = arrayListOf<Double>()

        var pzMessage = ""
        var fixMessage = ""

        if (wkrtLength == 20) {
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
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            failCount = 202020 //for integration testing
            GlobalVariables.failCount = failCount
            //pace_suggestor("20", failCount)
            val avgPow = sum.toDouble() / length //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace20", failCount, avgPow)
        } else if (wkrtLength == 30) {
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
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            //}
            failCount = 303030 // for integration testing
            GlobalVariables.failCount = failCount
            //pace_suggestor("30", failCount)
            val avgPow = sum.toDouble() / length //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace30", failCount, avgPow)
        } else if (wkrtLength == 40) {
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
                        failCount++
                        count = 0
                    }
                } else {
                    fixMessage = "Nice pace!! Keep it up!!"
                    pzFixChanges.setMessage(fixMessage)
                    count = 0
                }
            }
            failCount = 404040 //for integration testing
            GlobalVariables.failCount = failCount
            //pace_suggestor("40", failCount)
            val avgPow = sum.toDouble() / length //uncomment
            db.add_history(GlobalVariables.loggedInUsername, "pace40", failCount, avgPow)
        }
        return powtimearray
    }

    // Returns value of users predicted power in five more workouts
    fun powerPredictor(power: ArrayList<Double>): String {
        // declare empty x arraylist
        val x = ArrayList<Int>()
        val length = power.size
        var message = ""
        if (length <= 1) {
            message = "Do more workouts and check back for a prediction"
        }
        else {
            // sum up powers and x sequence
            var x_sum = 0
            var y_sum = 0.0
            for (i in power.indices) {
                x.add(i) //adding the sequence of x values into arraylist
                y_sum += power[i]
                x_sum += i
            }

            //calculate mean of x values
            val x_mean: Double = x_sum.toDouble() / length
            //calculate mean of y values
            val y_mean = y_sum / length

            //numerator of slope equation
            var num_sum = 0.0
            var x_diff = 0.0
            var y_diff = 0.0
            for (i in 0..length - 1) {
                x_diff = x[i] - x_mean
                y_diff = power[i] - y_mean
                num_sum += x_diff * y_diff
            }
            //denominator of slope equation
            var den_sum = 0.0
            var x_diff_den = 0.0
            for (i in 0..length - 1) {
                x_diff_den = x[i] - x_mean
                den_sum += x_diff_den * x_diff_den
            }

            //compute slope of line of best fit
            val slope = num_sum / den_sum

            //compute y intercept of line of best fit
            val y_int = y_mean - slope * x_mean

            // in five more workouts your power output might be...
            // y = mx + b
            val predic = (slope * (length + 4)) + y_int // TODO is that length + 4 correct?
            val formattedPredic = String.format("%.2f", predic)

//            if (formattedPredic == "NaN") {
//                message = "Check back later, NaN error"
//            }
            if (slope < 0) {
                //user is trending backwards
                message = "You are trending downwards. In 5 more workouts your power could be " + formattedPredic + " watts"
            }
            else {
                message = "You are trending upwards. In 5 more workouts your power could be " + formattedPredic + " watts"
            }
        }
        return message
    }

    fun Suggestion(wkrt: String): String {
        var Suggestion = "" //declare string to change and print to screen
        if (wkrt == "ftpCalc") {
            Suggestion = "Now that you have an FTP, try doing an interval workout!"
        }
        else if (wkrt === "interval1") { //20 min interval
            if (GlobalVariables.failCount > 20) {
                Suggestion = "You left your power zone a lot! Try redoing your FTP Calculator workout"
            } else {
                Suggestion = "You did well! Go again or try a longer interval workout!"
            }
        } else if (wkrt === "interval2") { //30 min interval
            if (GlobalVariables.failCount > 20 && GlobalVariables.failCount < 50) {
                Suggestion = "You left your power zone often. Try doing the Interval 1 workout"
            } else if (GlobalVariables.failCount >= 50) {
                Suggestion = "You left your power zone a lot! Try redoing your FTP Calculator workout"
            } else {
                Suggestion = "You did well! Go again or try a longer interval workout!"
            }
        } else if (wkrt === "interval3") { //40 min interval
            if (GlobalVariables.failCount > 20 && GlobalVariables.failCount < 50) {
                Suggestion = "You left your power zone often. Try doing the Interval 2 workout"
            } else if (GlobalVariables.failCount >= 50) {
                Suggestion = "You left your power zone a lot! Try redoing your FTP Calculator workout"
            } else {
                Suggestion = "You did well! Go again or try redoing your FTP Calculator workout!"
            }
        }
        else if (wkrt === "pace20") {
            if (GlobalVariables.failCount > 14) {
                Suggestion = "Your pace was very inconsistent. Try again or do an interval workout to work on fitness"
            } else {
                Suggestion = "You did well! Go again or try a longer pace workout!"
            }
        } else if (wkrt === "pace30") {
            if (GlobalVariables.failCount > 14) {
                Suggestion = "Your pace was very inconsistent. Try doing the 20 min pace workout instead"
            } else {
                Suggestion = "You did well! Go again or try a longer pace workout!"
            }
        } else if (wkrt === "pace40") {
            if (GlobalVariables.failCount > 14) {
                Suggestion = "Your pace was very inconsistent. Try doing the 30 min pace workout instead"
            } else {
                Suggestion = "You did well! Nice work!"
            }
        } else if (wkrt == "demo") {
            Suggestion = "GREAT JOB!"
        }
        return Suggestion
    }
}