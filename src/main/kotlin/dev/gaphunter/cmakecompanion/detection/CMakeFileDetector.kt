package dev.gaphunter.cmakecompanion.detection

/**
 * Decides whether a file is really a CMake script, from filename + a
 * content sample. "CMakeLists.txt" and `*.cmake` are unambiguous by
 * filename alone; the content sniff exists for unusually named included
 * modules (e.g. a project's own "Toolchain.txt") that don't match either
 * pattern but still start with real CMake commands.
 */
object CMakeFileDetector {

    private val CONTENT_HINTS = listOf(
        "cmake_minimum_required", "project(", "add_executable(", "add_library(",
        "add_subdirectory(", "find_package(", "target_link_libraries(",
        "target_include_directories(", "include(", "set(",
    )

    fun isCMakeFile(fileName: String, contentSample: String): Boolean {
        val lowerName = fileName.lowercase()
        if (lowerName == "cmakelists.txt") return true
        if (lowerName.endsWith(".cmake")) return true

        val lower = contentSample.lowercase()
        // Two independent hits is a stronger signal than one -- avoids
        // false-positiving on, say, a shell script that happens to mention
        // "include(" once in a comment.
        return CONTENT_HINTS.count { lower.contains(it) } >= 2
    }
}
