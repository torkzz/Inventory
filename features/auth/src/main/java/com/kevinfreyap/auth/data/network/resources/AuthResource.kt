package com.kevinfreyap.auth.data.network.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/auth")
class AuthResource {
    @Serializable
    @Resource("/register")
    class Register(val parent: AuthResource)

    @Serializable
    @Resource("/login")
    class Login(val parent: AuthResource)

    @Serializable
    @Resource("/local")
    class Local(val parent: AuthResource)

    @Serializable
    @Resource("/refresh")
    class Refresh(val parent: AuthResource)

    @Serializable
    @Resource("/logout")
    class Logout(val parent: AuthResource)
}
