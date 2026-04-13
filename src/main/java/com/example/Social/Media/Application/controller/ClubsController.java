package com.example.Social.Media.Application.controller;

import com.example.Social.Media.Application.entity.Clubs;
import com.example.Social.Media.Application.entity.User;
import com.example.Social.Media.Application.services.ClubService;
import com.example.Social.Media.Application.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clubs")
public class ClubsController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserServices userServices;

    @PostMapping
    public ResponseEntity<?>  createClubs(@RequestBody Clubs clubs) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userServices.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            Clubs club = clubService.createClub(clubs, user.getUsername());
            return new ResponseEntity<>(club, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllClubs")
    public ResponseEntity<?> getAllClub() {
        try {
            List<Clubs> clubs = clubService.getAllClubs();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchClubs")
    public ResponseEntity<?> searchClubs(@RequestParam String name) {
        try {
            return ResponseEntity.ok(clubService.findClubByName(name));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/joinClub/{clubId}")
    public ResponseEntity<?> joinClubs(@PathVariable Long clubId,  @RequestBody Map<String, String> request) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userServices.findByUserName(userName)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            String clubCode = request.get("clubCode");

            clubService.joinClubRequest(user.getUserId(), clubId, clubCode);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
