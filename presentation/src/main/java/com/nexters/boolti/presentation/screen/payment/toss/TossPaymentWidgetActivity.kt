package com.nexters.boolti.presentation.screen.payment.toss

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.AgreementStatus
import com.tosspayments.paymentsdk.model.AgreementStatusListener
import com.tosspayments.paymentsdk.model.PaymentMethodEventListener
import com.tosspayments.paymentsdk.model.PaymentWidgetOptions
import com.tosspayments.paymentsdk.model.PaymentWidgetStatusListener
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.view.Agreement
import com.tosspayments.paymentsdk.view.PaymentMethod
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TossPaymentWidgetActivity : AppCompatActivity() {
    private lateinit var paymentMethod: PaymentMethod
    private lateinit var agreement: Agreement

    private val paymentEventListener
        get() = object : PaymentMethodEventListener() {
            override fun onCustomRequested(paymentMethodKey: String) {
                val message = "onCustomRequested : $paymentMethodKey"
                Log.d(TAG, message)

                toast(message)
            }

            override fun onCustomPaymentMethodSelected(paymentMethodKey: String) {
                val message = "onCustomPaymentMethodSelected : $paymentMethodKey"
                Log.d(TAG, message)

                toast(message)
            }

            override fun onCustomPaymentMethodUnselected(paymentMethodKey: String) {
                val message = "onCustomPaymentMethodUnselected : $paymentMethodKey"
                Log.d(TAG, message)

                toast(message)
            }
        }

    private val agreementStatusListener
        get() = object : AgreementStatusListener {
            override fun onAgreementStatusChanged(agreementStatus: AgreementStatus) {
                Log.d(TAG, "onAgreementStatusChanged : ${agreementStatus.agreedRequiredTerms}")

                runOnUiThread {
//                    binding.requestPaymentCta.isEnabled = agreementStatus.agreedRequiredTerms
                }
            }
        }

    private val paymentMethodWidgetStatusListener = object : PaymentWidgetStatusListener {
        override fun onLoad() {
            val message = "PaymentMethods loaded"

            Log.d(TAG, message)
//            binding.paymentMethodWidgetStatus.text = message
        }

        override fun onFail(fail: TossPaymentResult.Fail) {
            Log.d(TAG, fail.errorMessage)
            /*startActivity(
                PaymentResultActivity.getIntent(
                    this@PaymentWidgetActivity,
                    false,
                    arrayListOf(
                        "ErrorCode|${fail.errorCode}",
                        "ErrorMessage|${fail.errorMessage}",
                        "OrderId|${fail.orderId}"
                    )
                )
            )*/
        }
    }

    private val agreementWidgetStatusListener = object : PaymentWidgetStatusListener {
        override fun onLoad() {
            val message = "Agreements loaded"

            Log.d(TAG, message)
//            binding.agreementWidgetStatus.text = message
        }

        override fun onFail(fail: TossPaymentResult.Fail) {
            Log.d(TAG, fail.errorMessage)
/*
            startActivity(
                PaymentResultActivity.getIntent(
                    this@PaymentWidgetActivity,
                    false,
                    arrayListOf(
                        "ErrorCode|${fail.errorCode}",
                        "ErrorMessage|${fail.errorMessage}",
                        "OrderId|${fail.orderId}"
                    )
                )
            )
*/
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooltiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {

                    var paymentMethodCreated by remember { mutableStateOf(false) }
                    var agreementCreated by remember { mutableStateOf(false) }
                    val readyToRender by remember {
                        derivedStateOf {
                            paymentMethodCreated && agreementCreated
                        }
                    }
                    LaunchedEffect(readyToRender) {
                        if (readyToRender) {
                            intent?.run {
                                initPaymentWidget(
                                    amount = getDoubleExtra(EXTRA_KEY_AMOUNT, 0.0),
                                    clientKey = getStringExtra(EXTRA_KEY_CLIENT_KEY).orEmpty(),
                                    customerKey = getStringExtra(EXTRA_KEY_CUSTOMER_KEY).orEmpty(),
                                    orderId = getStringExtra(EXTRA_KEY_ORDER_ID).orEmpty(),
                                    orderName = getStringExtra(EXTRA_KEY_ORDER_NAME).orEmpty(),
                                    currency = getSerializableExtra(EXTRA_KEY_CURRENCY) as? PaymentMethod.Rendering.Currency
                                        ?: PaymentMethod.Rendering.Currency.KRW,
                                    countryCode = getStringExtra(EXTRA_KEY_COUNTRY_CODE)?.takeIf { it.length == 2 }
                                        ?: "KR",
                                    variantKey = getStringExtra(EXTRA_KEY_VARIANT_KEY),
                                    redirectUrl = getStringExtra(EXTRA_KEY_REDIRECT_URL)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        AndroidView(
                            modifier = Modifier.weight(1f),
                            factory = { context ->
                                PaymentMethod(context).apply {
                                    paymentMethod = this
                                }
                            },
                            update = { paymentMethodCreated = true }
                        )
                        AndroidView(
                            factory = { context ->
                                Agreement(context).apply {
                                    agreement = this
                                }
                            },
                            update = { agreementCreated = true }
                        )
                    }
                }
            }
        }
    }

    private fun initPaymentWidget(
        amount: Number,
        clientKey: String,
        customerKey: String,
        orderId: String,
        orderName: String,
        currency: PaymentMethod.Rendering.Currency,
        countryCode: String,
        variantKey: String?,
        redirectUrl: String?
    ) {
        val paymentWidget = PaymentWidget(
            activity = this@TossPaymentWidgetActivity,
            clientKey = clientKey,
            customerKey = customerKey,
            redirectUrl?.let {
                PaymentWidgetOptions.Builder()
                    .brandPayOption(redirectUrl = it)
                    .build()
            }
        )

        val renderingAmount = PaymentMethod.Rendering.Amount(amount, currency, countryCode)

        val renderingOptions = variantKey?.takeIf { it.isNotBlank() }?.let {
            PaymentMethod.Rendering.Options(variantKey = it)
        }
        paymentWidget.run {
            renderPaymentMethods(
                method = paymentMethod,
                amount = renderingAmount,
                options = renderingOptions,
                paymentWidgetStatusListener = paymentMethodWidgetStatusListener,
            )

            renderAgreement(agreement, agreementWidgetStatusListener)

            addPaymentMethodEventListener(paymentEventListener)
            addAgreementStatusListener(agreementStatusListener)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "PaymentWidgetActivity"
        private const val EXTRA_KEY_AMOUNT = "extraKeyAmount"
        private const val EXTRA_KEY_CLIENT_KEY = "extraKeyClientKey"
        private const val EXTRA_KEY_CUSTOMER_KEY = "extraKeyCustomerKey"
        private const val EXTRA_KEY_ORDER_ID = "extraKeyOrderId"
        private const val EXTRA_KEY_ORDER_NAME = "extraKeyOrderName"
        private const val EXTRA_KEY_CURRENCY = "extraKeyCurrency"
        private const val EXTRA_KEY_COUNTRY_CODE = "extraKeyCountryCode"
        private const val EXTRA_KEY_VARIANT_KEY = "extraKeyVariantKey"
        private const val EXTRA_KEY_REDIRECT_URL = "extraKeyRedirectUrl"

        fun getIntent(
            context: Context,
            amount: Number,
            clientKey: String,
            customerKey: String,
            orderId: String,
            orderName: String,
            currency: PaymentMethod.Rendering.Currency,
            countryCode: String,
            variantKey: String? = null,
            redirectUrl: String? = null
        ): Intent {
            return Intent(context, TossPaymentWidgetActivity::class.java)
                .putExtra(EXTRA_KEY_AMOUNT, amount)
                .putExtra(EXTRA_KEY_CLIENT_KEY, clientKey)
                .putExtra(EXTRA_KEY_CUSTOMER_KEY, customerKey)
                .putExtra(EXTRA_KEY_ORDER_ID, orderId)
                .putExtra(EXTRA_KEY_ORDER_NAME, orderName)
                .putExtra(EXTRA_KEY_CURRENCY, currency)
                .putExtra(EXTRA_KEY_COUNTRY_CODE, countryCode)
                .putExtra(EXTRA_KEY_VARIANT_KEY, variantKey)
                .putExtra(EXTRA_KEY_REDIRECT_URL, redirectUrl)
        }
    }
}
