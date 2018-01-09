package com.example.ryzii.vocabulary

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.beust.klaxon.Json
import com.example.ryzii.vocabulary.data.database
import android.support.v4.app.Fragment
import android.util.Log
import com.example.ryzii.vocabulary.data.WordsController

// TODO: выученные слова - помечать те, которые больше 5 раз повторены
// TODO: число 5 выше убрать в настройки
// TODO: показывать отдельно выученные и невыученные слова в списке
// TODO: в окне изменения сделать галочку "Выученное"
// TODO: индикатор выученности слова
// TODO: анимация в окне тренировки
// TODO: добавлять к словам тэг
// TODO: отображение слов по тэгу
// TODO: изучение слов по тэгу
// TODO: прикрутить API Yandex-словаря
// TODO: перенести кнопку сохранить в верхнюю часть экрана
// TODO: если слов в списке нет - сделать заглушку
// TODO: если слов для тренировки нет - сделать заглушку
// TODO: добавить ачивки в приложение на количество слов
// TODO: добавить напоминалку PUSH-уведомлениями
// TODO: добавить в настройки управление напоминанием
// TODO: сделать виджет на главный экран для тренировки
// TODO: share word to other applications
// TODO: сделать сохранение слов в приложение из других приложений

// НЕ В ПРИЛОЖЕНИИ
// TODO: сделать синхронизацию слов с сервером
// TODO: сделать дополнение в браузер для добавления слов


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var wordsController: WordsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        wordsController = WordsController(database)
        this.wordsController = wordsController

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val defaultFragment = AddWordFragment.newInstance();
        supportFragmentManager.beginTransaction().replace(R.id.layout_container, defaultFragment).commit()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancelAll()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        try {
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_add -> AddWordFragment.newInstance()
                R.id.nav_words_list -> WordsListFragment.newInstance()
                R.id.nav_settings -> AddWordFragment.newInstance()
                R.id.nav_train -> TrainFragment.newInstance()
                else -> AddWordFragment.newInstance()
            }
            supportFragmentManager.beginTransaction().replace(R.id.layout_container, fragment).commit()
        } catch (e: Exception) {
            Log.d("createFragmentError", e.toString())
        }

        item.isChecked = true
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

data class ServerTranslation(
    @Json(name = "text")
    var texts: List<String> = arrayListOf()
)
