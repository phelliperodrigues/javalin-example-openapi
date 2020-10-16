package dev.phellipe.controller

import dev.phellipe.ErrorResponse
import dev.phellipe.model.NewUserRequest
import dev.phellipe.model.User
import dev.phellipe.services.UserService
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.plugin.openapi.annotations.*

object UserController {

    @OpenApi(
        summary = "Create user",
        operationId = "createUser",
        tags = ["User"],
        requestBody = OpenApiRequestBody([OpenApiContent(NewUserRequest::class)]),
        responses = [
            OpenApiResponse("201"),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)])
        ]
    )
    fun create(ctx: Context) {
        val user = ctx.body<NewUserRequest>()
        UserService.save(name = user.name, email = user.email)
        ctx.status(201)
    }

    @OpenApi(
        summary = "Get all users",
        operationId = "getAllUsers",
        tags = ["User"],
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )
    fun getAll(ctx: Context) {
        ctx.json(UserService.getAll())
    }

    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        pathParams = [OpenApiParam("userId", Int::class, "The user ID")],
        responses = [
            OpenApiResponse("200", [OpenApiContent(User::class)]),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)]),
            OpenApiResponse("404", [OpenApiContent(ErrorResponse::class)])
        ]
    )
    fun getOne(ctx: Context) {
        ctx.json(UserService.findById(ctx.validPathParamUserId()) ?: throw NotFoundResponse("User not found"))
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        pathParams = [OpenApiParam("userId", Int::class, "The user ID")],
        requestBody = OpenApiRequestBody([OpenApiContent(NewUserRequest::class)]),
        responses = [
            OpenApiResponse("204"),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)]),
            OpenApiResponse("404", [OpenApiContent(ErrorResponse::class)])
        ]
    )
    fun update(ctx: Context) {
        val user = UserService.findById(ctx.validPathParamUserId()) ?: throw NotFoundResponse("User not found")
        val newUser = ctx.body<NewUserRequest>()
        UserService.update(id = user.id, name = newUser.name, email = newUser.email)
        ctx.status(204)
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        pathParams = [OpenApiParam("userId", Int::class, "The user ID")],
        responses = [
            OpenApiResponse("204"),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)]),
            OpenApiResponse("404", [OpenApiContent(ErrorResponse::class)])
        ]
    )
    fun delete(ctx: Context) {
        val user = UserService.findById(ctx.validPathParamUserId()) ?: throw NotFoundResponse("User not found")
        UserService.delete(user.id)
        ctx.status(204)
    }
}

// Prevent duplicate validation of userId
private fun Context.validPathParamUserId() = this.pathParam<Int>("userId").check({ it >= 0 }).get()
