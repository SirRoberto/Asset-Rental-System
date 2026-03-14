package com.rental.asset.infrastructure.adapter.`in`.web

import com.rental.asset.domain.exception.AssetNotFoundException
import com.rental.asset.domain.exception.DomainException
import com.rental.asset.domain.exception.InvalidStatusTransitionException
import com.rental.asset.domain.exception.InventoryItemNotFoundException
import com.rental.asset.domain.exception.MaintenanceRequiredException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ProblemDetail {
        val problem = when (ex) {
            is InvalidStatusTransitionException -> 
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message!!)
            is MaintenanceRequiredException -> 
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message!!)
            is AssetNotFoundException, is InventoryItemNotFoundException -> 
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message!!)
            else -> 
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Domain error")
        }
        problem.title = ex::class.simpleName
        return problem
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid argument")
    }
}
