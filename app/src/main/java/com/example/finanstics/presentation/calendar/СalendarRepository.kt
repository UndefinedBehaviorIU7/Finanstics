import android.os.Build
import androidx.annotation.RequiresApi
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.calendar.ActionDataClass
import com.example.finanstics.presentation.calendar.DataClass
import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.presentation.calendar.MountClass


class CalendarRepository(private var db: FinansticsDatabase) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getActionDays(data: DataClass): Array<ActionDataClass?> {
        val actions = actionDao.getActionsByDate(data.getDay(), data.getMonth().number, data.getYear())
        var res = mutableListOf<ActionDataClass>()
        for (el in actions) {
            res.add(ActionDataClass(
                userName = "user",
                actionName = el.name,
                actionType = el.type,
                actionMoney = el.value,
                actionCategory = categoryDao.getCategoryById(el.categoryId)!!.name,
                data = DataClass(el.date.dayOfMonth, MonthNameClass.fromInt(el.date.monthValue), el.date.year)
            ))
        }
        return res.toTypedArray()
    }
}