package com.example.demo.service

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.response.PurchaseInvoiceResponse

interface PurchaseOperationService {
    fun create(request: PurchaseInvoiceCreateRequest): PurchaseInvoiceResponse?
}
