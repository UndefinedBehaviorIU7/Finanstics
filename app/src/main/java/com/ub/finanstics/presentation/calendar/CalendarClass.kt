package com.ub.finanstics.presentation.calendar

import CalendarGroupRepository
import CalendarRepository
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.calendar.MonthNameClass.APRIL
import com.ub.finanstics.presentation.calendar.MonthNameClass.DECEMBER
import com.ub.finanstics.presentation.calendar.MonthNameClass.FEBRUARY
import com.ub.finanstics.presentation.calendar.MonthNameClass.JANUARY
import com.ub.finanstics.presentation.calendar.MonthNameClass.JUNE
import com.ub.finanstics.presentation.calendar.MonthNameClass.NOVEMBER
import com.ub.finanstics.presentation.calendar.MonthNameClass.SEPTEMBER
import dataClassToApiString
import dataClassToLocalDate

enum class ErrorCalendar(val str: String) {
    ERRORSERVER("ошибка сервера"),
    OK("ok")
}

private const val DAYS_IN_MONTH_28 = 28
private const val DAYS_IN_MONTH_29 = 29
private const val DAYS_IN_MONTH_30 = 30
private const val DAYS_IN_MONTH_31 = 31

private const val NUM_4 = 4
private const val NUM_100 = 100
private const val NUM_400 = 400

private const val ZERO = 0

private const val COUNT_ACTION = 20
private const val COUNT_MONEY = 0

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

data class DataClass(
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

    companion object {
        fun getDataByString(
            str: String
        ): DataClass {
            val el = str.split("\\.".toRegex())
            return DataClass(el[0].toInt(), MonthNameClass.fromInt(el[1].toInt()), el[2].toInt())
        }
    }

    fun getMonthLast(): DataClass {
        val (monthLast, yearLast) = if (month == JANUARY) {
            DECEMBER to (year - 1)
        } else {
            MonthNameClass.fromInt(month.number - 1) to year
        }
        return DataClass(day, monthLast, yearLast)
    }

    fun getMonthNext(): DataClass {
        val (monthNext, yearNext) = if (month == DECEMBER) {
            JANUARY to (year + 1)
        } else {
            MonthNameClass.fromInt(month.number + 1) to year
        }
        return DataClass(day, monthNext, yearNext)
    }

    fun dataMonthNext() {
        val nextDate = getMonthNext()
        this.day = nextDate.getDay()
        this.month = nextDate.getMonth()
        this.year = nextDate.getYear()
    }

    fun dataMonthLast() {
        val lastDate = getMonthLast()
        this.day = lastDate.getDay()
        this.month = lastDate.getMonth()
        this.year = lastDate.getYear()
    }

    operator fun compareTo(other: DataClass): Int {
        return when {
            this.year != other.year -> this.year - other.year
            this.month.number != other.month.number -> this.month.number - other.month.number
            else -> this.day - other.day
        }
    }
}

data class ActionDataClass(
    private var userName: String,
    private var userId: Int,
    private var actionName: String,
    private var actionType: Int,
    private var actionMoney: Int,
    private var actionCategory: String,
    private var data: DataClass,
    private var description: String?
) {

    fun getMoney(): Int {
        return actionMoney
    }

    fun getUserName(): String {
        return userName
    }

    fun getUserId(): Int {
        return userId
    }

    fun getActionName(): String {
        return actionName
    }

    fun getActionType(): Int {
        return actionType
    }

    fun getActionCategory(): String {
        return actionCategory
    }

    fun getActionAPI(): Action {
        return Action(
            id = 1,
            userId = userId,
            type = actionType,
            name = actionName,
            value = actionMoney,
            date = dataClassToApiString(data),
            category_id = 1,
            description = description,
            created_at = "ub",
            groups = emptyList()
        )
    }

    fun getActionBD(): com.ub.finanstics.db.Action {
        return com.ub.finanstics.db.Action(
            actionId = 1,
            type = actionType,
            name = actionName,
            value = actionMoney,
            date = dataClassToLocalDate(data),
            categoryId = 1,
            description = description,
            createdAt = null,
            serverId = null
        )
    }
}

