package com.felix.meratodo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptInvitationRequest {

    @NotBlank(message = "Token is required.")
    private String token;

}
