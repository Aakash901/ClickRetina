package com.example.clickretina.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.clickretina.utils.LoadingAnimationHandler
import com.example.clickretina.R
import com.example.clickretina.databinding.ActivityMainBinding
import com.example.clickretina.viewmodel.NetwkViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: NetwkViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    private val loadingAnimationHandler = LoadingAnimationHandler { text ->
        binding.titleTv.text = text
        binding.descriptionTv.text = text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        //here we are launching the animation to look it user friendly
        loadingAnimationHandler.startAnimation()

        viewModel.title.observe(this, Observer { title ->

            loadingAnimationHandler.stopAnimation()
            binding.titleTv.text = title
        })

        viewModel.description.observe(this, Observer { description ->

            loadingAnimationHandler.stopAnimation()
            binding.descriptionTv.text = description
        })


        viewModel.error.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                loadingAnimationHandler.stopAnimation()
            }
        })

        binding.shareTitle.setOnClickListener {
            shareTitleOrDes(binding.titleTv.text.toString())
        }

        binding.shareDescription.setOnClickListener {
            shareTitleOrDes(binding.descriptionTv.text.toString())
        }

        binding.copyTitle.setOnClickListener {
            copyTitleOrDesToClipboard(binding.titleTv.text.toString())
        }
        binding.copyDescription.setOnClickListener {
            copyTitleOrDesToClipboard(binding.descriptionTv.text.toString())
        }


        viewModel.fetchDataFromApi()
    }

    private fun setStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getResources().getColor(R.color.dark_red)
    }

    private fun copyTitleOrDesToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Title", text)
        clipboard.setPrimaryClip(clip)
        Log.d("MainActivity", "Title copied to clipboard")
    }

    private fun shareTitleOrDes(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share title via"))
    }

}
