package com.example.authapp.repository;

import com.example.authapp.model.Folder;
import com.example.authapp.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends MongoRepository<Folder,String> {

    boolean existsByFolderNameAndUserId(String folderName, String userId);

    @Query(value = "{'userId':?0 , 'status': ?1}",
            sort = "{'createdAt': -1}")
    List<Folder> findByUserIdAndStatusOrderByCreatedAtDesc (String userId, Status status, Pageable pageable );

    Optional<Folder> findByIdAndUserIdAndStatus(String id, String userId, Status status);

    @Query("""
    { 'userId' : ?0 , 
    'status' : ?2 ,
    $or : [
     {'folderName' :   {$regex: '^?1' , $options: 'i' } },
      ] }
     """)
    Page<Folder> searchByUserIdAndText(String userId , String text, Status status, Pageable pageable);

}
