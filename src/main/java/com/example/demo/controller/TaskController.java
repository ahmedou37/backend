package com.example.demo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Task;
import com.example.demo.model.Task.Status;
import com.example.demo.service.TaskService;



@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskService taskService;

    @GetMapping
    public List<Task> getTasks(@RequestParam (required = false, defaultValue = "0") int page ,
        @RequestParam(required = false, defaultValue = "10") int size ,
        @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
        @RequestParam(required = false, defaultValue = "desc") String sortDir,
        @RequestParam(required = false ) String title
        ){
        if (sortDir != null && sortDir.equals("desc")){
            return taskService.getTasks(PageRequest.of(page, size,Sort.by(sortBy).descending()),title);
        }
        return taskService.getTasks(PageRequest.of(page, size ,Sort.by(sortBy).ascending()),title);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Task addTask(@RequestBody Task task) {
        taskService.addTask(task);
        return task;
    }
    @GetMapping("/{id}")
    public Task getTask(@PathVariable int id) {
        return taskService.getTask(id);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTask(@PathVariable int id) {
        return taskService.deleteTask(id);
    }
    @GetMapping("/filter")
    public List<Task> getCompletedTasks(@RequestParam Status status){
        return taskService.getTasksByStatus(status);
    }
    @PostMapping("{username}/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String assingTaskToUser(@PathVariable int taskId,@PathVariable String username){
        return taskService.assingTaskToUser(username, taskId);
    }
}
