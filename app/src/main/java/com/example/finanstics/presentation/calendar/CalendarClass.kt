package com.example.finanstics.presentation.calendar

import android.util.Log
import com.example.finanstics.presentation.calendar.MonthNameClass.APRIL
import com.example.finanstics.presentation.calendar.MonthNameClass.DECEMBER
import com.example.finanstics.presentation.calendar.MonthNameClass.FEBRUARY
import com.example.finanstics.presentation.calendar.MonthNameClass.JANUARY
import com.example.finanstics.presentation.calendar.MonthNameClass.JUNE
import com.example.finanstics.presentation.calendar.MonthNameClass.NOVEMBER
import com.example.finanstics.presentation.calendar.MonthNameClass.SEPTEMBER

private const val DAYS_IN_MONTH_28 = 28
private const val DAYS_IN_MONTH_29 = 29
private const val DAYS_IN_MONTH_30 = 30
private const val DAYS_IN_MONTH_31 = 31

private const val NUM_4 = 4
private const val NUM_100 = 100
private const val NUM_400 = 400

private const val ZERO = 0

private const val COUNT_ACTION = 20
private const val COUNT_MONEY = 100

@Suppress("MagicNumber")
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
            return entries.find { it.number == month }
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

@Suppress("MagicNumber")
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
                0 -> SATURDAY
                1 -> SUNDAY
                2 -> MONDAY
                3 -> TUESDAY
                4 -> WEDNESDAY
                5 -> THURSDAY
                6 -> FRIDAY
                else -> throw IllegalArgumentException("Некорректный день недели")
            }
        }
    }
}

class DataClass(
    private var day: Int,
    private var month: MonthNameClass,
    private var year: Int,
) {

    fun getDay(): Int {
        return day
    }

    fun getMonth(): MonthNameClass {
        return month
    }

    fun getYear(): Int {
        return year
    }

    fun getMonthLast(): DataClass {
        val monthLast = if (month == JANUARY)
            DECEMBER
        else
            MonthNameClass.fromInt(month.number - 1)

        val yearLast = if (month == JANUARY)
            year - 1
        else
            year

        return DataClass(day, monthLast, yearLast)
    }

    fun getMonthNext(): DataClass {
        val monthNext = if (month == DECEMBER)
            JANUARY
        else
            MonthNameClass.fromInt(month.number + 1)

        val yearNext = if (month == DECEMBER)
            year + 1
        else
            year

        return DataClass(day, monthNext, yearNext)
    }

    fun dataMonthNext() {
        val nextDate = getMonthNext()
        this.day = nextDate.getDay()
        this.month = nextDate.getMonth()
        this.year = nextDate.getYear()
    }

    fun dataMonthLast() {
        val nextDate = getMonthLast()
        this.day = nextDate.getDay()
        this.month = nextDate.getMonth()
        this.year = nextDate.getYear()
    }
}

class Action(
    private var userName: String,
    private var actionName: String,
    private var actionType: Int,
    private var actionMoney: Int,
    private var actionCategory: String,
    private var data: DataClass
) {

    fun getMoney(): Int {
        return actionMoney
    }

    fun getUserName(): String {
        return userName
    }

    fun getActionName(): String {
        return actionName
    }

    fun getActionType(): Int {
        return actionType
    }
}

class DayClass(
    private val data: DataClass
) {
    private val dayOfWeek: DayWeekClass = DayWeekClass.fromInt(dayOfWeekInit(data))
    private val money = COUNT_MONEY
    private var actions: Array<Action?>

    private fun initActions(): Array<Action?> {
        val action = mutableListOf<Action>()
        for (i in ZERO..COUNT_ACTION)
            action.add(Action("user $i", "action $i", ZERO, (i + 1) * NUM_100, "категория", data))
        return action.toTypedArray()
    }

    init {
        actions = initActions()
    }

    companion object {
        @Suppress("MagicNumber")
        fun dayOfWeekInit(
            data: DataClass
        ): Int {
            val d = data.getDay()
            var m = data.getMonth().number
            var y = data.getYear()

            if (m < 3) {
                m += 12
                y--
            }

            val k = y % 100
            val j = y / 100

            return (d + (13 * (m + 1)) / 5 + k + (k / 4) + (j / 4) + (5 * j)) % 7
        }
    }

    fun getActions(): Array<Action?> {
        return actions
    }

    fun getDayData(): Int {
        return data.getDay()
    }

//    fun getData() : DataClass {
//        return data
//    }

    fun getDayMoney(): Int {
        return money
    }

    fun getDayOfWeek(): DayWeekClass {
        return dayOfWeek
    }
}

