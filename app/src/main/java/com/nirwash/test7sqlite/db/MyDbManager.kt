package com.nirwash.test7sqlite.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManager(val context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    fun insertToDb(title: String, content: String, uri: String, time: String) {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }

    fun editItemDb(title: String, content: String, uri: String, id: Int, time: String) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)

        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id: String) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }


    suspend fun readDbData(searchText: String): ArrayList<ListItem> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataTitle =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataUri =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_IMAGE_URI))
            val dataTime =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TIME))
            val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val item = ListItem()
            item.title = dataTitle
            item.desc = dataContent
            item.uri = dataUri
            item.id = dataId
            item.time = dataTime
            dataList.add(item)
        }
        cursor.close()
        return@withContext dataList
    }

    fun closeDb() {
        myDbHelper.close()
    }
}