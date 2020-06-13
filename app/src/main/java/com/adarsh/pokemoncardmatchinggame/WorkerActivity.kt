package com.adarsh.pokemoncardmatchinggame

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList
import com.adarsh.pokemoncardmatchinggame.R.drawable.*
import kotlinx.android.synthetic.main.activity_worker.*
import kotlinx.android.synthetic.main.win_layout.*
import kotlinx.coroutines.*

val buttons:MutableList<MutableList<Button>> = ArrayList() //Contains grid with all buttons
val N = 2  //passed on Level
var images: MutableList<Int> = mutableListOf() //Images for a particular grid
var imageNo: MutableList<Int> = mutableListOf() //Overall Images for whole game


var ItemClicked =0  //Number of cards clicked till the moment
var lastItem=0      //Index of Last Item clicked (used to check images being equal)
var lastItemi=0     //Index of Last Item's row number
var lastItemj=0     //Index of Last Item's column number
var ans= 0      //counting number of ans found

class WorkerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker)

        //Array containing all images for the game, available.
         imageNo = mutableListOf(baltoise, bhoot, bulba, charizard, charmander, dyno, gengar, inferno,
            lady, pigeon, pikachu, squirtle, umbreo, articuno, chikorita, dragonite, electri, evee, firy,
         lapras, motlres, zapdos)
        imageNo.shuffle()


        //Initial SetUp
        createGrid()

        //Creating image array for the game
        createImageArray()

        //changing views
        viewChange()
    }

    private fun createImageArray() {
        val x = N*N
        var y =0
        for(i in 0 until x){
            if(y < x/2){
                images.add(imageNo[y])
                y++
            }
            else images.add(imageNo[i-y])
        }
        images.shuffle()
    }


    private fun viewChange() {

        for(i in 0 until N) {
            for(j in 0 until N){
                buttons[i][j].setOnClickListener {

                    if(buttons[i][j].text == "off" && ItemClicked<2 ){
                        buttons[i][j].setBackgroundResource(images[(j)+(i*N)])
                        buttons[i][j].text = "on"
                        if(ItemClicked == 0)
                            {
                                lastItem = j+(i*N)
                                lastItemi = i
                                lastItemj = j
                            }
                        ItemClicked++
                    }else if(buttons[i][j].text == "on"){
                        buttons[i][j].setBackgroundResource(bush)
                        buttons[i][j].text = "off"
                        ItemClicked--
                    }

                    if(ItemClicked == 2) {
                        if (images[(j) + (i * N)] == images[lastItem]) {

                            CoroutineScope(Dispatchers.Main).launch {
                                buttons[i][j].isClickable = false
                                buttons[lastItemi][lastItemj].isClickable = false
                                ItemClicked = 0
                                ++ans
                                found_count.text = "$ans"

                                val objectAnimator = ObjectAnimator.ofObject(
                                    layout,
                                    "backgroundColor",
                                    ArgbEvaluator(),
                                    Color.parseColor("#FFFFFF"),
                                    Color.parseColor("#000000")
                                )
                                objectAnimator.repeatCount = 1
                                objectAnimator.repeatMode = ValueAnimator.REVERSE
                                objectAnimator.duration = 500
                                objectAnimator.start()
                                }
                            if(ans+1 == (N*N)/2) {
                                winAfterEffects()
                                Log.d("TAg", "$ans ,"+ ((N*N)/2))
                            }
                        }
                        else {
                            GlobalScope.launch(Dispatchers.Main) {
                                delay(500)
                                buttons[i][j].setBackgroundResource(bush)
                                buttons[i][j].text = "off"
                                buttons[lastItemi][lastItemj].setBackgroundResource(bush)
                                buttons[lastItemi][lastItemj].text = "off"
                                ItemClicked = 0
                            }
                        }
                    }
                }
            }
        }
    }

    private fun winAfterEffects() {
        var mDialog = Dialog(this@WorkerActivity)
        mDialog.setContentView(R.layout.win_layout)
        val window = mDialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setCanceledOnTouchOutside(false) // prevent dialog box from getting dismissed on outside touch
        //mDialog.setCancelable(false)  //prevent dialog box from getting dismissed on back key pressed
        mDialog.show()

        mDialog.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {   }
            override fun onAnimationEnd(p0: Animator?) {
                mDialog.home.visibility = View.VISIBLE
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
        })
        mDialog.home.setOnClickListener {
            startActivity(Intent(this@WorkerActivity, MainActivity::class.java))
        }

    }


    private fun createGrid() {
        //Creating Layout for Buttons.
        val layout = findViewById<LinearLayout>(R.id.mainLayout)
        layout.orientation =
            LinearLayout.VERTICAL //Can also be done in xml by android:orientation="vertical"


        for (i in 0 until N) {
            val row = LinearLayout(this)
            row.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f
            )
            val buttonRow: MutableList<Button> = ArrayList()
            for (j in 0 until N) {
                val btnTag = Button(this)
                btnTag.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
                btnTag.setBackgroundResource(bush)
                btnTag.text = "off"
                row.addView(btnTag)
                buttonRow.add(btnTag)
            }
            buttons.add(buttonRow)
            layout.addView(row)
        }
    }
}