package com.example.demo.config

import com.example.demo.api.BooksOperationApi
import com.example.demo.api.request.BookCreateRequest
import com.example.demo.doma.generator.entity.Publisher
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.exception.LoginRateLimitExceededException
import com.example.demo.exception.OpenBdBookNotFoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.mybatis.generator.entity.BookEntity
import com.example.demo.mybatis.generator.entity.BookGenreEntity
import com.example.demo.openbd.generated.invoker.ApiException
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.constraints.Min
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.dao.CannotAcquireLockException
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

internal class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun handlePessimisticLockingFailureExceptionReturnsConflict() {
        val problem = handler.handlePessimisticLockingFailureException(CannotAcquireLockException("lock failed"))

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("更新競合")
        Assertions.assertThat(problem.getDetail()).isEqualTo("他ユーザーによって更新されています")
    }

    @Test
    fun handleForeignKeyReferenceNotFoundExceptionReturnsMissingReferences() {
        val ex = ForeignKeyReferenceNotFoundException(Publisher::class.java, 999L)

        val problem = handler.handleForeignKeyReferenceNotFoundException(ex)

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("データバリデーション")
        Assertions.assertThat(problem.getDetail()).isEqualTo("参照先データが存在しません: publisher(id=999)")
    }

    @Test
    fun handleForeignKeyReferenceNotFoundExceptionRemovesEntitySuffix() {
        val ex = ForeignKeyReferenceNotFoundException(BookEntity::class.java, 999L)

        val problem = handler.handleForeignKeyReferenceNotFoundException(ex)

        Assertions.assertThat(problem.getDetail()).isEqualTo("参照先データが存在しません: book(id=999)")
    }

    @Test
    fun handleForeignKeyReferenceNotFoundExceptionConvertsEntityNameToSnakeCase() {
        val ex = ForeignKeyReferenceNotFoundException(BookGenreEntity::class.java, 999L)

        val problem = handler.handleForeignKeyReferenceNotFoundException(ex)

        Assertions.assertThat(problem.getDetail()).isEqualTo("参照先データが存在しません: book_genre(id=999)")
    }

    @Test
    fun handleUniqueConstraintValidationExceptionReturnsBadRequest() {
        val ex = UniqueConstraintValidationException("book", "isbn", "0000000000001")

        val problem = handler.handleUniqueConstraintValidationException(ex)

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("データバリデーション")
        Assertions.assertThat(problem.getDetail()).isEqualTo("一意制約に違反しています: book(isbn=0000000000001)")
    }

    @Test
    fun handleOpenBdBookNotFoundExceptionReturnsNotFound() {
        val problem = handler.handleOpenBdBookNotFoundException(OpenBdBookNotFoundException())

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("OpenBD書誌なし")
    }

    @Test
    fun handleApiExceptionReturnsBadGateway() {
        val problem = handler.handleApiException(ApiException(500, "OpenBD error"))

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("外部API呼び出しエラー")
        Assertions.assertThat(problem.getDetail()).isEqualTo("OpenBD APIの呼び出しに失敗しました")
    }

    @Test
    @Throws(Exception::class)
    fun handleMethodArgumentNotValidExceptionReturnsFieldErrors() {
        val request = BookCreateRequest("", null, null, null, null, null, null)
        val bindingResult = BeanPropertyBindingResult(request, "request")
        bindingResult.addError(FieldError("request", "title", "size must be between 1 and 100"))
        bindingResult.addError(FieldError("request", "releaseDate", "must not be null"))
        bindingResult.addError(FieldError("request", "isbn", "must not be null"))
        val methodParameter = MethodParameter(
            BooksOperationApi::class.java.getDeclaredMethod("createBook", BookCreateRequest::class.java),
            0
        )
        val ex = MethodArgumentNotValidException(methodParameter, bindingResult)

        val problem = handler.handleMethodArgumentNotValidException(ex)

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(problem.getProperties()))
            .containsExactly("title", "releaseDate", "isbn")
        Assertions.assertThat(getErrorMessages(problem.getProperties()))
            .containsExactly("size must be between 1 and 100", "must not be null", "must not be null")
    }

    @Test
    fun handleConstraintViolationExceptionReturnsFieldErrors() {
        Validation.buildDefaultValidatorFactory().use { validatorFactory ->
            val validator = validatorFactory.getValidator()
            val violations: MutableSet<ConstraintViolation<SearchCondition?>?>? =
                validator.validate<SearchCondition?>(SearchCondition(-1))
            val ex = ConstraintViolationException(violations)

            val problem = handler.handleConstraintViolationException(ex)

            Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value())
            Assertions.assertThat(problem.getTitle()).isEqualTo("リクエストエラー")
            Assertions.assertThat(problem.getDetail()).isNotNull()
            Assertions.assertThat(getErrorFields(problem.getProperties())).containsExactly("page")
            Assertions.assertThat(getErrorMessages(problem.getProperties()).first()).isNotBlank()
        }
    }

    @Test
    fun handleLoginRateLimitExceededExceptionReturnsTooManyRequests() {
        val problem = handler.handleLoginRateLimitExceededException(LoginRateLimitExceededException())

        Assertions.assertThat(problem.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value())
        Assertions.assertThat(problem.getTitle()).isEqualTo("リクエスト回数制限")
        Assertions.assertThat(problem.getDetail()).isEqualTo("ログインリクエスト回数が日次上限を超えました")
    }

    private fun getErrorFields(properties: Map<String, Any>?): MutableList<String?> {
        return getErrors(properties)!!.stream()
            .map<String?> { error: MutableMap<String?, String?>? -> error!!.get("field") }
            .toList()
    }

    private fun getErrorMessages(properties: Map<String, Any>?): MutableList<String?> {
        return getErrors(properties)!!.stream()
            .map<String?> { error: MutableMap<String?, String?>? -> error!!.get("message") }
            .toList()
    }

    private fun getErrors(properties: Map<String, Any>?): MutableList<MutableMap<String?, String?>?>? {
        return properties!!["errors"] as MutableList<MutableMap<String?, String?>?>?
    }

    private data class SearchCondition(@field:Min(0) val page: Int)
}
