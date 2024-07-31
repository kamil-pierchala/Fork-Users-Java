package com.atipera.github.service;

import com.atipera.github.exception.UserNotFoundException;
import com.atipera.github.model.Branch;
import com.atipera.github.model.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    @Value("${github.api.url:https://api.github.com}")
    private String githubApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Repository> getRepositories(String username) {
        String url = String.format("%s/users/%s/repos", githubApiUrl, username);
        RequestEntity<Void> request = new RequestEntity<>(getHeaders(), HttpMethod.GET, URI.create(url));

        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(request, Repository[].class);
            Repository[] repositories = response.getBody();
            if (repositories == null) {
                throw new UserNotFoundException("No repositories found for user: " + username);
            }

            return Arrays.stream(repositories)
                    .filter(repo -> !repo.isFork())
                    .peek(repo -> repo.setBranches(getBranches(username, repo.getName())))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User not found: " + username);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("An error occurred while fetching repositories: " + e.getMessage());
        }
    }

    private List<Branch> getBranches(String username, String repositoryName) {
        String url = String.format("%s/repos/%s/%s/branches", githubApiUrl, username, repositoryName);
        RequestEntity<Void> request = new RequestEntity<>(getHeaders(), HttpMethod.GET, URI.create(url));

        try {
            ResponseEntity<Branch[]> response = restTemplate.exchange(request, Branch[].class);
            Branch[] branches = response.getBody();
            if (branches == null) {
                throw new RuntimeException("No branches found for repository: " + repositoryName);
            }

            return Arrays.asList(branches);

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("An error occurred while fetching branches: " + e.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        return headers;
    }
}
