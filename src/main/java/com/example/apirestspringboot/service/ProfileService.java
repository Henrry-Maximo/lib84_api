package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.ProfileRecordDto;
import com.example.apirestspringboot.entity.Profile;
import com.example.apirestspringboot.exception.ProfileNotFoundException;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.ProfileRepository;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Profile create(ProfileRecordDto dto) {
        var userId = dto.userId();

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        var profile = new Profile();

        profile.setName(dto.name());
        profile.setPhone(dto.phone());
        profile.setAddress(dto.address());
        profile.setUser(user.get());

        return this.profileRepository.save(profile);
    }

    public Page<Profile> getAll(Pageable pageable) {
        return this.profileRepository.findAll(pageable);
    }

    public Profile getById(UUID id) {
        var profile = this.profileRepository.findById(id);

        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        return profile.get();
    }

    public Profile getByUserId(UUID userId) {
        var user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        var profile = this.profileRepository.findByUserId(userId);

        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(userId);
        }

        return profile.get();
    }

    @Transactional
    public Profile update(UUID id, ProfileRecordDto dto) {
        var profile = this.getById(id);

        var userId = dto.userId();

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        profile.setName(dto.name());
        profile.setPhone(dto.phone());
        profile.setAddress(dto.address());
        profile.setUser(user.get());

        return this.profileRepository.save(profile);
    }

    @Transactional
    public void delete(UUID id) {
        var profile = this.getById(id);

        this.profileRepository.delete(profile);
    }
}
