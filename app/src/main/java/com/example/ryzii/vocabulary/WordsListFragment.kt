package com.example.ryzii.vocabulary

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ryzii.vocabulary.data.Word
import kotlinx.android.synthetic.main.fragment_words_list.*
import org.jetbrains.anko.toast
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.DrawableCompat
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.example.ryzii.vocabulary.data.wordsController

class WordsListFragment : Fragment() {
    private val p = Paint()
    private lateinit var adapter: WordsListAdapter
    private var wordChangingId: String = "-1"
    private var wordChangingPosition: Int = -1
    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var edit_dialog_ru: EditText
    private lateinit var edit_dialog_en: EditText
    private lateinit var editDalogView: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_words_list, container, false)
    }

    @SuppressLint("InflateParams")
    private fun initDialog() {
        alertDialog = AlertDialog.Builder(activity)
        editDalogView = layoutInflater.inflate(R.layout.word_change_dialog, null)
        edit_dialog_ru = editDalogView.findViewById(R.id.edit_dialog_ru)
        edit_dialog_en = editDalogView.findViewById(R.id.edit_dialog_en)
        alertDialog.setView(editDalogView)
        alertDialog.setPositiveButton("Save") { dialog, _ ->
            val en = edit_dialog_en.text.toString()
            val ru = edit_dialog_ru.text.toString()
            adapter.changeItem(wordChangingPosition, ru, en)

            val values = ContentValues()
            values.put(Word.COLUMN_EN, en)
            values.put(Word.COLUMN_RU, ru)
            activity.wordsController.updateById(wordChangingId, values)
            dialog.dismiss()
        }
        alertDialog.setOnCancelListener {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getWordsList()
        initDialog()
        initRecyclerView()
        initSwipe()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        words_list_container.layoutManager = layoutManager
        adapter = WordsListAdapter(getWordsList(), { activity.toast(it.toString()) })
        words_list_container.adapter = adapter
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val itemId = viewHolder.itemView.tag.toString()

                if (direction == ItemTouchHelper.LEFT) {
                    try {
                        adapter.removeItem(position)
                        activity.wordsController.deleteById(itemId)
                    } catch (e: Exception) {
                        Log.d("error", e.toString())
                    }
                } else {
                    removeView()
                    try {
                        wordChangingId = itemId
                        wordChangingPosition = position
                        alertDialog.setTitle("Edit Name")
                        val word = activity.wordsController.getOneById(itemId)
                        edit_dialog_ru.setText(word.ru)
                        edit_dialog_en.setText(word.en)
                        alertDialog.show()
                    } catch (e: Exception) {
                        Log.d("error", e.toString())
                    }
                }
            }

            @SuppressLint("ObsoleteSdkInt")
            fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
                var drawable = ContextCompat.getDrawable(context, drawableId)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    drawable = DrawableCompat.wrap(drawable).mutate()
                }

                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                return bitmap
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                try {

                    val icon: Bitmap
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        if (dX > 0) {
                            p.color = Color.parseColor("#388E3C")
                            val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                            c.drawRect(background, p)
                            icon = getBitmapFromVectorDrawable(context, R.drawable.ic_edit_white)
                            val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                            c.drawBitmap(icon, null, iconDest, p)
                        } else {
                            p.color = Color.parseColor("#D32F2F")
                            val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                            c.drawRect(background, p)
                            icon = getBitmapFromVectorDrawable(context, R.drawable.ic_delete_white)
                            val iconDest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                            c.drawBitmap(icon, null, iconDest, p)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("DrawError", e.toString())
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(words_list_container)
    }

    private fun removeView() {
        if (editDalogView.parent != null) {
            (editDalogView.parent as ViewGroup).removeView(editDalogView)
        }
    }

    private fun getWordsList(): List<Word> {
        return try {
            activity.wordsController.getAllWords()
        } catch (e: Exception) {
            Log.d("getWordsListError", e.toString())
            emptyList()
        }
    }

    companion object {
        fun newInstance(): WordsListFragment {
            return WordsListFragment()
        }
    }
}
