package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.UserDto.PaginatedUserResponse;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.security.annotation.UserPermissions.CanManageUser;
import cz.upce.fei.redsys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Manage users")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class UserController {

    private final UserService userService;

    @Operation(summary = "List users", description = "List all users with pagination", operationId = "listUsers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = @Content(schema = @Schema(implementation = PaginatedUserResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedUserResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/users: {}", pageable);
        PaginatedUserResponse response = userService.list(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Block user", description = "Blocks a user, preventing login", operationId = "blockUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User blocked",
                    content = @Content(schema = @Schema(implementation = UserResponse.class)))
    })
    @PatchMapping("/{identifier}/block")
    @CanManageUser
    public ResponseEntity<UserResponse> block(@PathVariable String identifier) {
        log.debug("PATCH /api/users/{}/block", identifier);
        return ResponseEntity.ok(userService.blockUser(identifier));
    }

    @Operation(summary = "Unblock user", description = "Reactivates a user account", operationId = "unblockUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User unblocked",
                    content = @Content(schema = @Schema(implementation = UserResponse.class)))
    })
    @PatchMapping("/{identifier}/unblock")
    @CanManageUser
    public ResponseEntity<UserResponse> unblock(@PathVariable String identifier) {
        log.debug("PATCH /api/users/{}/unblock", identifier);
        return ResponseEntity.ok(userService.unblockUser(identifier));
    }
}
