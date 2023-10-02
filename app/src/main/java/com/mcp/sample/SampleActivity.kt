package com.mcp.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcp.one4all.ViewType
import com.mcp.one4all.extension.capitalizeFirstLetter
import com.mcp.sample.databinding.ActivitySampleBinding
import java.util.Random


class SampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.apply {
            recyclerView.apply {
                adapter = SampleAdapter(createSampleTypes())
                layoutManager = LinearLayoutManager(this@SampleActivity)
            }
        }
    }

    private fun createSampleTypes(): List<SampleData> {
        val rng = Random()
        var list = mutableListOf<SampleData>()
        ViewType.values().forEach {
            list.add(SampleData(it.name.capitalizeFirstLetter(), it, isRequired = rng.nextBoolean()))
        }
        ViewType.values().forEach {
            list.add(SampleData(it.name.capitalizeFirstLetter(), it, isRequired = rng.nextBoolean()))
        }
        return list
    }
}
