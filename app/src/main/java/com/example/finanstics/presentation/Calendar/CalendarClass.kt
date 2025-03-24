package com.example.calendar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.calendar.DayClass.Companion.DayOfWeekInit
import com.example.calendar.MonthNameClass.APRIL
import com.example.calendar.MonthNameClass.AUGUST
import com.example.calendar.MonthNameClass.DECEMBER
import com.example.calendar.MonthNameClass.FEBRUARY
import com.example.calendar.MonthNameClass.JANUARY
import com.example.calendar.MonthNameClass.JULY
import com.example.calendar.MonthNameClass.JUNE
import com.example.calendar.MonthNameClass.MARCH
import com.example.calendar.MonthNameClass.MAY
import com.example.calendar.MonthNameClass.NOVEMBER
import com.example.calendar.MonthNameClass.OCTOBER
import com.example.calendar.MonthNameClass.SEPTEMBER
import java.time.DayOfWeek

enum class MonthNameClass(val number: Int) {
    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12);

    companion object {
        fun fromInt(month: Int): MonthNameClass {
            return values().find { it.number == month }
                ?: throw IllegalArgumentException("Некорректный месяц")
        }

        fun str(month: MonthNameClass): String {
            return when (month) {
                JANUARY -> "Январь"
                FEBRUARY -> "Февраль"
                MARCH -> "Март"
                APRIL -> "Апрель"
                MAY -> "Май"
                JUNE -> "Июнь"
                JULY -> "Июль"
                AUGUST -> "Август"
                SEPTEMBER -> "Сентябрь"
                OCTOBER -> "Октябрь"
                NOVEMBER -> "Ноябрь"
                DECEMBER -> "Декабрь"
            }
        }
    }
}

enum class DayWeekClass(val number: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    companion object {
        fun fromInt(day: Int): DayWeekClass {
            return when (day) {
                2 -> MONDAY
                3 -> TUESDAY
                4 -> WEDNESDAY
                5 -> THURSDAY
                6 -> FRIDAY
                0 -> SATURDAY
                1 -> SUNDAY
                else -> throw IllegalArgumentException("Некорректный день недели")
            }
        }
    }
}


class DayClass(day: Int,month: MonthNameClass, year: Int) {
    val DayOfWeek: DayWeekClass
    val day = day
    val money = 1000
    companion object {
        fun DayOfWeekInit(day: Int, month: MonthNameClass, year: Int): Int {
            val q = day
            var m = month.number
            var y = year
            if (month == JANUARY || month == FEBRUARY) {
                m += 12
                y--
            }

            val k = y % 100
            val j = y / 100
            val res = (q + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 + 5 * j) % 7
            return res
        }
    }

    init {
        DayOfWeek = DayWeekClass.fromInt(DayOfWeekInit(day, month, year))
    }
}



class MountClass(month: MonthNameClass, year: Int)
{

    val CountDays: Int
    val DaysMount: Array<DayClass?>
    val year = year
    val month = month

    init {
        CountDays = CountDaysInit()
        DaysMount = DaysInit()
    }

    fun DaysInit() : Array<DayClass?>{
        var Days = Array<DayClass?>(CountDays) { null }
        for (i in 0..(CountDays - 1))
            Days[i] = DayClass(i + 1, month, year)
        return Days
    }

    fun isLeapYear(): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun CountDaysInit () : Int {
        if (month == FEBRUARY) {
            if (isLeapYear())
                return 29
            else
                return 28
        }
        if (month == APRIL || month == JUNE || month == SEPTEMBER || month == NOVEMBER)
            return 30
        return 31

    }

    fun getDays(first: Int, last: Int): Array<DayClass?>{
        var Days = Array<DayClass?>(last - first) { null }
        for (i in first..last)
            Days[i] = DaysMount[i]
        return Days
    }

    fun getLastDay(): DayClass? {
        return DaysMount[CountDays - 1]
    }
}
class CalendarClass() {
    var day: Int
    var month: MonthNameClass
    var year: Int
    var Days: Array<DayClass?>
    init {
        val calendar = java.util.Calendar.getInstance()
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        month = MonthNameClass.fromInt(calendar.get(java.util.Calendar.MONTH) + 1)
        year = calendar.get(java.util.Calendar.YEAR)
        Days = DaysInit()
    }

    fun DaysInit(): Array<DayClass?> {
        val Days = Array<DayClass?>(42) { null }

        val MountNow = MountClass(month, year)
        val MountNext = if (month != DECEMBER)
            MountClass(MonthNameClass.fromInt(month.number + 1), year)
        else
            MountClass(JANUARY, year + 1)

        val MountLast = if (month != JANUARY)
            MountClass(MonthNameClass.fromInt(month.number - 1), year)
        else
            MountClass(DECEMBER, year - 1)

        val firstDayIndex = MountNow.DaysMount[0]?.DayOfWeek?.number

        for (i in 0 until firstDayIndex!! - 1) {
            val dayIndex = MountLast.CountDays - firstDayIndex + i + 1
            Days[i] = MountLast.DaysMount[dayIndex]
        }

        val CountDayLastMount = firstDayIndex - 1

        for (i in 0 until MountNow.CountDays) {
            val dayIndex = CountDayLastMount + i
            Days[dayIndex] = MountNow.DaysMount[i]
        }

        val lastDayIndex = MountNow.DaysMount[MountNow.CountDays - 1]?.DayOfWeek?.number

        for (i in 0 until 7 - lastDayIndex!!) {
            val dayIndex = CountDayLastMount + MountNow.CountDays + i
            Days[dayIndex] = MountNow.DaysMount[i]
        }

        return Days
    }

    fun NextMount()
    {
        if (month != DECEMBER)
        {
            month = MonthNameClass.fromInt(month.number + 1)
        }
        else
        {
            month = JANUARY
            year += 1
        }
        Days = DaysInit()
    }

    fun LastMount()
    {
        if (month != JANUARY)
        {
            month = MonthNameClass.fromInt(month.number - 1)
        }
        else
        {
            month = DECEMBER
            year -= 1
        }
        Days = DaysInit()
    }

//    fun copy() : CalendarClass() {
//
//    }

}