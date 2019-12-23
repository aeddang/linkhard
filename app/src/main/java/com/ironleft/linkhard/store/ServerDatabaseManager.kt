package com.ironleft.linkhard.store

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class ServerDatabaseManager(ctx:Context): SQLiteOpenHelper(ctx, "Server.db", null, 1){

    private val TABLE = "Servers"

    init {
        writableDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS " + TABLE +
                    "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "path TEXT," +
                    "userID TEXT," +
                    "userPW TEXT);"
        )
    }

    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    data class Row(var path:String = "",var userID:String = "",var userPW:String = ""){
        var id:Int = -1
        var idx:Int = -1
        var title:String = ""
        var isLock:Boolean = true
        fun setupCompleted():Boolean{
            isLock = !(path == "" || userID == "" || userPW == "")
            return isLock
        }
    }


    fun insert(row: Row) {
        val addRowValue = ContentValues()
        addRowValue.put("title", row.title)
        addRowValue.put("path", row.path)
        addRowValue.put("userID", row.userID)
        addRowValue.put("userPW", row.userPW)
        writableDatabase.insert(TABLE, null, addRowValue)
        writableDatabase.close()
    }


    fun update(row: Row) {
        val updateRowValue = ContentValues()
        updateRowValue.put("title", row.title)
        updateRowValue.put("path", row.path)
        updateRowValue.put("userID", row.userID)
        updateRowValue.put("userPW", row.userPW)

        writableDatabase.update(TABLE, updateRowValue, "_id=?", arrayOf( row.id.toString()))
        writableDatabase.close()
    }

    fun delete(row: Row) {
        writableDatabase.delete( TABLE,"_id=?", arrayOf( row.id.toString()))
        writableDatabase.close()
    }
    fun getData(id:Int):Row? {
        val db = readableDatabase
        val cursor: Cursor? = db.query(TABLE, arrayOf("_id",  "title", "path", "userID", "userPW"), "_id=?", arrayOf( id.toString()), null, null, null)
        var currentData:Row? = null
        if (cursor != null) {
            cursor.moveToFirst()
            currentData = Row(cursor.getString(2), cursor.getString(2), cursor.getString(3))
            cursor.close()
        }
        db.close()
        return currentData
    }

    fun getDatas(): ArrayList<Row> {
        val list = ArrayList<Row>()
        val columns = arrayOf("_id", "title", "path", "userID", "userPW")
        val db = readableDatabase
        val cursor: Cursor? = db.query(TABLE, columns, null, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val currentData = Row(cursor.getString(2), cursor.getString(2), cursor.getString(3))
                currentData.id = cursor.getInt(0)
                currentData.title = cursor.getString(1)
                currentData.idx = list.size
                currentData.setupCompleted()
                list.add( currentData )
            }
        }
        cursor?.close()
        db.close()
        return list
    }

    /*
    private fun update(
        updateRowValue: ContentValues?,
        whereClause: String?,
        whereArgs: Array<String?>?
    ): Int {
        return writableDatabase.update(
            TABLE,
            updateRowValue,
            whereClause,
            whereArgs
        )
    }

    private fun delete(
        whereClause: String?,
        whereArgs: Array<String?>?
    ): Int {
        return writableDatabase.delete(
            TABLE,
            whereClause,
            whereArgs
        )
    }

    private fun query(
        colums: Array<String>,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        orderby: String?
    ): Cursor? {
        return readableDatabase.query(
            TABLE,
            colums,
            selection,
            selectionArgs,
            groupBy,
            having,
            orderby
        )
    }

    */

}