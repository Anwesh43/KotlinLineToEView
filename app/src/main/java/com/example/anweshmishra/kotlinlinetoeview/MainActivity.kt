package com.example.anweshmishra.kotlinlinetoeview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.linetoeview.LineToEView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LineToEView.create(this)
    }
}
