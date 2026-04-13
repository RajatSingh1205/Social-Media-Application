package com.example.Social.Media.Application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Jacksonized
public class UserFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "follower_Id", nullable = false)
    @JsonBackReference
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "following_Id", nullable = false)
    @JsonBackReference
    private User following;

    @Column(name = "created_at")
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt =LocalDateTime.now();
    }

}
