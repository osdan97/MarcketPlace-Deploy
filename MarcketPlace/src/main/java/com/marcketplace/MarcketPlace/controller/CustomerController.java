package com.marcketplace.MarcketPlace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcketplace.MarcketPlace.dto.request.ProfileUpdateDTO;

import com.marcketplace.MarcketPlace.dto.response.ProfileDTORes;
import com.marcketplace.MarcketPlace.exception.IdNotFoundException;
import com.marcketplace.MarcketPlace.exception.NameExistsException;

import com.marcketplace.MarcketPlace.service.IProfileService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Controlador de profiles")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private IProfileService profileService;

    // Listar usuario por Email
    @GetMapping("/{byEmail}")
    public ResponseEntity<ProfileDTORes> getProfile(@PathVariable String byEmail) throws IdNotFoundException {
        return ResponseEntity.ok(profileService.getCustomerByEmail(byEmail));
    }

    // Actualizar perfil del usuario
    @PatchMapping("/{byEmail}")
    public ResponseEntity<HttpStatus> updateProfile(@PathVariable String byEmail, @Valid @RequestBody ProfileUpdateDTO profileUpdateDTO)
            throws IdNotFoundException,
            NameExistsException {
                profileUpdateDTO.setEmail(byEmail);
        profileService.updateCustomer(profileUpdateDTO);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