class MountClass(
    private var data: DataClass
) {
    private val countDays: Int
    private val days: Array<DayClass?>

    init {
        countDays = countDaysInit()
        days = daysInit()
    }

    private fun daysInit(): Array<DayClass?> {
        val days = Array<DayClass?>(countDays) { null }
        for (i in ZERO until countDays)
            days[i] = DayClass(DataClass(i + 1, data.getMonth(), data.getYear()))
        return days
    }

    private fun isLeapYear(): Boolean {
        return (data.getYear() % NUM_4 == ZERO && data.getYear() % NUM_100 != ZERO) ||
                (data.getYear() % NUM_400 == ZERO)
    }

    private fun countDaysInit(): Int {
        val monthsWith30Days = setOf(APRIL, JUNE, SEPTEMBER, NOVEMBER)
        if (data.getMonth() == FEBRUARY) {
            return if (isLeapYear())
                DAYS_IN_MONTH_29
            else
                DAYS_IN_MONTH_28
        }
        if (data.getMonth() in monthsWith30Days)
            return DAYS_IN_MONTH_30
        return DAYS_IN_MONTH_31
    }

    fun getAllDays(): Array<DayClass?> {
        return days
    }

    fun getFirstWeek(): Array<DayClass?> {
        val days = mutableListOf<DayClass>()
        for (day in this.days) {
            days.add(day!!)
            if (day.getDayOfWeek() == DayWeekClass.SUNDAY)
                break
        }
        return days.toTypedArray()
    }

    fun getLastWeek(): Array<DayClass?> {
        val days = mutableListOf<DayClass>()
        for (day in this.days.reversed()) {
            Log.d("Calendar", day?.getDayOfWeek()?.number.toString())
            days.add(day!!)
            if (day.getDayOfWeek() == DayWeekClass.MONDAY)
                break
        }
        return days.reversed().toTypedArray()
    }
}

class GridDatas(
    data: DataClass
) {
    private var days: Array<DayClass?>

    init {
        days = initDays(data)
    }

    private fun initDays(
        data: DataClass
    ): Array<DayClass?> {
        val days = mutableListOf<DayClass>()
        val dataLast = data.getMonthLast()
        val dataNext = data.getMonthNext()

        val mountLast = MountClass(dataLast)
        val mountThis = MountClass(data)
        val mountNext = MountClass(dataNext)

        days.addAll(mountLast.getLastWeek().filterNotNull())
        days.addAll(mountThis.getAllDays().filterNotNull())
        days.addAll(mountNext.getFirstWeek().filterNotNull())

        return days.toTypedArray()
    }

    fun newDays(
        data: DataClass
    ) {
        days = initDays(data)
    }

    fun getDays(): Array<DayClass?> {
        return days
    }
}

class CalendarClass {
    private var data: DataClass
    private var gridDatas: GridDatas

    init {
        val calendar = java.util.Calendar.getInstance()
        data = DataClass(
            calendar.get(java.util.Calendar.DAY_OF_MONTH),
            MonthNameClass.fromInt(calendar.get(java.util.Calendar.MONTH) + 1),
            calendar.get(java.util.Calendar.YEAR)
        )
        gridDatas = GridDatas(data)
    }

    fun getDays(): Array<DayClass?> {
        return gridDatas.getDays()
    }

    private fun getGrid(): GridDatas {
        return gridDatas
    }

    fun getData(): DataClass {
        return data
    }

    fun nextMount() {
        data.dataMonthNext()
        gridDatas.newDays(data)
    }

    fun lastMount() {
        data.dataMonthLast()
        gridDatas.newDays(data)
    }

    fun copy(
        calendar: CalendarClass
    ) {
        data = calendar.getData()
        gridDatas = calendar.getGrid()
    }
}
