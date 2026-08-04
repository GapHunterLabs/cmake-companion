package dev.gaphunter.cmakecompanion.inspection

import dev.gaphunter.cmakecompanion.lang.CMakeLexer
import dev.gaphunter.cmakecompanion.lang.CMakeTokenTypes

data class ParenIssue(val offset: Int, val length: Int, val message: String)

/**
 * The "syntax inspection" half of this plugin's v1 scope (highlighting is
 * the other half, see CMakeCommandAnnotator): flags unmatched `(`/`)` in a
 * CMake script by replaying [CMakeLexer]'s token stream and tracking paren
 * depth. Deliberately a pure function over text (not a PsiElement walk) so
 * it's directly unit-testable with plain JUnit, no BasePlatformTestCase/
 * platform test fixture required -- [dev.gaphunter.cmakecompanion.highlighting.CMakeParenAnnotator]
 * is a thin wrapper that runs this once per file and turns the result into
 * annotations.
 */
object CMakeParenChecker {
    fun check(text: CharSequence): List<ParenIssue> {
        val lexer = CMakeLexer()
        lexer.start(text, 0, text.length, 0)
        val openOffsets = ArrayDeque<Int>()
        val issues = mutableListOf<ParenIssue>()

        while (true) {
            val type = lexer.tokenType ?: break
            when (type) {
                CMakeTokenTypes.LPAREN -> openOffsets.addLast(lexer.tokenStart)
                CMakeTokenTypes.RPAREN -> {
                    if (openOffsets.isEmpty()) {
                        issues.add(ParenIssue(lexer.tokenStart, 1, "Unmatched closing parenthesis"))
                    } else {
                        openOffsets.removeLast()
                    }
                }
                else -> {}
            }
            lexer.advance()
        }

        for (openOffset in openOffsets) {
            issues.add(ParenIssue(openOffset, 1, "Unmatched opening parenthesis"))
        }
        return issues.sortedBy { it.offset }
    }
}
