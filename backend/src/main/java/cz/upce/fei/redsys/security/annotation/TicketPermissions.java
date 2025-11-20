package cz.upce.fei.redsys.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

public final class TicketPermissions {
    private TicketPermissions() {}

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole('CHIEF_EDITOR') or hasRole('EDITOR') or hasRole('REVIEWER') or hasRole('ADMIN')")
    public @interface CanViewTicket { }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole('CHIEF_EDITOR') or hasRole('ADMIN')")
    public @interface CanManageTicket { }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole('CHIEF_EDITOR') or hasRole('REVIEWER') or hasRole('ADMIN')")
    public @interface CanTransitionTicketState { }
}
