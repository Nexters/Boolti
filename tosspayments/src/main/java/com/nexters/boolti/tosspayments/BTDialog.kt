package com.nexters.boolti.tosspayments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nexters.boolti.tosspayments.databinding.DialogCommonBinding

class BTDialog : DialogFragment(R.layout.dialog_common) {
    private var _binding: DialogCommonBinding? = null
    private val binding: DialogCommonBinding
        get() = _binding ?: error("BTDialog binding is null")

    var title: String = ""
    var message: String = ""
    var buttonLabel: String = ""
    var listener: BTDialogListener? = null

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
    }

    private fun initViews() = with(binding) {
        tvTitle.text = title
        tvMessage.text = message
        btnCta.text = buttonLabel

        btnCta.setOnClickListener {
            listener?.onClick()
        }
    }
}

fun interface BTDialogListener {
    fun onClick()
}
