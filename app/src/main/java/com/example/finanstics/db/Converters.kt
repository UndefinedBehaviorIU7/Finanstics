package com.example.finanstics.db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.finanstics.presentation.calendar.MonthNameClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromMonthNameClass(month: MonthNameClass): Int {
        return month.number
    }

    @TypeConverter
    fun toMonthNameClass(monthNumber: Int): MonthNameClass {
        return MonthNameClass.entries.first { it.number == monthNumber }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }
}
