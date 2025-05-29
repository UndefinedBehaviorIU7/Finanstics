package com.ub.finanstics.presentation.userScreens.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import com.ub.finanstics.db.FinansticsDatabase

class CalendarRepository(db: FinansticsDatabase) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getActionDays(data: DataClass): Array<ActionDataClass?> {
        val actions = actionDao.getActionsByDate(
            data.getDay(),
            data.getMonth().number,
            data.getYear()
        )
        val res = mutableListOf<ActionDataClass>()
        for (action in actions) {
            res.add(
                ActionDataClass(
                    actionName = action.name,
                    actionType = action.type,
                    actionMoney = action.value,
                    actionCategory = categoryDao.getCategoryById(action.categoryId)!!.name,
                    data = DataClass(
                        action.date.dayOfMonth,
                        MonthNameClass.fromInt(action.date.monthValue),
                        action.date.year
                    ),
                    userId = 1,
                    description = action.description
                )
            )
        }
        return res.toTypedArray()
    }
}
