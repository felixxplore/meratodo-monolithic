package com.felix.meratodo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TokenResponse {

   private String message;
   private  String accessToken;
   private String refreshToken;

   public TokenResponse(String message){
      this.message=message;
   }
   public  TokenResponse(String accessToken,String refreshToken){
      this.accessToken=accessToken;
      this.refreshToken=refreshToken;
   }
}
