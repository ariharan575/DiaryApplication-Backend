package com.example.authapp.dto.diaryDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRequest {

    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "content is Empty")
    private String content;
}