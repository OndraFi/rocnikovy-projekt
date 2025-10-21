package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.User;
import lombok.Builder;

public final class UserDto {
    private UserDto() {}

    @Builder
    public record UserResponse(
            Long id,
            String username
    ) {}

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
