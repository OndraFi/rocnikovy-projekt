package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.domain.UserRole;
import lombok.Builder;

import java.util.List;

public final class UserDto {
    private UserDto() {}

    @Builder
    public record UserResponse(
            Long id,
            String username,
            String fullName,
            UserRole role
    ) {}

    public record PaginatedUserResponse(
            List<UserResponse> users,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
