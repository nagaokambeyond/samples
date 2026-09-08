package com.example.resilientapi.job;

import jakarta.validation.constraints.NotBlank;

public record JobRequest(@NotBlank String payload, boolean shouldFail) {
}
