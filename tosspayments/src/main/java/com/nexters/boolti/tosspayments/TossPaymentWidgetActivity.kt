package com.nexters.boolti.tosspayments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nexters.boolti.tosspayments.databinding.ActivityTossPaymentWidgetBinding
import com.nexters.boolti.tosspayments.extension.toCurrency
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.AgreementStatus
import com.tosspayments.paymentsdk.model.AgreementStatusListener
import com.tosspayments.paymentsdk.model.PaymentCallback
import com.tosspayments.paymentsdk.model.PaymentMethodEventListener
import com.tosspayments.paymentsdk.model.PaymentWidgetOptions
import com.tosspayments.paymentsdk.model.PaymentWidgetStatusListener
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.view.PaymentMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TossPaymentWidgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTossPaymentWidgetBinding
    private val viewModel: TossPaymentsWidgetViewModel by viewModels()

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
                    binding.btnPay.isEnabled = agreementStatus.agreedRequiredTerms
                }
            }
        }

    private val paymentMethodWidgetStatusListener = object : PaymentWidgetStatusListener {
        override fun onLoad() {
            binding.btnPay.isVisible = true
            binding.btnPay.isEnabled = true
            binding.pbLoading.isVisible = false
        }

        override fun onFail(fail: TossPaymentResult.Fail) {
            Log.d(TAG, fail.errorMessage)
            binding.btnPay.isEnabled = false
            binding.pbLoading.isVisible = false
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
        binding = ActivityTossPaymentWidgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        observe()
    }

    private fun initViews() {
        intent?.run {
            initPaymentWidget(
                amount = getIntExtra(EXTRA_KEY_AMOUNT, 0),
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

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest(::handleEvent)
            }
        }
    }

    private fun handleEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.Approved -> {
                toast("승인 성공!!!!!!")
                Log.d("[MANGBAAM]", "observe: 승인 성공, orderId: ${event.orderId}")
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
                method = binding.paymentMethodWidget,
                amount = renderingAmount,
                options = renderingOptions,
                paymentWidgetStatusListener = paymentMethodWidgetStatusListener,
            )
            renderAgreement(binding.agreementWidget, agreementWidgetStatusListener)

            addPaymentMethodEventListener(paymentEventListener)
            addAgreementStatusListener(agreementStatusListener)
        }

        binding.btnPay.setOnClickListener {
            paymentWidget.requestPayment(
                paymentInfo = PaymentMethod.PaymentInfo(orderId = orderId, orderName = orderName),
                paymentCallback = object : PaymentCallback {
                    override fun onPaymentSuccess(success: TossPaymentResult.Success) {
                        handlePaymentSuccessResult(success)
                    }

                    override fun onPaymentFailed(fail: TossPaymentResult.Fail) {
                        handlePaymentFailResult(fail)
                    }
                }
            )
            Log.d("selectedPaymentMethod", paymentWidget.getSelectedPaymentMethod().toString())
        }
    }

    private fun handlePaymentSuccessResult(success: TossPaymentResult.Success) {
        viewModel.approvePayment(
            orderId = success.orderId,
            paymentKey = success.paymentKey,
            totalPrice = success.amount.toInt(),
        )
    }

    private fun handlePaymentFailResult(fail: TossPaymentResult.Fail) {
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
        private const val EXTRA_KEY_SHOW_ID = "extraKeyShowId"
        private const val EXTRA_KEY_SALES_TICKET_ID = "extraKeySalesTicketId"
        private const val EXTRA_KEY_TICKET_COUNT = "extraKeyTicketCount"
        private const val EXTRA_KEY_RESERVATION_NAME = "extraKeyReservationName"
        private const val EXTRA_KEY_RESERVATION_PHONE_NUMBER = "extraKeyReservationPhoneNumber"
        private const val EXTRA_KEY_DEPOSITOR_NAME = "extraKeyDepositorName"
        private const val EXTRA_KEY_DEPOSITOR_PHONE_NUMBER = "extraKeyDepositorPhoneNumber"

        fun getIntent(
            context: Context,
            amount: Number,
            clientKey: String,
            customerKey: String,
            orderId: String,
            orderName: String,
            currency: String,
            countryCode: String,
            showId: String,
            salesTicketTypeId: String,
            ticketCount: Int,
            reservationName: String,
            reservationPhoneNumber: String,
            depositorName: String,
            depositorPhoneNumber: String,
            variantKey: String? = null,
            redirectUrl: String? = null,
        ): Intent {
            return Intent(context, TossPaymentWidgetActivity::class.java)
                .putExtra(EXTRA_KEY_AMOUNT, amount)
                .putExtra(EXTRA_KEY_CLIENT_KEY, clientKey)
                .putExtra(EXTRA_KEY_CUSTOMER_KEY, customerKey)
                .putExtra(EXTRA_KEY_ORDER_ID, orderId)
                .putExtra(EXTRA_KEY_ORDER_NAME, orderName)
                .putExtra(EXTRA_KEY_CURRENCY, currency.toCurrency())
                .putExtra(EXTRA_KEY_COUNTRY_CODE, countryCode)
                .putExtra(EXTRA_KEY_VARIANT_KEY, variantKey)
                .putExtra(EXTRA_KEY_REDIRECT_URL, redirectUrl)
                .putExtra(EXTRA_KEY_SHOW_ID, showId)
                .putExtra(EXTRA_KEY_SALES_TICKET_ID, salesTicketTypeId)
                .putExtra(EXTRA_KEY_TICKET_COUNT, ticketCount)
                .putExtra(EXTRA_KEY_RESERVATION_NAME, reservationName)
                .putExtra(EXTRA_KEY_RESERVATION_PHONE_NUMBER, reservationPhoneNumber)
                .putExtra(EXTRA_KEY_DEPOSITOR_NAME, depositorName)
                .putExtra(EXTRA_KEY_DEPOSITOR_PHONE_NUMBER, depositorPhoneNumber)
        }
    }
}
