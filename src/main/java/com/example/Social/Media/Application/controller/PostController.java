package com.example.Social.Media.Application.controller;

import com.example.Social.Media.Application.DTO.PostDTO;
import com.example.Social.Media.Application.entity.Post;
import com.example.Social.Media.Application.entity.User;
import com.example.Social.Media.Application.services.PostService;
import com.example.Social.Media.Application.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/post")
@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserServices userServices;

    @GetMapping("/getPostById/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            Post postList = postService.getPostById(postId);
            return new ResponseEntity<>(postList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("post does not exist" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addPost")
    public ResponseEntity<String> createPost(@RequestBody Post post ) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userServices.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            Post newPost = postService.createPost(
                    post.getOpinion(),
                    post.getFact(),
                    post.getImage(),
                    post.getPing(),
                    user.getUserId()

            );
            return new ResponseEntity<>("post is created " + newPost.getPostId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to create post: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }  catch (Exception e) {
            return new ResponseEntity<>("post is not created", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<?> deletePostById(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userServices.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            Post post = postService.getPostById(postId);

            if (!post.getAssignedUser().getUserId().equals(user.getUserId())) {
                return new ResponseEntity<>("You are not authorized to delete this post", HttpStatus.FORBIDDEN);
            }

            postService.deletePost(postId);

            return new ResponseEntity<>("post has been deleted", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Post does not exist: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<Post> allPosts = postService.getAllPosts();
            // Convert to DTOs to ensure username is included
            List<PostDTO> postDTOs = allPosts.stream()
                    .map(PostDTO::fromEntity)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(postDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch posts: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("editPost/{postId}")
    private ResponseEntity<?> editPost(@PathVariable Long postId,  @RequestBody Post updatedPost) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userServices.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            Post editedPost = postService.editPost(
                    user.getUserId(),
                    postId,
                    updatedPost.getOpinion(),
                    updatedPost.getFact(),
                    updatedPost.getImage(),
                    updatedPost.getPing()
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/likePost/{postId}")
    public ResponseEntity<?> likeThePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userServices.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
            postService.likePost(user.getUserId(), postId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
