import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.finanstics.api.ApiRepository
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.calendar.ActionDataClass
import com.example.finanstics.presentation.calendar.DataClass
import com.example.finanstics.presentation.calendar.MonthNameClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun dataApiToDataClass(
    dataApi: String
): DataClass {
    val dataLocal = LocalDate.parse(dataApi, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return DataClass(
        dataLocal.dayOfMonth,
        MonthNameClass.fromInt(dataLocal.monthValue),
        dataLocal.year
    )
}

suspend fun getUserName(
    userId: Int
): String? {
    var res: String? = null
    try {
        val apiRep = ApiRepository()
        val response = apiRep.getUser(userId)
        if (!response.isSuccessful) {
            Log.e("getUserName", "not isSuccessful")
        } else {
            res = response.body()?.username
        }
    } catch (e: Exception) {
        Log.e("getUserName", e.toString())
    }
    return res
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getArrayActionDataClass(
    actions: List<com.example.finanstics.api.models.Action>
): Array<ActionDataClass?> {
    val res = mutableListOf<ActionDataClass>()
    for (el in actions) {
        val userName = getUserName(el.userId)
        Log.d("getArrayActionDataClass", "for")
        if (userName != null) {
            Log.d("getArrayActionDataClass", el.name)
            res.add(
                ActionDataClass(
                userName = userName,
                actionName = el.name,
                actionType = el.type,
                actionMoney = el.value,
                actionCategory = "cat",
                data = dataApiToDataClass(el.date)
            )
            )
        }
    }

    return res.toTypedArray()
}

class CalendarGroupRepository(private var db: FinansticsDatabase) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGroupActionDays(groupId: Int, data: DataClass): Array<ActionDataClass?>? {
        var res: Array<ActionDataClass?>? = null
        val apiRep = ApiRepository()
        try {
            val response = apiRep.getGroupActionsByDate(
                groupId,
                data.getYear(),
                data.getMonth().number,
                data.getDay()
            )

            if (!response.isSuccessful) {
                res = null
            } else {
                val actions = response.body()

                if (actions != null) {
                    res = getArrayActionDataClass(actions)
                }
            }
        } catch (e: Exception) {
            Log.e("getGroupActionDays ERROR", e.toString())
        }
        return res
    }
}
