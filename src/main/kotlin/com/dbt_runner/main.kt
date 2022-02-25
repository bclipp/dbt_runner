package com.dbt_runner

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val repository = getVariables()
    val gitOutput = cloneGitProject(repository)
    val dbtOutput = runDbt("run")
    println(dbtOutput)
}

fun getVariables(): String {
    val variable = System.getenv("PROJECT_REPO")
    variable.let {
        return it.toString()
    }
}

fun cloneGitProject(repo: String): String {
    val command = "git clone $repo"
    val output = executeCli(command)
    return(output)
}

fun runDbt(arg: String): String {
    val command = "dbt $arg"
    val output = executeCli(command)
    return(output)
}

fun executeCli(command: String): String {
    fun String.runCommand(
        workingDir: File = File("."),
        timeoutAmount: Long = 60,
        timeoutUnit: TimeUnit = TimeUnit.SECONDS
    ): String? = runCatching {
        ProcessBuilder("\\s".toRegex().split(this))
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
            .inputStream.bufferedReader().readText()
    }.onFailure { it.printStackTrace() }.getOrNull()
    try {
        val output = command.runCommand()
        // should this go in the try block??
        output.let {
            return(it.toString())
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    // why would the bash not return anything??
    return ("No CLI Output")
}
