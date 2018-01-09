package com.example.ryzii.vocabulary

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import co.metalab.asyncawait.async
import com.beust.klaxon.Klaxon
import com.example.ryzii.vocabulary.data.Word
import com.example.ryzii.vocabulary.data.wordsController
import kotlinx.android.synthetic.main.fragment_add_word.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import java.net.URL


class AddWordFragment : Fragment() {
    private var translateLang = "en"
    private val TRANSLATION_API_KEY: String = "trnsl.1.1.20180105T200708Z.02f36036a633e1da.aeabe16649e00fc61a961627e476e1c9cc82effa"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_add_word, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        try {
            text_to_translate.addTextChangedListener(object : TextWatcher {
                var handler = Handler(Looper.getMainLooper())
                var workRunnable: Runnable = Runnable({})

                override fun afterTextChanged(s: Editable) {
                    clearTranslation()
                    handler.removeCallbacks(workRunnable)
                    workRunnable = Runnable { getTranslationFromServer(s.toString(), translateLang) }
                    handler.postDelayed(workRunnable, 1000)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
            save_word_button.setOnClickListener { saveWord() }
        } catch (e: Exception) {
            Log.d("getTranslateError", e.toString())
        }
        translation_direction_container.setOnClickListener { changeTranslateLang() }
        clear_text_to_translate.setOnClickListener { clearTextToTranslate() }
        super.onActivityCreated(savedInstanceState)
    }

    private fun changeTranslateLang() {
        if (translateLang == "ru") {
            translateLang = "en"
            translate_from.text = resources.getString(R.string.russian)
            translate_to.text = resources.getString(R.string.english)
            text_to_translate.hint = resources.getString(R.string.text_to_translate_hint_ru)
        } else {
            translateLang = "ru"
            text_to_translate.hint = resources.getString(R.string.text_to_translate_hint_en)
            translate_from.text = resources.getString(R.string.english)
            translate_to.text = resources.getString(R.string.russian)
        }
        clearTextToTranslate()
        clearTranslation()
    }

    private fun saveWord() {
        val values = ContentValues()
        values.put(Word.COLUMN_RU, text_to_translate.text.toString())
        values.put(Word.COLUMN_EN, translation.text.toString())
        values.put(Word.COLUMN_CREATED_AT, 0)
        activity.wordsController.addOne(values)
        clearTextToTranslate()
        clearTranslation()
        activity.toast("Успешно сохранено")
    }

    private fun clearTextToTranslate() {
        text_to_translate.setText("")
    }

    private fun clearTranslation() {
        translation.setText("")
    }

    private fun getTranslationFromServer(text: String, lang: String) {
        if (text.isEmpty()) return

        async {
            try {
                val response = await {
                    URL("""https://translate.yandex.net/api/v1.5/tr.json/translate?key=$TRANSLATION_API_KEY&text=$text&lang=$lang""").readText()
                }
                val translationResponse = Klaxon().parse<ServerTranslation>(response)
                setTranslation(translationResponse!!.texts[0])
            } catch (e: Exception) {
                activity.longToast("getTranslationError: $e")
            }
        }
    }

    private fun setTranslation(text: String) {
        translation.text = text
    }

    companion object {
        fun newInstance(): AddWordFragment {
            return AddWordFragment()
        }
    }
}
