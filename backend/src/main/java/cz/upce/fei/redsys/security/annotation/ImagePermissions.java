package cz.upce.fei.redsys.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

public final class ImagePermissions {
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole('CHIEF_EDITOR') or hasRole('EDITOR') or hasRole('ADMIN')")
    public @interface CanUploadImage { }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole('CHIEF_EDITOR') or hasRole('EDITOR') or hasRole('ADMIN')")
    public @interface CanDeleteImage { }
}