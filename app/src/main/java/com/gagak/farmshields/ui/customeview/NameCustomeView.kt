package com.gagak.farmshields.ui.customeview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.gagak.farmshields.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NameCustomeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val nameInputLayout: TextInputLayout
    private val nameEditText: TextInputEditText

    init {
        LayoutInflater.from(context).inflate(R.layout.view_name_custome, this, true)
        nameInputLayout = findViewById(R.id.username_input_layout)
        nameEditText = findViewById(R.id.username_input)

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateName(s.toString())
            }
        })
    }

    private fun validateName(name: String) {
        if (name.isEmpty()) {
            nameInputLayout.error = context.getString(R.string.invalid_name_error)
            nameInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.red)
        } else {
            nameInputLayout.error = null
            nameInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimaryVariant)
        }
    }

    fun getText(): String {
        return nameEditText.text.toString()
    }
}
