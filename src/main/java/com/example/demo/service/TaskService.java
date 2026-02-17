package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.model.Notification;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repositry.NotificationRepositry;
import com.example.demo.repositry.TaskRepository;
import com.example.demo.repositry.UserRepositry;

import lombok.extern.slf4j.Slf4j;

import com.example.demo.model.Task.Status;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepositry userRepositry;
    @Autowired
    private NotificationRepositry notificationRepositry;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Cacheable(value = "tasks")
    public List<Task> getTasks(Pageable pageable,String title) {
        if(title == null){
            return taskRepository.findAll(pageable).getContent();
        }
        return taskRepository.findAllByTitle(title,pageable);
    }

    public Task getTask(int id) {
        return taskRepository.findById(id).orElse(null);
    }

    public void addTask(Task task) {
        taskRepository.save(task);
    }

    public String deleteTask(int id) {
        taskRepository.deleteById(id);
        return "Task deleted successfully";
    }

    public List<Task> getTasksByStatus(Status status) {
        List<Task> Tasks = new ArrayList<>();
        for (Task task : taskRepository.findAll()) {
            if (task.getStatus().toString().equals(status)) {
                Tasks.add(task);
            }
        }
        return Tasks;
    }
    
    public String assingTaskToUser(String username,int taskId){
        User user =userRepositry.findByName(username);
        if (user == null) {
           throw new RuntimeException("User not found with name: " + username);
        }
        Task task =taskRepository.findById(taskId).orElse(null);
        Notification notification=new Notification();
        notification.setLocalDate(LocalDate.now());
        notification.setSeen(false);
        notification.setName("new task assigned with title "+task.getTitle());
        user.getTasks().add(task);
        task.setAssignedUser(user);
        notification.setUser(user);
        notificationRepositry.save(notification)    ;
        user.getNotifications().add(notification);
        userRepositry.save(user);
        taskRepository.save(task);
        messagingTemplate.convertAndSend("/topic/notification",notification);
        return "task assigned to user succeffly !";
    }
}
