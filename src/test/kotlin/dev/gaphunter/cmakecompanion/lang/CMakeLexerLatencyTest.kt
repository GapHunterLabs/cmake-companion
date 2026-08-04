package dev.gaphunter.cmakecompanion.lang

import dev.gaphunter.cmakecompanion.inspection.CMakeParenChecker
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Synthetic large-CMakeLists.txt latency check (NEXT_BATCH_PLAN.md's
 * required test for this plugin) -- the cited competitor complaint was
 * "makes the IDE unresponsive and is really slow"; this confirms the
 * hand-rolled lexer and the paren checker built on top of it are linear,
 * not accidentally quadratic, over a realistically large generated file.
 */
class CMakeLexerLatencyTest {
    @Test
    fun `tokenizing a large generated CMakeLists file does not hang`() {
        val sb = StringBuilder()
        sb.append("cmake_minimum_required(VERSION 3.20)\n")
        sb.append("project(GeneratedLatencyFixture LANGUAGES CXX)\n")
        val targetCount = 65_000
        for (i in 0 until targetCount) {
            sb.append("add_library(module_").append(i).append(" STATIC module_").append(i).append(".cpp)\n")
            sb.append("target_include_directories(module_").append(i)
                .append(" PUBLIC \${CMAKE_SOURCE_DIR}/include/module_").append(i).append(")\n")
            sb.append("target_link_libraries(module_").append(i).append(" PRIVATE common_utils)\n")
        }
        val largeFile = sb.toString()
        assertTrue("test fixture should exceed 10MB to be meaningful", largeFile.length > 10_000_000)

        val start = System.nanoTime()
        val lexer = CMakeLexer()
        lexer.start(largeFile, 0, largeFile.length, 0)
        var tokenCount = 0
        while (lexer.tokenType != null) {
            tokenCount++
            lexer.advance()
        }
        val issues = CMakeParenChecker.check(largeFile)
        val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0

        assertTrue("expected many tokens from a large file", tokenCount > targetCount)
        assertTrue("well-formed generated file should have no unmatched parens", issues.isEmpty())
        assertTrue(
            "lexing + paren-checking a >10MB CMakeLists.txt took ${elapsedSeconds}s -- too slow, likely quadratic behavior",
            elapsedSeconds < 30.0
        )
    }
}
