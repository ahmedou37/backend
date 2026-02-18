package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.AuthToken;
import com.example.demo.model.Notification;
import com.example.demo.model.Task;
import com.example.demo.model.Task.Status;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping
    @PreAuthorize("hasPermission('USER','READ') or hasRole('ADMIN')")
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable int id ){
        return userService.getUserById(id);
    }
    @GetMapping("/name/{name}")
    public UserDTO getUserByName(@PathVariable String name){
        return userService.getUserByName(name);
    } 
    @PostMapping
    public AuthToken addUser(@Valid @RequestBody User user , HttpServletRequest request) throws IOException{
        return userService.addUser(user,request);
    }
   
    @PutMapping
    public UserDTO updateUser(@Valid @RequestBody UserDTO user ) throws IOException{
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id ){
        return userService.deleteUser(id);
    }
    
    @GetMapping("/verifyUsernameExist/{name}")
    public boolean verifyUserName(@PathVariable String name) {
        return userService.verifyUserNameExist(name);
    }


    @GetMapping("/tasks")
    public List<Task> getTasks(Authentication authentication) {
        String name=authentication.getName();
        return userService.getTasks(name);
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("@taskSecurity.canAccessTask(#id, authentication)")
    public Task getTask(@PathVariable int id, Authentication authentication) {
        String name = authentication.getName();
        return userService.getTask(name, id);
    }

    @GetMapping("/tasks/filter")
    public List<Task> getTasksByStatus(@RequestParam Status status, Authentication authentication) {//(requiered)
        String name=authentication.getName();
        return userService.getTasksByStatus(name,status);
    }

    @PostMapping("tasks/{taskId}")
    public Task updateTaskStatus(@PathVariable int taskId ,Authentication authentication,@RequestParam Status status){
        Task task =userService.getTask(authentication.getName(), taskId);
        return userService.updateTaskStatus(task, status);
    }
    
    @GetMapping("/tasks/not")
    public List<Notification> getNotifications(Authentication authentication){
        return userService.getNotifications(authentication.getName());
    }
    @GetMapping("/tasks/unseen")
    public List<Notification> getUseenNotifications(Authentication authentication){
        return userService.getUnseenNotifications(authentication.getName());
    }
    @GetMapping("/tasks/setSeen")
    public void setToSeen(Authentication authentication){
        userService.setToSeen(authentication.getName());
    }
    @GetMapping("tasks/notLenght")
    public int getNotLenght(Authentication authentication){
        int notLenght=userService.getNotLenght(authentication.getName());
        messagingTemplate.convertAndSend("/topic/notlenght",notLenght);
        return notLenght;
    }
    

    @PostMapping("/images")
    public String saveImage(@RequestParam MultipartFile image) throws IOException{
        return userService.saveImage(image);
    }
}