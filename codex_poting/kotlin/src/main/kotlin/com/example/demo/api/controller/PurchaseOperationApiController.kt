package com.example.demo.api.controller

import com.example.demo.api.PurchaseOperationApi
import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.service.PurchaseOperationService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class PurchaseOperationApiController(private val service: PurchaseOperationService) : PurchaseOperationApi {
    override fun createPurchaseInvoice(request: PurchaseInvoiceCreateRequest): PurchaseInvoiceResponse {
        return service.create(request)!!
    }
}
