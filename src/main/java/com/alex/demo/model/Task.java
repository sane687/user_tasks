package com.alex.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name")
    @Size(min = 2, max = 30, message = "task name should be between 2 and 30 characters")
    private String taskName;

    @Column(name = "deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "only future or present date allowed")
    private LocalDate deadline;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @Column(name = "completed")
    private LocalDate completed;

    @Column(name = "task_details")
    @Size(min = 5, max = 300, message = "task details should be between 2 and 30 characters")
    private String taskDetails;

}
