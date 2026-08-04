package dev.gaphunter.cmakecompanion.inspection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CMakeParenCheckerTest {
    @Test
    fun `balanced parens produce no issues`() {
        val issues = CMakeParenChecker.check("if(NOT (A AND B))\nendif()")
        assertTrue(issues.isEmpty())
    }

    @Test
    fun `flags a stray closing paren`() {
        val text = "message(hello))"
        val issues = CMakeParenChecker.check(text)
        assertEquals(1, issues.size)
        assertEquals("Unmatched closing parenthesis", issues[0].message)
        assertEquals(text.length - 1, issues[0].offset)
    }

    @Test
    fun `flags an unclosed opening paren`() {
        val text = "add_executable(myapp main.cpp"
        val issues = CMakeParenChecker.check(text)
        assertEquals(1, issues.size)
        assertEquals("Unmatched opening parenthesis", issues[0].message)
        assertEquals(text.indexOf('('), issues[0].offset)
    }

    @Test
    fun `does not flag parens inside quoted strings differently from real parens`() {
        // A paren-like character inside a string is still lexed as part of
        // the STRING token by CMakeLexer, not as a real LPAREN/RPAREN --
        // this confirms the checker rides on the lexer's token stream
        // rather than doing its own naive character scan.
        val issues = CMakeParenChecker.check("""message("unbalanced ( paren in a string")""")
        assertTrue(issues.isEmpty())
    }

    @Test
    fun `reports multiple unmatched opens in offset order`() {
        val text = "foreach(x IN LISTS y\n  if(x"
        val issues = CMakeParenChecker.check(text)
        assertEquals(2, issues.size)
        assertTrue(issues[0].offset < issues[1].offset)
    }
}
