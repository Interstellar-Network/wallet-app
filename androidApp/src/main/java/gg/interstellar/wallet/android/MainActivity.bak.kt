package gg.interstellar.wallet.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import gg.interstellar.wallet.Greeting
import android.widget.TextView

fun greet(): String {
    return Greeting().greeting()
}

class MainActivityBak : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_bak)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()
    }
}
