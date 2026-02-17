package com.example.demo.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.repositry.TaskRepository;
import com.example.demo.model.Task;

@Component("taskSecurity")
public class TaskSecurity {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public boolean canAccessTask(int taskId ,Authentication authentication){
        Task task=taskRepository.findById(taskId).orElseThrow(()-> new RuntimeException("Task not found"));

        if (task!=null && (task.getAssignedUser().getName().equals(authentication.getName()) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))){
            return true;
        }
        return false;
    }
}



// @PreAuthorize("@taskSecurity.canAccessTask(#id, authentication)")
//public boolean canAccessTask(Long id, Authentication authentication) {
    //                         ↑       ↑ AUTOMATICALLY INJECTED!
    // You don't pass it - Spring does!
