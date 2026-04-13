package com.example.Social.Media.Application.services;

import com.example.Social.Media.Application.entity.Post;
import com.example.Social.Media.Application.entity.User;
import com.example.Social.Media.Application.repository.PostRepository;
import com.example.Social.Media.Application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserServices userServices;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(String opinion,
                           String fact,
                           String image,
                           String ping,
                           Long userId
                           )
    {

        Optional<User> userOptional = userServices.findById(userId);
        if(userOptional.isEmpty()) {
            throw new RuntimeException("user not found with this ID " + userId);
        }

        Post newPost = new Post();

        newPost.setOpinion(opinion);
        newPost.setFact(fact);
        newPost.setImage(image);
        newPost.setPing(ping);
        newPost.setAssignedUser(userOptional.get());

       return postRepository.save(newPost);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
    }

    public List<Post> getPostByIdAsList(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            return List.of(optionalPost.get());
        }
        return new ArrayList<>();
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsByPostId(postId)) {
            throw new RuntimeException("user does not exists" + postId);
        }
        postRepository.deleteById(postId);
    }

    public Post editPost(Long userId, Long postId, String newOpinion, String newFact, String newImage, String newPing) {

        if (!postRepository.existsByPostId(postId)) {
            throw new RuntimeException("podt does not exists ");
        }

        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("user does not exists ");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(" Post not found "));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("user not found "));

        if (!post.getAssignedUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("you are not authorized to edit this post");
        }

        if (newOpinion != null && !newOpinion.trim().isEmpty()) {
            post.setOpinion(newOpinion);
        }

        if (newFact != null && !newFact.trim().isEmpty())  {
            post.setFact(newFact);
        }

        if (newImage != null && !newImage.trim().isEmpty()) {
            post.setImage(newImage);
        }

        if (newPing != null && !newPing.trim().isEmpty()) {
            post.setPing(newPing);
        }

        return postRepository.save(post);
    }

    public void likePost(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post does not exist"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));



        if (post.getLikedUsers().contains(user)) {
            post.getLikedUsers().remove(user);
            if (post.getLikesCount() > 0) {
                post.setLikesCount(post.getLikesCount() - 1);

            }
        } else {
            post.getLikedUsers().add(user);
            post.setLikesCount(post.getLikesCount() + 1);
        }

        postRepository.save(post);
    }
}
