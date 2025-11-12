package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.User;
import lombok.Builder;

public final class UserDto {
    private UserDto() {}

    @Builder
    public record UserResponse(
            Long id,
            String username,
            String fullName
    ) {}

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .build();
    }
}
