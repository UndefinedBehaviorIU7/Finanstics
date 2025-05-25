import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.calendar.ActionDataClass
import com.ub.finanstics.presentation.calendar.DataClass
import com.ub.finanstics.presentation.calendar.MonthNameClass
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

fun dataClassToApiString(data: DataClass): String {
    val localDate = LocalDate.of(
        data.getYear(),
        data.getMonth().number,
        data.getDay()
    )
    return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

@Suppress("TooGenericExceptionCaught")
suspend fun getUserName(
    userId: Int
): String? {
    var res: String? = null
    Log.d("getUserNameuserId", userId.toString())
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

@Suppress("ReturnCount", "TooGenericExceptionCaught")
suspend fun getCategoriesById(
    groupId: Int
): Array<Category>? {
    val apiRep = ApiRepository()
    try {
        val response = apiRep.getGroupCategories(groupId)
        if (!response.isSuccessful) return null

        val categories = response.body() ?: return null
        return categories.toTypedArray()
    } catch (e: Exception) {
        Log.e("getGroupActionDays ERROR", e.toString())
        return null
    }
}

@Suppress("TooGenericExceptionCaught")
@RequiresApi(Build.VERSION_CODES.O)
suspend fun getArrayDataClass(
    actions: com.ub.finanstics.api.models.Action,
    categories: Array<Category>?
): ActionDataClass? {
//    val userName = getUserName(actions.userId)
    var res: ActionDataClass? = null
    Log.d("getArrayActionDataClassid", actions.userId.toString())

    res = ActionDataClass(
            userName = "skip skipish",
            actionName = actions.name,
            actionType = actions.type,
            actionMoney = actions.value,
            actionCategory = if (categories != null)
                categories[actions.category_id].name
            else actions.category_id.toString(),
            data = dataApiToDataClass(actions.date),
            userId = actions.userId,
            description = actions.description
        )


    return res
}

class CalendarGroupRepository(private var db: FinansticsDatabase) {
//    @Suppress("TooGenericExceptionCaught")
//    @RequiresApi(Build.VERSION_CODES.O)
//    suspend fun getGroupActionDays(groupId: Int, data: DataClass): Array<ActionDataClass?>? {
//        var res: Array<ActionDataClass?>? = null
//        val apiRep = ApiRepository()
//        try {
//            val response = apiRep.getGroupActionsByDate(
//                groupId,
//                data.getYear(),
//                data.getMonth().number,
//                data.getDay()
//            )
//
//            if (!response.isSuccessful) {
//                res = null
//            } else {
//                val actions = response.body()
//
//                if (actions != null) {
//                    res = getArrayActionDataClass(actions)
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("getGroupActionDays ERROR", e.toString())
//        }
//        return res
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress(
        "MagicNumber",
        "LongParameterList",
        "LongMethod",
        "ComplexMethod",
        "TooGenericExceptionCaught",
        "NestedBlockDepth",
        "ReturnCount"
    )
    suspend fun getGroupActionByDataMonth(
        groupId: Int,
        dataFirst: DataClass,
        dataSecond: DataClass
    ): Map<DataClass, Array<ActionDataClass?>?>? {
        val tempMap = mutableMapOf<DataClass, MutableList<ActionDataClass?>>()

        val apiRep = ApiRepository()
        try {
            val response = apiRep.getGroupActions(groupId)
            if (!response.isSuccessful) return null

            val actions = response.body() ?: return null

            val categories = getCategoriesById(groupId)

            for (el in actions) {
                val date = dataApiToDataClass(el.date)
                if (dataFirst <= date && date <= dataSecond) {
                    val list = tempMap.getOrPut(date) { mutableListOf() }
                    val actionsData = getArrayDataClass(el, categories)
                    Log.d("dataApiToDataClass", (actionsData == null).toString())
                    if (actionsData != null)
                        list.add(actionsData)
                }
            }
        } catch (e: Exception) {
            Log.e("getGroupActionDays ERROR", e.toString())
            return null
        }
        return tempMap.mapValues { it.value.toTypedArray() }
    }
}
