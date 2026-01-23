package com.rodkrtz.foundationkit.command

/**
 * Command Bus for dispatching commands to their handlers.
 *
 * The Command Bus acts as a mediator that routes commands to the appropriate
 * command handlers. It decouples the command sender from the handler implementation.
 *
 * Benefits:
 * - Single entry point for all commands
 * - Easy to add cross-cutting concerns (logging, validation, transactions)
 * - Simplifies testing and mocking
 * - Enables handler registration and discovery
 *
 * Typical usage:
 * ```
 * val command = CreateUserCommand(email = "user@example.com")
 * val user = commandBus.dispatch(command)
 * ```
 */
public interface CommandBus {
    /**
     * Dispatches a command to its registered handler.
     *
     * @param C The command type
     * @param R The result type
     * @param command The command to dispatch
     * @return The result from the command handler
     * @throws IllegalStateException if no handler is registered for the command
     */
    public fun <C : Command, R> dispatch(command: C): R
}
