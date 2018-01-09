package com.example.ryzii.vocabulary

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.ryzii.vocabulary.data.Word
import com.example.ryzii.vocabulary.data.wordsController
import kotlinx.android.synthetic.main.fragment_train.*

class TrainFragment : Fragment() {
    private lateinit var currentWord: Word
    private var currentTrainType: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_train, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = ArrayAdapter.createFromResource(activity, R.array.train_types, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        train_type.adapter = adapter
        remember_yes.setOnClickListener { setRememberWord(true) }
        remember_no.setOnClickListener { setRememberWord(false) }
        train_task_container.setOnClickListener { train_translation.visibility = View.VISIBLE }
        train_type.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                currentTrainType = position
                updateWordToTrain()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                currentTrainType = 0
                updateWordToTrain()
            }
        }
    }

    private fun updateWordToTrain() {
        try {
            train_translation.visibility = View.INVISIBLE
            currentWord = activity.wordsController.getOneToTrain(currentTrainType)
            train_task_container.visibility = View.VISIBLE
            no_words_to_train_hint.visibility = View.GONE
            if (currentTrainType == 0) {
                train_task_to_translate.text = currentWord.ru
                train_translation.text = currentWord.en
            } else {
                train_task_to_translate.text = currentWord.en
                train_translation.text = currentWord.ru
            }
        } catch (e: Exception) {
            train_task_container.visibility = View.GONE
            no_words_to_train_hint.visibility = View.VISIBLE
        }
    }

    private fun setRememberWord(isRemember: Boolean) {
        activity.wordsController.setTrainResult(currentWord, isRemember, currentTrainType)
        updateWordToTrain()
    }

    companion object {
        fun newInstance(): TrainFragment {
            return TrainFragment()
        }
    }
}
