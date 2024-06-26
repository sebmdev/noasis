package dev.sebm.noasis.jsonresponses;

import dev.sebm.noasis.jsonresponses.models.User;
import lombok.Data;

@Data
public class LoginSuccessResponse {
    private String message;
    private User user;
}
