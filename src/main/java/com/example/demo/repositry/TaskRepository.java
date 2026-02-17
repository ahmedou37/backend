package com.example.demo.repositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import com.example.demo.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByTitle(String title, Pageable pageable);
}