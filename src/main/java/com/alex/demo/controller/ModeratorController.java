package com.alex.demo.controller;

import com.alex.demo.model.User;
import com.alex.demo.service.TaskService;
import com.alex.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("user")
@RequestMapping("/moderator")
public class ModeratorController {

    private final UserService userService;
    private final TaskService taskService;
    @Autowired
    public ModeratorController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("{id}")
    @PreAuthorize("#id == authentication.principal.id")  // позволяет доступ пользователю только к его странице (id авторизованного совпадает с запрашиваемым)
    public String index(@PathVariable Long id, Model model){
        model.addAttribute("moderator", userService.findUserById(id));
        model.addAttribute("users", userService.findAllUsers());
        return "moderator/moderator";
    }

    @GetMapping("user/{userId}")
    public String showUser(@PathVariable Long userId, Model model){
        model.addAttribute("user", userService.findUserById(userId));
        model.addAttribute("inProgressAndWaitingForApproveTasks", taskService.findAllInProgressAndWaitingForApproveTasks(userId));
        model.addAttribute("completedTasks", taskService.findAllCompletedTasks(userId));
        model.addAttribute("expiredTasks", taskService.findAllExpiredTasks(userId));
        return "moderator/show-user";
    }

    @GetMapping("show-task/{taskId}")
    public String showTask(@PathVariable Long taskId, Model model){
        model.addAttribute("task", taskService.findTaskById(taskId));
        return "moderator/task-details";
    }

    @PostMapping("approve-task/{taskId}")
    public String approveTask(@PathVariable Long taskId, User user){
        taskService.approveTask(taskId);
        return "redirect:/moderator/user/" + user.getId();
    }

    @PostMapping("reject-task/{taskId}")
    public String rejectTask(@PathVariable Long taskId, User user){
        taskService.rejectTask(taskId);
        return "redirect:/moderator/user/" + user.getId();
    }

    @GetMapping("user-management")
    public String userManagement(Model model, @ModelAttribute("statusMessage") String statusMessage){
        model.addAttribute("users", userService.findAllUsers());
        return "moderator/user-management";
    }

    @PostMapping("user-management/change-status/{userId}")
    public String changeUserStatus(@PathVariable Long userId,
                               @RequestParam(value = "status") boolean status,
                               RedirectAttributes redirectAttributes){

        String username = userService.findUserById(userId).getUsername();
        //сообщение о бане или разбане
        String message = userService.changeUserStatus(userId, status);

        if(!message.equals("no changes")){
            redirectAttributes.addFlashAttribute("statusMessage",
                    "User " + username + " has been "+ message);
        }
        return "redirect:/moderator/user-management";
    }
}
