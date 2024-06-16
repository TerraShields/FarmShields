package com.gagak.farmshields.ui.customeview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.gagak.farmshields.R
import com.gagak.farmshields.databinding.ViewConfirmPasswordCustomBinding

class ConfirmPasswordCustomeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewConfirmPasswordCustomBinding =
        ViewConfirmPasswordCustomBinding.inflate(LayoutInflater.from(context), this)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_confirm_password_custom, this, true)
        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })
    }

    fun getText(): String {
        return binding.passwordInput.text.toString()
    }

    fun addTextChangedListener(listener: (CharSequence?) -> Unit) {
        binding.passwordInput.doOnTextChanged { text, _, _, _ ->
            listener(text)
        }
    }

    fun validatePassword(confirmPassword: String) {
        val originalPassword = binding.passwordInputLayout.editText?.text.toString()
        if (confirmPassword != originalPassword) {
            binding.passwordInputLayout.error = context.getString(R.string.password_mismatch_error)
            binding.passwordInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.red)
        } else {
            binding.passwordInputLayout.error = null
            binding.passwordInputLayout.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
        }
    }
}
