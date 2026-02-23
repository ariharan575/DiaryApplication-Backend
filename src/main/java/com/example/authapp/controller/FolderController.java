package com.example.authapp.controller;

import com.example.authapp.dto.diaryDto.DiaryDto;
import com.example.authapp.dto.diaryDto.FolderDto;
import com.example.authapp.model.Diary;
import com.example.authapp.model.Folder;
import com.example.authapp.model.Status;
import com.example.authapp.service.FolderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folder")
public class FolderController {

    private FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/create")
    public ResponseEntity<FolderDto> createFolder (@RequestBody FolderDto request, Authentication auth){
        return ResponseEntity.ok(folderService.createFolder(request,auth));
    }

    @PostMapping("/create/diary")
    public ResponseEntity<DiaryDto> createDiaryInsideFolder(@RequestBody DiaryDto request, Authentication auth){
        return ResponseEntity.ok(folderService.createDiaryinsideFolder(request,auth));
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<Folder> getFolderById(@PathVariable String id,
                                                @RequestParam(defaultValue = "ACTIVE") Status status,
                                                Authentication auth){
        return ResponseEntity.ok(folderService.getFolderById(id,auth,status));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Folder> updateFolderById(@RequestBody FolderDto request,
                                                   @PathVariable String id,Authentication auth,Status status){
        return ResponseEntity.ok(folderService.updateFolder(request,id,auth,status));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFolderById(@PathVariable String id, Authentication auth,Status status){
        folderService.deleteFolder(id,auth,status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/achieve/{id}")
    public ResponseEntity<Folder> moveToAchieveFolder(@PathVariable String id,
                                                      @RequestParam(defaultValue = "ACTIVE") Status status,
                                                      Authentication auth){
        return ResponseEntity.ok(folderService.moveToAchieveFolder(id,auth,status));
    }

    // 🔴 NEW RESTORE FOLDER API
    @PutMapping("/restore/{id}")
    public ResponseEntity<Folder> restoreFolder(
            @PathVariable String id,
            @RequestParam(defaultValue = "ACHIEVED") Status status,
            Authentication auth){

        return ResponseEntity.ok(folderService.restoreFolder(id,auth,status));
    }

    @GetMapping("/fetch_all")
    public ResponseEntity<List<Folder>> getAllFolderByUserId(@RequestParam(defaultValue = "ACTIVE") Status status, Authentication auth){
        return ResponseEntity.ok(folderService.fetchAllFolderByUserId(status,auth));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Folder>> getFolderByName(
            @RequestParam String text,
            @RequestParam(required = false) Status status,
            Authentication auth){

        return ResponseEntity.ok(
                folderService.searchFolderByName(text, auth, status)
        );
    }

    @GetMapping("/search/diary/{id}")
    public ResponseEntity<Page<Diary>> searchByFolderIdWiseDiary(
            @PathVariable String id,
            @RequestParam(required = false) Status status,
            @RequestParam String text,
            Authentication auth){
        return ResponseEntity.ok(
                folderService.searchByFolderIdWiseDiary(id,status,text,auth)
        );
    }

    @GetMapping("/diary/{id}")
    public ResponseEntity<Page<Diary>> getDiaryFolderWaise(@PathVariable String id, Authentication auth){
        return ResponseEntity.ok(folderService.fetchDiaryByFolderId(id,auth));
    }

    @PutMapping("/diary/restore/{id}")
    public ResponseEntity<Diary> restoreDiary(@PathVariable String id, @RequestParam String folderId, Authentication auth,Status status){
        return ResponseEntity.ok(folderService.restoreDairy(id,folderId,auth,status));
    }

}