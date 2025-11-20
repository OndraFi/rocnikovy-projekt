package cz.upce.fei.redsys.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

public final class CategoryPermissions {
    private CategoryPermissions() {}

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole('CHIEF_EDITOR') or hasRole('ADMIN')")
    public @interface CanManageCategory { }
}
