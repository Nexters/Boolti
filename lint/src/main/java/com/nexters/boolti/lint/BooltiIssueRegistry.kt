package com.nexters.boolti.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

class BooltiIssueRegistry : IssueRegistry() {
    override val issues = listOf(WebBridgeCommandDetector.ISSUE)

    override val api = CURRENT_API

    override val vendor = Vendor(vendorName = "Boolti")
}