data class DayClass(
    private val data: DataClass
) {
    private val dayOfWeek: DayWeekClass = DayWeekClass.fromInt(dayOfWeekInit(data))
    private var money = COUNT_MONEY
    private var actionDataClasses: Array<ActionDataClass?> = arrayOfNulls(0)

    fun initActions(actions: Array<ActionDataClass?>) {
        actionDataClasses = actions
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

    fun getActions(): Array<ActionDataClass?> {
        return actionDataClasses!!
    }

    fun getDayData(): Int {
        return data.getDay()
    }

    fun getData(): DataClass {
        return data
    }

    fun getDayMoney(): Int {
        return money
    }

    fun getDayOfWeek(): DayWeekClass {
        return dayOfWeek
    }

    fun getDayMonth(): MonthNameClass {
        return data.getMonth()
    }

    fun updateMoney() {
        var res = 0
        for (el in actionDataClasses) {
            if (el != null) {
                if (el.getActionType() == 0) {
                    res -= el.getMoney()
                } else {
                    res += el.getMoney()
                }
            }
        }
        money = res
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
        val isLeap = data.getYear() % NUM_4 == ZERO && data.getYear() % NUM_100 != ZERO
        return (isLeap || data.getYear() % NUM_400 == ZERO)
    }

    private fun countDaysInit(): Int {
        val monthsWith30Days = setOf(APRIL, JUNE, SEPTEMBER, NOVEMBER)
        val month = data.getMonth()

        return when (month) {
            FEBRUARY -> {
                if (isLeapYear()) DAYS_IN_MONTH_29 else DAYS_IN_MONTH_28
            }
            in monthsWith30Days -> {
                DAYS_IN_MONTH_30
            }
            else -> {
                DAYS_IN_MONTH_31
            }
        }
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

    fun newDays(data: DataClass) {
        days = initDays(data)
    }

    fun getDays(): Array<DayClass?> {
        return days
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initActions(application: Application) {
        val db = FinansticsDatabase.getDatabase(application)
        val repository = CalendarRepository(db)
        for (el in days) {
            Log.d("elel", "")
            el?.initActions(repository.getActionDays(el.getData()))
            el?.updateMoney()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initActionsByApi(
        application: Application,
        groupId: Int
    ): ErrorCalendar {
        val db = FinansticsDatabase.getDatabase(application)
        val repository = CalendarGroupRepository(db)

        val dataFirst = days[0]!!.getData()
        val dataSecond = days.last()!!.getData()

        val actionsMap = repository.getGroupActionByDataMonth(
            groupId,
            dataFirst,
            dataSecond)

        if (actionsMap == null)
            return ErrorCalendar.ERRORSERVER

        for (day in days) {
            val date = day!!.getData()
            val actionsForDay = actionsMap[date]
            if (actionsForDay != null) {
                day.initActions(actionsForDay)
                day.updateMoney()
            }
        }
        return ErrorCalendar.OK
    }

    fun getDayByData(data: DataClass): DayClass? {
        for (el in days)
            if (el!!.getData() == data)
                return el
        return null
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

    companion object {
        private fun getNowData(): DataClass {
            val calendar = java.util.Calendar.getInstance()
            return DataClass(
                calendar.get(java.util.Calendar.DAY_OF_MONTH),
                MonthNameClass.fromInt(calendar.get(java.util.Calendar.MONTH) + 1),
                calendar.get(java.util.Calendar.YEAR)
            )
        }

        fun getNowDay(): DayClass {
            return DayClass(getNowData())
        }
    }

    fun getNowDataClass(): DayClass {
        return gridDatas.getDayByData(getNowData())!!
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

    fun nextMonth() {
        data.dataMonthNext()
        gridDatas.newDays(data)
    }

    fun lastMonth() {
        data.dataMonthLast()
        gridDatas.newDays(data)
    }

    fun copy(calendar: CalendarClass) {
        data = calendar.getData()
        gridDatas = calendar.getGrid()
    }

    fun deepCopy(): CalendarClass {
        val newInstance = CalendarClass()
        newInstance.data = this.data
        newInstance.gridDatas = this.gridDatas
        return newInstance
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initActionsDay(application: Application) {
        gridDatas.initActions(application)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initActionsDayByApi(
        application: Application,
        groupId: Int
    ): ErrorCalendar {
        return gridDatas.initActionsByApi(application, groupId)
    }
}
