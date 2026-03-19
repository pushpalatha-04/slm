package com.example.slmback.repository;

import com.example.slmback.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByCourseIdOrderBySentAtAsc(String courseId);
}