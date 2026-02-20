package com.example.authapp.dto.diaryDto;

import com.example.authapp.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.authapp.model.Status.ACTIVE;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDto {

    private String id;

    private String userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Status status;
    private String folderId = null;
    private String folderName = null;


}
