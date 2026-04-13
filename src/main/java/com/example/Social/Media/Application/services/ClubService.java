package com.example.Social.Media.Application.services;

import com.example.Social.Media.Application.entity.Clubs;
import com.example.Social.Media.Application.entity.User;
import com.example.Social.Media.Application.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.security.SecureRandom;

@Service
public class ClubService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserServices userServices;

    public Clubs createClub(Clubs clubs, String username) {
        try {
            User user = user = userServices.findByUserName(username)
                            .orElseThrow(() -> new RuntimeException("user not found"));

            if (clubs.isPrivate()) {
                String clubCode = generateClubCode();;
                clubs.setClubCode(clubCode);
            } else {
                clubs.setClubCode(null);
            }


            clubs.setOwner(user);
            clubs.getAdmins().add(user);
            clubs.getMembers().add(user);
            clubs.setCreatedAt(LocalDateTime.now());
            clubs.setUpdatedAt(LocalDateTime.now());

            return clubRepository.save(clubs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error while creating club: " + e.getMessage(), e);
        }
    }

    public List<Clubs> getAllClubs() {
        return clubRepository.findAll();
    }

    public List<Clubs> findClubByName(String clubName) {
        try {
            return clubRepository.findByClubNameContainingIgnoreCase(clubName);
        } catch (Exception e) {
            throw new RuntimeException("error " + e.getMessage());
        }
    }

    public void joinClubRequest(Long userId, Long clubId, String enteredClubCode) {

        User user = userServices.findById(userId)
                .orElseThrow(() -> new RuntimeException("user does not exists by this userId "));

        Clubs club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("club odes not exists with this username "));

        if (club.getMembers().contains(user)) {
            throw new RuntimeException("user already in the club ");
        }

        if (club.isPrivate()) {
            if (enteredClubCode == null || !enteredClubCode.equals(club.getClubCode())) {
                throw new RuntimeException("invalid club code access details ");
            }
        }

        if (club.isRequiredApproval()) {
            System.out.println("Join request pending approval for club: " + club.getClubName());
            return;
        }

        club.getMembers().add(user);
        clubRepository.save(club);

        System.out.println("✅ " + user.getUsername() + " joined club: " + club.getClubName());
    }

    private String generateClubCode() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

}
