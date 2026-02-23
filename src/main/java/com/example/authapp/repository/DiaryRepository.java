package com.example.authapp.repository;

import com.example.authapp.model.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.authapp.model.Status;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends MongoRepository<Diary,String> {

    boolean existsByUserIdAndTitle(String userId,String title);

    Optional<Diary> findByIdAndUserId(String id, String userId);

    @Query(value = "{'userId':?0 , 'status': ?1}",
           sort = "{'createdAt': -1}")
    List<Diary> findByUserIdAndStatusOrderByCreatedAtDesc (String userId,Status status,Pageable pageable );

    Optional<Diary> findByIdAndUserIdAndStatus(String id, String userId, Status status);

    @Query("{ 'userId': ?0, 'status': ?3, 'createdAt': { '$gte': ?1 , '$lte': ?2 }}")
    List<Diary> findByUserIdAndCreatedAtBetweenAndStatus(String userId, LocalDateTime start,
                                                         LocalDateTime end, Status status,
                                                         Pageable pageable);
    @Query("""
    { 'userId' : ?0 , 
    'status' : ?2 ,
    $or : [
     {'title' :   {$regex: '^?1' , $options: 'i' } },
     {'content' : {$regex: '^?1' , $options: 'i'} }
      ] }
     """)
    Page<Diary> searchByUserIdAndText(String userId , String text, Status status, Pageable pageable);

    @Query("{ 'userId': ?0, 'status': ?3, " +
            "$or: [ { 'folderId': ?1 }, { 'folderName': { $regex: ?2, $options: 'i' } } ] }")
    Page<Diary> searchByUserIdAndFolderIdAndText(
            String userId,
            String folderId,
            String text,
            Status status,
            Pageable pageable);

    @Query(value = "{'userId': ?0 , 'folderId': ?1}",
    sort = "{'createdAt': -1}")
    Page<Diary> findByUserIdAndFolderIdOrderByCreatedAtDesc(String userId, String folderId,
                                                                      Pageable pageable);

    Optional<Diary> findByIdAndUserIdAndFolderIdAndStatus(
            String id,
            String userId,
            String folderId,
            Status status
    );

    List<Diary> findByUserIdAndFolderId(String userId, String folderId);

    void deleteByUserIdAndFolderId(String userId, String folderId);



}
