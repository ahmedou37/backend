package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Main;
import com.example.demo.events.UserSignUp;
import com.example.demo.mappers.UserMapper;
import com.example.demo.model.AuthToken;
import com.example.demo.model.MyUserDetails;
import com.example.demo.model.Notification;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.repositry.NotificationRepositry;
import com.example.demo.repositry.TaskRepository;
import com.example.demo.repositry.UserRepositry;

import jakarta.servlet.http.HttpServletRequest;

import com.example.demo.model.Task.Status;




@Service
public class UserService {

    
    @Autowired
    UserRepositry userRepositry;

    @Autowired
    JWTService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    NotificationRepositry notificationRepositry;
    // @Autowired(required= false)
    // JavaMailSender javaMailSender;

    @Autowired
    UserMapper mapper;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    MyUserDetailsService myUserDetailsService;

    private static final Logger logger=LogManager.getLogger(Main.class);

    BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(10);

    @Cacheable(value = "users")
    public List<UserDTO> getUsers() {
        return mapper.toDtoList(userRepositry.findAllByOrderByUpdatedAtDesc());
    }

    @Cacheable(value = "usersById", key = "#id")
    public UserDTO getUserById(int id){
        return mapper.toDto(userRepositry.findById(id).orElse(null));
    }
    
    public UserDTO getUserByName(String name){
        return mapper.toDto(userRepositry.findByName(name));
    }

    @CacheEvict(value = "users", allEntries = true)
    public AuthToken addUser(User user, HttpServletRequest request) throws IOException{
        //String imageName=saveImage(image);
        user.setPassword(encoder.encode(user.getPassword()));
        if (verifyUserNameExist(user.getName())) {
            userRepositry.save(user);
            eventPublisher.publishEvent(new UserSignUp(mapper.toDto(user)));
            return login(user, request);
        } else {
            throw new RuntimeException("Username already exists");
        }
    }

    @Caching(
        put = @CachePut(value = "usersById", key = "#result.id"),  // Updates users cache
        evict = @CacheEvict(value = "users", allEntries = true)  // Clears list cache
    )
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepositry.findById(userDTO.getId())
       .orElseThrow(() -> new RuntimeException("User not found"));
    
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        userRepositry.save(user);

        return userDTO;
    }

    public String deleteUser(int id ){
        userRepositry.deleteById(id);
        return "user deleted!";
    }

    public boolean verifyUserNameExist(String name) {
        if (userRepositry.findByName(name) != null) {
            return false;
        }
        return true;
    }


    @Cacheable(value = "userTasks")
    public List<Task> getTasks(String name) {
        User user = userRepositry.findByName(name);
        if (user != null) {
            return user.getTasks();  
        }
        return List.of();
    }


     public Task getTask(String name, int id) {
        User user = userRepositry.findByName(name);
        if (user != null) {
            return user.getTasks().stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public List<Task> getTasksByStatus(String name, String status){
        User user = userRepositry.findByName(name);
        if (user != null) {           
            return user.getTasks().stream()
                .filter(t -> t.getStatus().equals(status))
               .collect(Collectors.toList());
        } else {
            return null;
        }

    }

    @CachePut(value = "userTasks", key = "#task.user.name")
    public Task updateTaskStatus(Task task,Status status){
        if(task!=null){
            task.setStatus(status);
            taskRepository.save(task);
        }
        return task;
    }
    
    public List<Notification> getNotifications(String name){
        User user =userRepositry.findByName(name);
        return user.getNotifications();
    }
    public List<Notification> getUnseenNotifications(String name){
        User user =userRepositry.findByName(name);
        List<Notification> unseenNotifications =new ArrayList<>();
        for(Notification notification :user.getNotifications()){
            if (notification.getSeen()==false) {
                unseenNotifications.add(notification);
            }
        };
        return unseenNotifications;
    }
    
    public void setToSeen(String name){
        User user =userRepositry.findByName(name);
        for(Notification not : user.getNotifications()){
            not.setSeen(true);
            notificationRepositry.save(not);
        }
    }
    public int getNotLenght(String name){
        User user =userRepositry.findByName(name);
        int number=0;
        for(Notification not : user.getNotifications()){
            if(not.getSeen()==false){
                number+=1;
            }
        }
        return number;
    }

    private Path imagePath;

    public UserService(@Value("${file.upload-dir}") String imagePath) throws IOException{
        this.imagePath=Paths.get(imagePath).toAbsolutePath().normalize();
        Files.createDirectories(this.imagePath);
    }
    
    public String saveImage(MultipartFile image) throws IOException {
        String imageName=image.getOriginalFilename();
        Path target =imagePath.resolve(imageName);
        Files.copy(image.getInputStream(),target,StandardCopyOption.REPLACE_EXISTING);
        return imageName;
    }
}

