package com.nexters.boolti.tosspayments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nexters.boolti.tosspayments.databinding.DialogCommonBinding

class BTDialog(
    private var title: String = "",
    private var message: String = "",
    private var buttonLabel: String = "",
    private val cancelable: Boolean = true,
    listener: BTDialogListener? = null,
) : DialogFragment(R.layout.dialog_common) {
    private var _binding: DialogCommonBinding? = null
    private val binding: DialogCommonBinding
        get() = _binding ?: error("BTDialog binding is null")

    private var _listener: BTDialogListener? = listener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        initViews()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initDialog() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        isCancelable = cancelable
    }

    private fun initViews() = with(binding) {
        tvTitle.text = title
        tvMessage.text = message
        btnCta.text = buttonLabel

        btnCta.setOnClickListener {
            _listener?.onClick()
        }
    }

    fun setListener(listener: BTDialogListener) {
        _listener = listener
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setButtonLabel(buttonLabel: String) {
        this.buttonLabel = buttonLabel
    }
}

fun interface BTDialogListener {
    fun onClick()
}
