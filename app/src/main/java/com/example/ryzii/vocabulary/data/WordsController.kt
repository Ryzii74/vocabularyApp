package com.example.ryzii.vocabulary.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import org.jetbrains.anko.db.*

class WordsController(private val db: WordsDBHelper) {
    fun getAllWords(): List<Word> {
        return db.use {
            select(Word.TABLE_NAME)
                    .exec {
                        parseList(classParser())
                    }
        }
    }

    companion object {
        private var instance: WordsController? = null

        @Synchronized
        fun getInstance(ctx: Context): WordsController {
            if (instance == null) {
                instance = WordsController(ctx.database)
            }
            return instance!!
        }
    }

    fun deleteById(id: String) {
        db.use {
            delete(Word.TABLE_NAME, "${Word._ID} = $id", null)
        }
    }

    fun updateById(wordChangingId: String, values: ContentValues) {
        db.use {
            update(Word.TABLE_NAME, values, "${Word._ID} = $wordChangingId", null)
        }
    }

    fun getOneById(itemId: String): Word {
        val words = db.use {
            select(Word.TABLE_NAME)
                .whereArgs("${Word._ID} = $itemId")
                .exec {
                    parseList<Word>(classParser())
                }
        }
        return words[0]
    }

    fun addOne(values: ContentValues) {
        db.use {
            insert(Word.TABLE_NAME, null, values)
        }
    }

    fun getOneToTrain(trainType: Int): Word {
        val fieldToSort = when (trainType) {
            0 -> Word.COLUMN_RU_TO_EN_COUNT
            1 -> Word.COLUMN_EN_TO_RU_COUNT
            else -> Word.COLUMN_RU_TO_EN_COUNT
        }
        val words = db.use {
            select(Word.TABLE_NAME)
                .whereArgs("$fieldToSort < 5")
                .orderBy(fieldToSort, SqlOrderDirection.ASC)
                .orderBy(Word.COLUMN_LAST_TRAIN_AT, SqlOrderDirection.ASC)
                .limit(1)
                .exec {
                    parseList<Word>(classParser())
                }
        }
        return words[0]
    }

    fun setTrainResult(word: Word, isRemembered: Boolean, trainType: Int) {
        try {
            val values = ContentValues()
            values.put(Word.COLUMN_LAST_TRAIN_AT, System.currentTimeMillis() / 1000L)
            val value = when (trainType) {
                0 -> Word.COLUMN_RU_TO_EN_COUNT to word.ru_to_en_count + 1
                1 -> Word.COLUMN_EN_TO_RU_COUNT to word.en_to_ru_count + 1
                else -> throw IllegalArgumentException("trainType bad value")
            }
            if (isRemembered) {
                values.put(value.first, value.second)
            } else {
                values.put(value.first, 0)
            }

            db.use {
                update(Word.TABLE_NAME, values, "${Word._ID} = ${word.id}", null)
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

}

val Context.wordsController: WordsController
    get() = WordsController.getInstance(applicationContext)