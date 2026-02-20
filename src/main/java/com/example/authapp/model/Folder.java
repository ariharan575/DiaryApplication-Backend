package com.example.authapp.model;

import com.example.authapp.model.Diary;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(name = "user_folder_name_index", def = "{'userId': ?1 ,'folderName': ?1,'createdAt': -1}"),
        @CompoundIndex(name = "user_createdAt_index",def = "{'userId': ?1, 'createdAt': -1}"),
        @CompoundIndex(name = "user_Status_FolderId_index", def = "{'folderId' : 1 , 'userId' : 1, 'status' : 1}")

})
@Document(collection = "folder")
public class Folder {

    @Id
    private String id;

    @Indexed
    private String userId;

    @NotBlank(message = "folderName is Empty")
    private String folderName;

    private LocalDateTime createdAt;

    private Status status = Status.ACTIVE;

}
