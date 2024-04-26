package com.nexters.boolti.tosspayments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.nexters.boolti.tosspayments.databinding.DialogCommonBinding

inline fun buildBTDialog(
    context: Context,
    crossinline builder: BTDialog.Builder.() -> Unit
): BTDialog = BTDialog.Builder(context).apply(builder).build()

class BTDialog : DialogFragment(R.layout.dialog_common) {
    private var _binding: DialogCommonBinding? = null
    private val binding: DialogCommonBinding
        get() = _binding ?: error("BTDialog binding is null")

    private var title: String = ""
    private var message: String = ""
    private var buttonLabel: String = ""
    private var cancelable: Boolean = true
    private var _listener: BTDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        title = args.getString(KEY_TITLE, "")
        message = args.getString(KEY_MESSAGE, "")
        buttonLabel = args.getString(KEY_BUTTON_LABEL, "")
        cancelable = args.getBoolean(KEY_CANCELABLE, true)
    }

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

    class Builder(private val context: Context) {
        var title: String = ""
        var message: String = ""
        var buttonLabel: String = ""
        var cancelable: Boolean = true
        var listener: BTDialogListener? = null

        fun setTitle(title: String): Builder = apply {
            this.title = title
        }

        fun setTitle(@StringRes titleId: Int): Builder = apply {
            this.title = context.getString(titleId)
        }

        fun setMessage(message: String): Builder = apply {
            this.message = message
        }

        fun setMessage(@StringRes messageId: Int): Builder = apply {
            this.message = context.getString(messageId)
        }

        fun setButtonLabel(label: String): Builder = apply {
            this.buttonLabel = label
        }

        fun setButtonLabel(@StringRes labelId: Int): Builder = apply {
            this.buttonLabel = context.getString(labelId)
        }

        fun setCancelable(cancelable: Boolean): Builder = apply {
            this.cancelable = cancelable
        }

        fun setListener(listener: BTDialogListener): Builder = apply {
            this.listener = listener
        }

        fun build(): BTDialog = create(this)
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_BUTTON_LABEL = "buttonLabel"
        private const val KEY_CANCELABLE = "cancelable"

        fun create(builder: Builder): BTDialog {
            return BTDialog().apply {
                arguments = bundleOf(
                    KEY_TITLE to builder.title,
                    KEY_MESSAGE to builder.message,
                    KEY_BUTTON_LABEL to builder.buttonLabel,
                    KEY_CANCELABLE to builder.cancelable,
                )
                builder.listener?.let(this::setListener)
            }
        }
    }
}

fun interface BTDialogListener {
    fun onClick()
}
