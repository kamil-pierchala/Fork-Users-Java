package com.atipera.github.controller;

import com.atipera.github.exception.UserNotFoundException;
import com.atipera.github.model.Repository;
import com.atipera.github.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {
    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/repos/{username}")
    public ResponseEntity<?> getUserRepositories(@PathVariable String username) {
        try {
            List<Repository> repositories = gitHubService.getRepositories(username);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal Server Error"));
        }
    }

    static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
