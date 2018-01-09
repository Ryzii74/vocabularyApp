package com.example.ryzii.vocabulary.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class WordsDBHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "words.db", null, 4) {
    companion object {
        private var instance: WordsDBHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): WordsDBHelper {
            if (instance == null) {
                instance = WordsDBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(Word.TABLE_NAME, true,
                Word._ID to INTEGER + PRIMARY_KEY + UNIQUE,
                Word.COLUMN_RU to TEXT,
                Word.COLUMN_EN to TEXT,
                Word.COLUMN_RU_TO_EN_COUNT to INTEGER + DEFAULT("0"),
                Word.COLUMN_EN_TO_RU_COUNT to INTEGER + DEFAULT("0"),
                Word.COLUMN_CREATED_AT to INTEGER,
                Word.COLUMN_LAST_TRAIN_AT to INTEGER + DEFAULT("0"))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val values = ContentValues()
        values.put(Word.COLUMN_EN_TO_RU_COUNT, 0)
        values.put(Word.COLUMN_RU_TO_EN_COUNT, 0)
        db.update(Word.TABLE_NAME, values, null, null)
    }

}


val Context.database: WordsDBHelper
get() = WordsDBHelper.getInstance(applicationContext)