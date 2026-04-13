package com.example.Social.Media.Application.entity;

import com.example.Social.Media.Application.services.UserFollowerServices;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@ToString
@Jacksonized
public class User  {

    //    user id
    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    //    username
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    //    password
    @Column(name = "password", nullable = false)

    private String password;

    //    email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    //    firstname
    @Column(name = "firstName")
    private String firstName;

    //    lastName
    @Column(name = "lastName")
    private String lastName;

    private Long followers = 0L;
    private Long following = 0L;

    @OneToMany(mappedBy = "assignedUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<UserFollower> followerRelation = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<UserFollower> followingRelation = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Clubs> ownedClubs = new HashSet<>();


    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Clubs> joinedClubs = new HashSet<>();

    @ManyToMany(mappedBy = "admins")
    @JsonIgnore
    private Set<Clubs> adminClubs = new HashSet<>();


    public Set<String> getRoles() {
        return roles;
    }

    public void addFollowing(User userToFollow) {
        UserFollower follow = new UserFollower();
        follow.setFollower(this);
        follow.setFollowing(userToFollow);
        followingRelation.add(follow);
        userToFollow.getFollowerRelation().add(follow);


        this.following = (long) followingRelation.size();
        userToFollow.followers = (long) userToFollow.followerRelation.size();

    }

    public void removeFollowing(User userToUnfollow) {
        followingRelation.removeIf(follow -> follow.getFollowing().equals(userToUnfollow));
        userToUnfollow.getFollowerRelation().removeIf(follow -> follow.getFollower().equals(this));

        // Update counts
        this.following = (long) followingRelation.size();
        userToUnfollow.followers = (long) userToUnfollow.followerRelation.size();
    }

//    public void joinClub(Club club) {
//        this.joinedClubs.add(club);
//        club.getMembers().add(this);
//    }
//
//    public void leaveClub(Club club) {
//        this.joinedClubs.remove(club);
//        club.getMembers().remove(this);
//    }


}
