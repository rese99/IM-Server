package com.yun.im.utilies;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class UUid {
    public String uuid(String email) {
        return String.valueOf(UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8)).hashCode()).replace("-", "");
    }
}
