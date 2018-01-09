package com.example.ryzii.vocabulary.data

data class Word(
        val id: Int,
        var ru: String,
        var en: String,
        val ru_to_en_count: Int,
        val en_to_ru_count: Int,
        val createdAt: Int,
        val lastTrainAt: Int
) {
    companion object {
        val _ID = "_id"
        val TABLE_NAME = "words"
        val COLUMN_RU = "ru"
        val COLUMN_EN = "en"
        val COLUMN_RU_TO_EN_COUNT = "ru_to_en_count"
        val COLUMN_EN_TO_RU_COUNT = "en_to_ru_count"
        val COLUMN_CREATED_AT = "created_at"
        val COLUMN_LAST_TRAIN_AT = "last_train_at"
    }
}