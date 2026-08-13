package com.example.demo.api;

import com.example.demo.api.response.OpenBdBookResponse;
import com.example.demo.openbd.generated.invoker.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/books/openbd")
@Tag(name = "OpenBD Books", description = "OpenBD本API")
public interface OpenBdBooksApi {
    @GetMapping
    @Operation(summary = "OpenBD ISBN書誌取得")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "成功",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = OpenBdBookResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "OpenBD書誌なし",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "openBdBookNotFound",
                    summary = "OpenBD書誌が存在しない",
                    value = """
                        {
                          "instance": "/api/books/openbd",
                          "status": 404,
                          "title": "OpenBD書誌なし"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "リクエストエラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "invalidIsbn",
                    summary = "ISBN形式不正",
                    value = """
                        {
                          "detail": "getBooksByIsbn.isbn: 13桁ISBNまたはカンマ区切りの13桁ISBNを指定してください",
                          "errors": [
                            {
                              "field": "isbn",
                              "message": "13桁ISBNまたはカンマ区切りの13桁ISBNを指定してください"
                            }
                          ],
                          "instance": "/api/books/openbd",
                          "status": 400,
                          "title": "リクエストエラー"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "502",
            description = "OpenBD API呼び出しエラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "openBdApiCallFailed",
                    summary = "OpenBD APIの呼び出しに失敗",
                    value = """
                        {
                          "detail": "OpenBD APIの呼び出しに失敗しました",
                          "instance": "/api/books/openbd",
                          "status": 502,
                          "title": "外部API呼び出しエラー"
                        }
                        """
                )
            )
        )
    })
    List<OpenBdBookResponse> getBooksByIsbn(
        @Parameter(description = "取得対象のISBN。複数指定する場合はカンマ区切り")
        @RequestParam
        @NotBlank
        @Pattern(regexp = "\\d{13}(,\\d{13})*", message = "13桁ISBNまたはカンマ区切りの13桁ISBNを指定してください")
        String isbn
    ) throws ApiException;
}
