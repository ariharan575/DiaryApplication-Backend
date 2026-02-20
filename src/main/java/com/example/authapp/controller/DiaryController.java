package com.example.authapp.controller;

import com.example.authapp.dto.diaryDto.DiaryDto;
import com.example.authapp.dto.diaryDto.DiaryRequest;
import com.example.authapp.model.Diary;
import com.example.authapp.model.Status;
import com.example.authapp.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @PostMapping("/create")
    public ResponseEntity<DiaryDto> createDiary(@RequestBody DiaryRequest request, Authentication auth){
        return ResponseEntity.ok(diaryService.createdDiary(request,auth));
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<DiaryDto> getDiaryById(@PathVariable String id,Authentication auth,
                                                 @RequestParam(defaultValue = "ACTIVE") Status status){
        return ResponseEntity.ok(diaryService.getDiaryById(id,auth,status));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Diary> updateDiary(@PathVariable String id,
                                             @RequestBody DiaryRequest request,
                                             Authentication auth,
                                             Status status){
        return ResponseEntity.ok(diaryService.updateDiary(id,request,auth,status));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteToTrash(@PathVariable String id,Authentication auth,Status status){
        diaryService.deleteToTrash(id,auth,status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/permanent_delete/{id}")
    public ResponseEntity<Void> deletePermanent(@PathVariable String id, Authentication auth){
        diaryService.deletePermanent(id, auth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/achieve/{id}")
    public ResponseEntity<Void> moveToAchieved(@PathVariable String id,Authentication auth,Status status){
        diaryService.moveToAchieved(id,auth,status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetch_all")
    public ResponseEntity<List<Diary>> getDiariesCollection(@RequestParam Status status, Authentication auth){
        return ResponseEntity.ok(diaryService.getRecentDiaryCollection(status,auth));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Diary>> searchDiares(@RequestParam String text,
                                                    @RequestParam(required = false) Status status, Authentication auth){
        return ResponseEntity.ok(diaryService.searchByText(text,auth,status));
    }

    @GetMapping("/specific_date_diary")
    public ResponseEntity<List<Diary>> perticularDateWaiseGetDiary(
            @RequestParam("date")
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
            Authentication auth) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay(); 

        return ResponseEntity.ok(
                diaryService.dateWiseDiares(auth, start, end)
        );
    }

    @GetMapping("/today_diary")
    public ResponseEntity<List<Diary>> getTodayDiary(Authentication auth){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return ResponseEntity.ok(diaryService.dateWiseDiares(auth,start,end));
    }

    @GetMapping("/week_diary")
    public ResponseEntity<List<Diary>> getThisWeekDiary(Authentication auth){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(6).atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return ResponseEntity.ok(diaryService.dateWiseDiares(auth,start,end));
    }

    @GetMapping("/month_diary")
    public ResponseEntity<List<Diary>> getThisMonthDiary(Authentication auth){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);
        return ResponseEntity.ok(diaryService.dateWiseDiares(auth,start,end));
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Diary> restoreDiary(
            @PathVariable String id,
            Authentication auth,
            @RequestParam Status status) {

        return ResponseEntity.ok(
                diaryService.restoreFromTrashOrAchieved(id, auth, status)
        );
    }

}
