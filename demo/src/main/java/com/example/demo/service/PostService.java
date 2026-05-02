package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAllPublished() {
        // Public post listing should expose published content, regardless of active status.
        return postRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<Post> findAllActive() {
        return postRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Post> findAllActiveAdmin() {
        return postRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post createPost(Post post, User author) {
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setActive(post.isPublished());
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        post.setActive(post.isPublished());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}