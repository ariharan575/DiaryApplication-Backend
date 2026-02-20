package com.example.authapp.dto.diaryDto;

import com.example.authapp.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDto {

    private String id;
    private String userId;
    private String folderName;
    private LocalDateTime createdAt;
    private Status status = Status.ACTIVE;

}
