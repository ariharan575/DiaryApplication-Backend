package com.example.authapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
     @CompoundIndex(name = "user_status_folter_date_index",def = "{'userId' : 1 ,'status' : 1,'folderId' : 1, 'createdAt' : -1}"),
        @CompoundIndex(name = "user_createdAt_status_index",def = "{'userId' : 1 , 'status' : 1 ,'createdAt' : -1 }"),
        @CompoundIndex(name = "user_Status_DairyId_index", def = "{'diaryId' : 1 , 'userId' : 1, 'status' : 1}")
})
@Document(collection = "diary")
public class Diary {
    @Id
    private String id;

    @Indexed
    private String userId;

    @NotBlank(message = "Title is Empty")
    private String title;

    @NotBlank(message = "=Content is Empty")
    private String content;

    @Field
    private LocalDateTime createdAt;

    @Indexed
    private String folderId = null;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Status status;

    private String folderName = null;

}