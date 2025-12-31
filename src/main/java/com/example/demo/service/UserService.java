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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Main;
import com.example.demo.mappers.UserMapper;
import com.example.demo.model.AuthenticationToken;
import com.example.demo.model.MyUserDetails;
import com.example.demo.model.Notification;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.repositry.NotificationRepositry;
import com.example.demo.repositry.TaskRepository;
import com.example.demo.repositry.UserRepositry;

import jakarta.servlet.http.HttpServletRequest;




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

    private static final Logger logger=LogManager.getLogger(Main.class);

    BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(10);

    
    public List<UserDTO> getUsers() {
        return mapper.toDtoList(userRepositry.findAllByOrderByUpdatedAtDesc());
    }

    public UserDTO getUserById(int id){
        return mapper.toDto(userRepositry.findById(id).orElse(null));
    }

    public UserDTO getUserByName(String name){
        return mapper.toDto(userRepositry.findByName(name));
    }

    public void addUser(User user) throws IOException{
        //String imageName=saveImage(image);
        user.setPassword(encoder.encode(user.getPassword()));
        // SimpleMailMessage mailMessage=new SimpleMailMessage();
        // mailMessage.setTo(user.getEmail());
        // mailMessage.setSubject("You Signed Up");
        // mailMessage.setText("you signup succefly ! you can now login at any time");
        // javaMailSender.send(mailMessage);
        userRepositry.save(user);
    }

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
    
    public AuthenticationToken verify(User user , HttpServletRequest request) {
        Authentication authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
        //AuthenticationManager tries to authenticate using its providers.
        
        if (authentication.isAuthenticated()){

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();//After authentication , the principals become UserDetails of the user

            return jwtService.generateToken(user.getName(), userDetails.getAuthorities(),request);
        }
        
        return new AuthenticationToken();

    }

    public boolean verifyUserName(String name) {
        List<UserDTO> users= mapper.toDtoList(userRepositry.findAll());
        List<String> usersNames= users.stream().map(u-> u.getName()).collect(Collectors.toList());
        logger.info("usersNames : "+usersNames);
        System.out.println("userNmas"+usersNames);
        return !usersNames.contains(name);
    }


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

    public Task updateTaskStatus(Task task,String status){
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

