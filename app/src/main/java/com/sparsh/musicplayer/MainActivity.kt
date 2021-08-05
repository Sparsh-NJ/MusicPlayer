package com.sparsh.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mp : MediaPlayer
    private var totalTime : Int=0
    private lateinit var playBtn : Button
    private lateinit var volumeBar : SeekBar
    private lateinit var positionBar : SeekBar
    private lateinit var elapsedTimeLabel : TextView
    private lateinit var remainingTimeLabel : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Music"

        playBtn = findViewById(R.id.playBtn)

        mp = MediaPlayer.create(this,R.raw.udd_gaye)
        mp.isLooping = true
        mp.setVolume(0.5f,0.5f)
        totalTime = mp.duration

        //volume bar
        volumeBar = findViewById(R.id.VolumeBar)
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser){
                        val volumeNum = progress / 100.0f
                        mp.setVolume(volumeNum,volumeNum)
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )
        // position bar
        positionBar = findViewById(R.id.SeekBar)
        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser){
                        mp.seekTo(progress)
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )

        fun createTimeLabel(time:Int):String{
            var timeLabel: String
            val min = time / 1000 / 60
            val sec = time / 1000 % 60

            timeLabel = "$min:"
            if(sec < 10) timeLabel+="0"
            timeLabel += sec

            return timeLabel
        }

        elapsedTimeLabel = findViewById(R.id.elapsedTime)
        remainingTimeLabel = findViewById(R.id.RemainingTime)


        val handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                val currentPosition = msg.what

                // Update Position Bar
                positionBar.progress = currentPosition

                // Update Labels
                val elapsedTime = createTimeLabel (currentPosition)
                elapsedTimeLabel.text = elapsedTime

                val remainingTime = createTimeLabel(totalTime - currentPosition)
                remainingTimeLabel.text = "-$remainingTime"


            }
        }

        // Thread
        Thread(Runnable {
            while(mp!=null){
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                }catch (e:InterruptedException ){
                }
            }
        }).start()
    }

    fun playBtnClick(v : View){
        if(mp.isPlaying){
            //stop
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)

        }
        else{
            //start
            mp.start()
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }
}