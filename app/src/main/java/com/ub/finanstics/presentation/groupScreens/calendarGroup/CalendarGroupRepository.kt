package com.ub.finanstics.presentation.groupScreens.calendarGroup

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.presentation.userScreens.calendar.ActionDataClass
import com.ub.finanstics.presentation.userScreens.calendar.DataClass
import com.ub.finanstics.presentation.userScreens.calendar.MonthNameClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("TooManyFunctions")

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

fun dataClassToLocalDate(data: DataClass): LocalDate {
    return LocalDate.of(
        data.getYear(),
        data.getMonth().number,
        data.getDay()
    )
}


class CalendarGroupRepository {
    private fun getCategoryById(
        id: Int,
        categories: Array<Category>?
    ): Category? {
        if (categories != null)
            for (el in categories) {
                if (el.id == id) {
                    return el
                }
            }
        return null
    }


    @Suppress("ReturnCount", "TooGenericExceptionCaught")
    private suspend fun getCategoriesById(
        groupId: Int
    ): Array<Category>? {
        val apiRep = ApiRepository()
        try {
            val response = apiRep.getGroupCategories(groupId)
            if (!response.isSuccessful) return null

            val categories = response.body() ?: return null

            return categories.toTypedArray()
        } catch (_: Exception) {
            return null
        }
    }

    @Suppress("TooGenericExceptionCaught")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getArrayDataClass(
        actions: Action,
        categories: Array<Category>?
    ): ActionDataClass {
        val res: ActionDataClass?
        val category: Category? = getCategoryById(actions.categoryId, categories)
        res = ActionDataClass(
            actionName = actions.name,
            actionType = actions.type,
            actionMoney = actions.value,
            actionCategory = category?.name ?: actions.categoryId.toString(),
            data = dataApiToDataClass(actions.date),
            userId = actions.userId,
            description = actions.description
        )
        return res
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getUserName(
        userId: Int
    ): String? {
        var res: String? = null
        try {
            val apiRep = ApiRepository()
            val response = apiRep.getUser(userId)
            if (response.isSuccessful) {
                res = response.body()?.username
            }
        } catch (_: Exception) {
        }
        return res
    }

    @Suppress("NestedBlockDepth", "TooGenericExceptionCaught")
    suspend fun userImage(userId: Int): Bitmap? = withContext(Dispatchers.IO) {
        val api = ApiRepository()
        try {
            val response = api.getUserImage(userId)
            if (response.isSuccessful) {
                response.body()?.byteStream().use { stream ->
                    if (stream != null) {
                        BitmapFactory.decodeStream(stream)
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

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
                    list.add(actionsData)
                }
            }
        } catch (_: Exception) {
            return null
        }
        return tempMap.mapValues { it.value.toTypedArray() }
    }
}
