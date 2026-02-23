package com.example.authapp.service;

import com.example.authapp.dto.diaryDto.DiaryDto;
import com.example.authapp.dto.diaryDto.DiaryRequest;
import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.model.Diary;
import com.example.authapp.model.Status;
import com.example.authapp.model.User;
import com.example.authapp.repository.DiaryRepository;
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
public class DiaryService {

    private UserRepository userRepository;
    private DiaryRepository diaryRepository;

    @Autowired
    private DiaryEncryptionService encryptionService;

    public DiaryService(UserRepository userRepository, DiaryRepository diaryRepository) {
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
    }

    private String getCurrentUserId(Authentication auth){
        String email = auth.getPrincipal().toString();
        User user = userRepository.findByEmail(email).orElseThrow();
        String userId = user.getId();
        return userId;
    }


    public @Nullable DiaryDto createdDiary(DiaryRequest request, Authentication authentication) {

         if(diaryRepository.existsByUserIdAndTitle(getCurrentUserId(authentication),request.getTitle())){
             throw new ApiException(ErrorCode.DIARY_NAME_ALREADY_EXISTS,"Title already exists be unique");
         }

        String encryptedDiary =
                encryptionService.encryptDiary(request.getContent(),getCurrentUserId(authentication));

        Diary diary = Diary.builder()
                .userId(getCurrentUserId(authentication))
                .title(request.getTitle())
                .content(encryptedDiary)
                .createdAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();
        diaryRepository.save(diary);

        return new DiaryDto(diary.getId(),diary.getUserId(),diary.getTitle(),
                diary.getContent(),diary.getCreatedAt(),diary.getStatus(),diary.getFolderId(),diary.getFolderName());
    }

    public @Nullable DiaryDto getDiaryById(String id, Authentication userId, Status status) {

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Diary diary = diaryRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(userId),finalStatus)
                .orElseThrow(()-> new ApiException(ErrorCode.DIARY_NOT_FOUND,"Diary not found!"));

        String decryptedDiary = encryptionService.decryptDiary(diary.getContent(), getCurrentUserId(userId));

        return new DiaryDto(diary.getId(),diary.getUserId(),diary.getTitle(),
                decryptedDiary,diary.getCreatedAt(),diary.getStatus(),diary.getFolderId(),diary.getFolderName());
    }

    public Diary updateDiary(String id, DiaryRequest request, Authentication userId, Status status) {

        String encryptedDiary =
                encryptionService.encryptDiary(request.getContent(),getCurrentUserId(userId));

        Diary diary = diaryRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(userId),status)
                .orElseThrow(()-> new ApiException(ErrorCode.DIARY_NOT_FOUND,"Diary not found!"));

        diary.setTitle(request.getTitle());
        diary.setContent(encryptedDiary);
        return diaryRepository.save(diary);
    }

    public void deleteToTrash(String id, Authentication currentUserId, Status status) {

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Diary diary = diaryRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(currentUserId),finalStatus)
                .orElseThrow(()-> new ApiException(ErrorCode.DIARY_NOT_FOUND,"Diary not found"));

        diary.setStatus(Status.TRASH);
        diary.setCreatedAt(LocalDateTime.now());
        diaryRepository.save(diary);
    }

    public void deletePermanent(String id, Authentication auth){

        Diary diary = diaryRepository.findByIdAndUserId(
                id,
                getCurrentUserId(auth)
        ).orElseThrow(() ->
                new ApiException(ErrorCode.DIARY_NOT_FOUND, "Diary not found!")
        );

        diaryRepository.delete(diary);
    }


    public @Nullable List<Diary> getRecentDiaryCollection( Status status ,Authentication userId) {

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Pageable pageable = PageRequest.of(0,80,
                Sort.by(Sort.Direction.DESC,"createdAt"));

        List<Diary> diaries = diaryRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(getCurrentUserId(userId),finalStatus,pageable);

        diaries.forEach(diary -> {
            String decrypted = encryptionService.decryptDiary(
                    diary.getContent(),
                    getCurrentUserId(userId)
            );
            diary.setContent(decrypted);
        });

        if (diaries.isEmpty()){
            throw new ApiException(ErrorCode.DIARY_NOT_FOUND,"No more Diary Exist!");
        }

        return diaries;
    }

    public @Nullable List<Diary> dateWiseDiares(Authentication currentUserId, LocalDateTime start, LocalDateTime end) {

        Pageable pageable = PageRequest.of(0,80,
                Sort.by(Sort.Direction.DESC,"createdAt"));

        List<Diary> diaries = diaryRepository.findByUserIdAndCreatedAtBetweenAndStatus
                (getCurrentUserId(currentUserId),start,end,Status.ACTIVE,pageable);

        diaries.forEach(diary -> {
            String decryped = encryptionService.decryptDiary(diary.getContent(),getCurrentUserId(currentUserId));
            diary.setContent(decryped);
        });

        if (diaries.isEmpty()){
            throw new ApiException(ErrorCode.DIARY_NOT_FOUND,"No more Diary Exist!");
        }

        return diaries;
    }

    public @Nullable Page<Diary> searchByText(String text, Authentication currentUserId, Status status) {

        Status finalStatus = (status != null) ? status : status.ACTIVE;

        Pageable pageable = PageRequest.of(0,80,
                Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<Diary> page = diaryRepository.searchByUserIdAndText(getCurrentUserId(currentUserId),
                text,finalStatus,pageable);

        page.forEach(diary -> {
            String decryped = encryptionService.decryptDiary(diary.getContent(),getCurrentUserId(currentUserId));
            diary.setContent(decryped);
        });

        if (page.isEmpty()){
            throw new ApiException(ErrorCode.DIARY_NOT_FOUND,"No more Diary Exist!");
        }

        return page;
    }

    public void moveToAchieved(String id, Authentication currentUserId, Status status) {

        Status finalStatus = (status != null) ? status : Status.ACTIVE;

        Diary diary = diaryRepository.findByIdAndUserIdAndStatus(id,getCurrentUserId(currentUserId),finalStatus)
                .orElseThrow(()-> new ApiException(ErrorCode.DIARY_NOT_FOUND,"Diary not found!"));

        diary.setStatus(Status.ACHIEVED);
        diary.setFolderName(null);
        diary.setFolderId(null);
        diary.setCreatedAt(LocalDateTime.now());
        diaryRepository.save(diary);
    }

    public Diary restoreFromTrashOrAchieved(String id, Authentication auth, Status status) {

        Status finalStatus = (status != null) ? status : Status.TRASH;

        Diary diary = diaryRepository.findByIdAndUserIdAndStatus(
                id,
                getCurrentUserId(auth),
                finalStatus
        ).orElseThrow(() ->
                new ApiException(ErrorCode.DIARY_NOT_FOUND, "Diary not found")
        );

        diary.setStatus(Status.ACTIVE);
        diary.setFolderId(null);
        diary.setFolderName(null);

        return diaryRepository.save(diary);
    }
}

