package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

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
    
    public List<Post> findAllPromotion(){
    	return postRepository.findByPromotionTrueOrderByCreatedAtDesc();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
    
    private void validatePost(Post post) {
    	if(post.getActive() && !post.isPublished()) {
    		throw new RuntimeException("active must be publish");
    	}
    	if(post.getPromotion() && !post.isPublished()) {
    		throw new RuntimeException("promotion must be publish");
    	}
    }

    @Transactional
    public Post createPost(Post post, User author) {
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        validatePost(post);
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        validatePost(post);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}