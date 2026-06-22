@ApplicationModule(
    displayName = "Auth",
    allowedDependencies = {
        "common",
        "user",
        "user::CreateUserDTO",
        "user::ResponseUserDTO",
        "user :: UserService"
    }
)
package com.solenuk.todotaskspetproject.auth;

import org.springframework.modulith.ApplicationModule;