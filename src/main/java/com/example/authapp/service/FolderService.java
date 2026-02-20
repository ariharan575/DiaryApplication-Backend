package com.example.authapp.service;

import com.example.authapp.dto.diaryDto.DiaryDto;
import com.example.authapp.dto.diaryDto.FolderDto;
import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.model.Diary;
import com.example.authapp.model.Folder;
import com.example.authapp.model.Status;
import com.example.authapp.model.User;
import com.example.authapp.repository.DiaryRepository;
import com.example.authapp.repository.FolderRepository;
import com.example.authapp.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private DiaryEncryptionService encryptionService;

    private FolderRepository folderRepository;
    private UserRepository userRepository;
    private DiaryRepository diaryRepository;

    public FolderService(UserRepository userRepository,DiaryRepository diaryRepository ,FolderRepository folderRepository) {
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.folderRepository = folderRepository;
    }

    private String getCurrentUserId(Authentication auth){
        String email = auth.getPrincipal().toString();
        User user = userRepository.findByEmail(email).orElseThrow();
        String userId = user.getId();
        return userId;
    }

    public Pageable pageable (int start,int end) {
        return PageRequest.of(start,end,
                Sort.by(Sort.Direction.DESC,"createdAt"));
    }


    public @Nullable FolderDto createFolder(FolderDto request, Authentication auth) {

        if(folderRepository.existsByFolderNameAndUserId(getCurrentUserId(auth),request.getFolderName())){
            throw new ApiException(ErrorCode.FOLDER_NAME_ALREADY_EXISTS,"FolderName already exists be unique");
        }

        Status status = request.getStatus() != null ? request.getStatus() : Status.ACTIVE;

        Folder folder = Folder.builder()
                .userId(getCurrentUserId(auth))
                .folderName(request.getFolderName())
                .createdAt(LocalDateTime.now())
                .status(status)
                .build();

        folderRepository.save(folder);
        return new FolderDto(folder.getId(),folder.getUserId(),folder.getFolderName(),folder.getCreatedAt(),folder.getStatus());
    }

    public @Nullable Folder getFolderById(String id, Authentication auth, Status status) {

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

       return  folderRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(auth),finalStatus).orElseThrow(()->
               new ApiException(ErrorCode.FOLDER_NOT_FOUND,"Folder not found"));
    }

    public Folder updateFolder(FolderDto request, String id, Authentication auth,Status status){

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Folder folder =  folderRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(auth),finalStatus).orElseThrow(()->
                new ApiException(ErrorCode.FOLDER_NOT_FOUND,"Folder not found"));

        folder.setFolderName(request.getFolderName());

        return folderRepository.save(folder);

    }

    public void deleteFolder(String id, Authentication auth,Status status){

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Folder folder = folderRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(auth),finalStatus).orElseThrow(()->
                new ApiException(ErrorCode.FOLDER_NOT_FOUND,"Folder not found"));

        folderRepository.deleteById(folder.getId());
    }

    public Folder moveToAchieveFolder(String id, Authentication auth,Status status) {

        Folder folder = folderRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(auth),status).orElseThrow(()->
                new ApiException(ErrorCode.FOLDER_NOT_FOUND,"Folder not found"));

        folder.setStatus(Status.ACHIEVED);

        return folderRepository.save(folder);

    }

    public @Nullable List<Folder> fetchAllFolderByUserId(Status status, Authentication auth) {

        List<Folder> folder = folderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(getCurrentUserId(auth),
                status,pageable(0,80));

        if(folder.isEmpty()){
            throw new ApiException(ErrorCode.FOLDER_NOT_FOUND,"No more Folder is Exists");
        }
        return folder;
    }


    public Page<Diary> fetchDiaryByFolderId(String id, Authentication auth) {

         Page<Diary> diaries = diaryRepository.findByUserIdAndFolderIdOrderByCreatedAtDesc(getCurrentUserId(auth),
                id,pageable(0,80));

        diaries.forEach(diary -> {
            String decrypted = encryptionService.decryptDiary(
                    diary.getContent(),
                    getCurrentUserId(auth)
            );
            diary.setContent(decrypted);
        });

        if(diaries.isEmpty()){
            throw new ApiException(ErrorCode.FOLDER_NOT_FOUND,"No more diary is Exists");
        }

        return diaries;
    }

    public @Nullable Page<Folder> searchFolderByName(String text, Authentication auth,Status status) {

        Status finalStatus = (status != null) ? status : status.ACTIVE;

        Page<Folder> page = folderRepository.searchByUserIdAndText(
                getCurrentUserId(auth), text, finalStatus, pageable(0, 50));

        if(page.isEmpty()){
            throw new ApiException(ErrorCode.FOLDER_NOT_FOUND,"No more Folder is Exists");
        }
        return page;
    }

    public DiaryDto createDiaryinsideFolder(DiaryDto request, Authentication userId) {

        if(diaryRepository.existsByUserIdAndTitle(getCurrentUserId(userId),request.getTitle())){
            throw new ApiException(ErrorCode.DIARY_NAME_ALREADY_EXISTS,"Title already exists be unique");
        }

        String encryptedDiary =
                encryptionService.encryptDiary(request.getContent(),getCurrentUserId(userId));


        Diary diary = Diary.builder()
                .userId(getCurrentUserId(userId))
                .title(request.getTitle())
                .content(encryptedDiary)
                .createdAt(LocalDateTime.now())
                .folderId(request.getFolderId())
                .folderName(request.getFolderName())
                .status(Status.ACTIVE)
                .build();
        diaryRepository.save(diary);

        return new DiaryDto(diary.getId(),diary.getUserId(),diary.getTitle(),
                diary.getContent(),diary.getCreatedAt(),diary.getStatus(),diary.getFolderId(),diary.getFolderName());
    }

    public Diary restoreDairy(String id, String folderId, Authentication auth, Status status) {

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Diary diary = diaryRepository.findByIdAndUserIdAndFolderIdAndStatus(
                id,
                getCurrentUserId(auth),
                folderId,
                finalStatus
        ).orElseThrow(() ->
                new ApiException(ErrorCode.DIARY_NOT_FOUND, "Diary not found")
        );

        diary.setFolderId(null);
        diary.setFolderName(null);
        diary.setStatus(Status.ACTIVE);

        return diaryRepository.save(diary);
    }
}
