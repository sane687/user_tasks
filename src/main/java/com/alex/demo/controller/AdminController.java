package com.alex.demo.controller;

import com.alex.demo.model.Task;
import com.alex.demo.model.User;
import com.alex.demo.service.TaskService;
import com.alex.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@SessionAttributes("user")     //использует модель "user" для передачи между контроллерами с сохранением состояния(updateUser() метод)
@RequestMapping(path = "/admin", method = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST})
public class AdminController {

    final TaskService taskService;
    final UserService userService;

    @Autowired
    public AdminController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String findAll(Model model){
        List<User> userList = userService.findAll();
        model.addAttribute("users", userList);
        return "admin/user-list";
    }

    @GetMapping("/{userId}")
    public String showUser(@PathVariable Long userId, Model model){
        model.addAttribute("user", userService.findUserById(userId));
        return "admin/show-user";
    }

    @GetMapping("/edit/{userId}")
    public String editUser(@PathVariable Long userId, Model model){
        model.addAttribute("user", userService.findUserById(userId));
        return "admin/edit-user";
    }

    @PostMapping("/update")
    public String updateUser(@Valid User user, BindingResult result){       //Bindingresult должен идти сразу после @Valid объекта
        if (result.hasErrors()){
            return "admin/edit-user";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("delete/{userId}")
    public RedirectView deleteUser(@PathVariable Long userId){
        userService.deleteById(userId);
        return new RedirectView("/admin");
    }

    @GetMapping("create-task")
    public String createTask(@ModelAttribute("user") User user,
                             @ModelAttribute("task") Task task){
        return "admin/create-task";
    }

    @PostMapping("create-task/new")
    public String createTask(@Valid Task task, BindingResult result, User user){
        if(result.hasErrors()){
            return "admin/create-task";
        }
        userService.creteTask(task, user);
        return "redirect:/admin/" + user.getId();
    }

    @PostMapping("delete-task/{taskId}")
    public RedirectView deleteTask (@PathVariable Long taskId){
        Long useId = taskService.findTaskById(taskId).getUser().getId();
        taskService.deleteTaskById(taskId);
        return new RedirectView("/admin/" + useId);
    }
}
