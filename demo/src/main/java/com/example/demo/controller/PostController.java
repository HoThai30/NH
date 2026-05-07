package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public PostController(PostService postService, UserService userService, FileStorageService fileStorageService) {
        this.postService = postService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPublished() {
        return ResponseEntity.ok(postService.findAllPublished());
    }
 
    @GetMapping("/active")
    public ResponseEntity<List<Post>> getAllActive() {
        return ResponseEntity.ok(postService.findAllActive());
    }
    @GetMapping("/promotion")
    public ResponseEntity<List<Post>> getAllPromotion() {
        return ResponseEntity.ok(postService.findAllPromotion());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<Post>> getAllAdmin() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return postService.findById(id)
            .map(post -> ResponseEntity.ok(post))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.store(file);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("filename", filename);
            }});
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error uploading file: " + ex.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody Post post) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();

        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) username = ((UserDetails) principal).getUsername();
        else if (principal instanceof String) username = (String) principal;

        User author = userService.findByEmail(username).orElse(null);
        if (author == null) return ResponseEntity.status(403).build();

        try {
            Post saved = postService.createPost(post, author);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Post post) {
        if (!postService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        post.setId(id);
        try {
            Post saved = postService.updatePost(post);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!postService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}