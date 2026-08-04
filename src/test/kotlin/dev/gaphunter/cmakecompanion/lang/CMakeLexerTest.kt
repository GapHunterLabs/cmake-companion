package dev.gaphunter.cmakecompanion.lang

import com.intellij.psi.tree.IElementType
import org.junit.Assert.assertEquals
import org.junit.Test

class CMakeLexerTest {

    private fun tokenize(text: String): List<Pair<IElementType, String>> {
        val lexer = CMakeLexer()
        lexer.start(text, 0, text.length, 0)
        val result = mutableListOf<Pair<IElementType, String>>()
        while (true) {
            val type = lexer.tokenType ?: break
            result.add(type to text.substring(lexer.tokenStart, lexer.tokenEnd))
            lexer.advance()
        }
        return result
    }

    private fun nonWhitespace(text: String) = tokenize(text).filter { it.first != CMakeTokenTypes.WHITESPACE }

    @Test
    fun simpleCommandInvocation() {
        val tokens = nonWhitespace("add_executable(myapp main.cpp)")
        assertEquals(
            listOf(
                CMakeTokenTypes.WORD to "add_executable",
                CMakeTokenTypes.LPAREN to "(",
                CMakeTokenTypes.WORD to "myapp",
                CMakeTokenTypes.WORD to "main.cpp",
                CMakeTokenTypes.RPAREN to ")",
            ),
            tokens,
        )
    }

    @Test
    fun commentRunsToEndOfLineOnly() {
        val tokens = nonWhitespace("# a comment\nproject(Foo)")
        assertEquals(CMakeTokenTypes.COMMENT, tokens[0].first)
        assertEquals("# a comment", tokens[0].second)
        assertEquals(CMakeTokenTypes.WORD to "project", tokens[1])
    }

    @Test
    fun doubleQuotedStringWithEscapedQuote() {
        val tokens = nonWhitespace("""message(STATUS "a \"quoted\" value")""")
        val string = tokens.first { it.first == CMakeTokenTypes.STRING }
        assertEquals(""""a \"quoted\" value"""", string.second)
    }

    @Test
    fun quotedStringCanSpanMultipleLines() {
        val tokens = nonWhitespace("set(X \"line one\nline two\")")
        val string = tokens.first { it.first == CMakeTokenTypes.STRING }
        assertEquals("\"line one\nline two\"", string.second)
    }

    @Test
    fun simpleVariable() {
        val tokens = nonWhitespace("message(\${MY_VAR})")
        assertEquals(CMakeTokenTypes.VARIABLE to "\${MY_VAR}", tokens[2])
    }

    @Test
    fun envAndCacheVariables() {
        val tokens = nonWhitespace("message(\$ENV{PATH} \$CACHE{BUILD_TYPE})")
        val variables = tokens.filter { it.first == CMakeTokenTypes.VARIABLE }
        assertEquals(listOf("\$ENV{PATH}", "\$CACHE{BUILD_TYPE}"), variables.map { it.second })
    }

    @Test
    fun variableEmbeddedInUnquotedArgument() {
        val tokens = nonWhitespace("set(X foo_\${VAR}_bar)")
        assertEquals(
            listOf(
                CMakeTokenTypes.WORD to "set",
                CMakeTokenTypes.LPAREN to "(",
                CMakeTokenTypes.WORD to "X",
                CMakeTokenTypes.WORD to "foo_",
                CMakeTokenTypes.VARIABLE to "\${VAR}",
                CMakeTokenTypes.WORD to "_bar",
                CMakeTokenTypes.RPAREN to ")",
            ),
            tokens,
        )
    }

    @Test
    fun emptyInputProducesNoTokens() {
        assertEquals(emptyList<Pair<IElementType, String>>(), tokenize(""))
    }

    @Test
    fun nestedParens() {
        val tokens = nonWhitespace("if(NOT (A AND B))")
        assertEquals(CMakeTokenTypes.LPAREN, tokens[1].first)
        assertEquals(CMakeTokenTypes.LPAREN, tokens[3].first)
        assertEquals(CMakeTokenTypes.RPAREN, tokens[tokens.size - 2].first)
        assertEquals(CMakeTokenTypes.RPAREN, tokens.last().first)
    }
}
