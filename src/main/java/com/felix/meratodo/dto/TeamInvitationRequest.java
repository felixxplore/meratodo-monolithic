package com.felix.meratodo.dto;

import lombok.Data;

@Data
public class TeamInvitationRequest {
    private String email;
    private Long  teamId;
    private String teamRole;


}
