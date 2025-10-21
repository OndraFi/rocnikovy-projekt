package cz.upce.fei.redsys.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDCodeGenerator {
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
