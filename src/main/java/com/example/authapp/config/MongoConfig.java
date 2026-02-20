package com.example.authapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(
                "mongodb+srv://notesdaily19_db_user:nMNE12o2NrMoWq2j@cluster0.9bpiiza.mongodb.net/DiaryApplication?retryWrites=true&w=majority"
        );
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "DiaryApplication");
    }
}
