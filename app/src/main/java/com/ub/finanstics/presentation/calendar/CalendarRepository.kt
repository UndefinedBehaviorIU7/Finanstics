import android.os.Build
import androidx.annotation.RequiresApi
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.calendar.ActionDataClass
import com.ub.finanstics.presentation.calendar.DataClass
import com.ub.finanstics.presentation.calendar.MonthNameClass

class CalendarRepository(private var db: FinansticsDatabase) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getActionDays(data: DataClass): Array<ActionDataClass?> {
        val actions = actionDao.getActionsByDate(
            data.getDay(),
            data.getMonth().number,
            data.getYear()
        )
        var res = mutableListOf<ActionDataClass>()
        for (el in actions) {
            res.add(
                ActionDataClass(
                    userName = "user",
                    actionName = el.name,
                    actionType = el.type,
                    actionMoney = el.value,
                    actionCategory = categoryDao.getCategoryById(el.categoryId)!!.name,
                    data = DataClass(
                        el.date.dayOfMonth,
                        MonthNameClass.fromInt(el.date.monthValue),
                        el.date.year
                    ),
                    userId = 1,
                    description = el.description
                )
            )
        }
        return res.toTypedArray()
    }
}
