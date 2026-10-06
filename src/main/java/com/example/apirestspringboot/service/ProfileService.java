package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.ProfileRecordDto;
import com.example.apirestspringboot.entity.Profile;
import com.example.apirestspringboot.exception.ProfileNotFoundException;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.ProfileRepository;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public Profile create(ProfileRecordDto dto) {
        var profile = new Profile();

        BeanUtils.copyProperties(dto, profile, "userId");
        profile.setUser(userRepository.findById(dto.userId()).orElseThrow(() -> new UserNotFoundException(dto.userId())));

        return this.profileRepository.save(profile);
    }

    public Page<Profile> getAll(Pageable pageable) {
        return this.profileRepository.findAll(pageable);
    }

    public Profile getById(UUID id) {
        return this.profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
    }

    public Profile update(Profile profile, ProfileRecordDto dto) {
        BeanUtils.copyProperties(dto, profile, "userId"); // não atualizar userId
        profile.setUser(userRepository.findById(dto.userId()).orElseThrow(() -> new UserNotFoundException(dto.userId())));

        return this.profileRepository.save(profile);
    }

    public Profile getByUserId(UUID userId) {
        return this.profileRepository.findByUserId(userId).orElseThrow(() -> new ProfileNotFoundException(userId));
    }

    public void delete(Profile profile) {
        this.profileRepository.deleteById(profile.getId());
    }
}
