package dev.gaphunter.cmakecompanion.lang

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * Hand-rolled lexer for CMake script syntax: `# comments`, double-quoted
 * strings, `${VAR}`/`$ENV{VAR}`/`$CACHE{VAR}` variable references,
 * parentheses, and everything else as a bare WORD (command names and
 * command arguments are lexically indistinguishable without a command
 * catalog -- same split of responsibility as NginxLexer: the lexer just
 * tokenizes, [dev.gaphunter.cmakecompanion.highlighting.CMakeCommandAnnotator]
 * decides which WORDs are real commands).
 *
 * v1 scope cut: CMake's bracket comments (`#[[...]]`) and bracket
 * arguments (`[[...]]`) are not handled -- both are rare in real-world
 * CMakeLists.txt files compared to `#` line comments and quoted/unquoted
 * arguments, and skipping them keeps this lexer as small and stable as
 * NginxLexer/GraphqlLexer (same "don't promise more than a hand-rolled
 * subset delivers correctly" scoping call as elsewhere in this workspace).
 * A `[[` is simply lexed as two WORD-adjacent tokens, not a syntax error.
 */
class CMakeLexer : LexerBase() {

    private lateinit var buffer: CharSequence
    private var startOffset = 0
    private var endOffset = 0

    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        tokenStart = startOffset
        tokenEnd = startOffset
        locateToken()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd
        locateToken()
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset

    private fun locateToken() {
        if (tokenStart >= endOffset) {
            tokenType = null
            tokenEnd = tokenStart
            return
        }
        val c = buffer[tokenStart]
        when {
            c.isWhitespace() -> {
                tokenType = CMakeTokenTypes.WHITESPACE
                tokenEnd = scanWhile(tokenStart) { it.isWhitespace() }
            }
            c == '#' -> {
                tokenType = CMakeTokenTypes.COMMENT
                tokenEnd = scanWhile(tokenStart + 1) { it != '\n' }
            }
            c == '"' -> {
                tokenType = CMakeTokenTypes.STRING
                tokenEnd = scanQuotedString(tokenStart)
            }
            c == '$' -> {
                tokenType = CMakeTokenTypes.VARIABLE
                tokenEnd = scanVariable(tokenStart)
            }
            c == '(' -> {
                tokenType = CMakeTokenTypes.LPAREN
                tokenEnd = tokenStart + 1
            }
            c == ')' -> {
                tokenType = CMakeTokenTypes.RPAREN
                tokenEnd = tokenStart + 1
            }
            else -> {
                tokenType = CMakeTokenTypes.WORD
                tokenEnd = scanWhile(tokenStart) { !isSpecial(it) }
            }
        }
        if (tokenEnd <= tokenStart) {
            // Safety net: never emit a zero-length token (would infinite-loop
            // the platform's lexer-consistency checks).
            tokenType = CMakeTokenTypes.BAD_CHARACTER
            tokenEnd = tokenStart + 1
        }
    }

    private fun isSpecial(c: Char): Boolean =
        c.isWhitespace() || c == '(' || c == ')' || c == '"' || c == '$' || c == '#'

    private fun scanWhile(from: Int, predicate: (Char) -> Boolean): Int {
        var i = from
        while (i < endOffset && predicate(buffer[i])) i++
        return i
    }

    private fun scanQuotedString(from: Int): Int {
        var i = from + 1
        while (i < endOffset) {
            val c = buffer[i]
            if (c == '\\' && i + 1 < endOffset) {
                i += 2
                continue
            }
            if (c == '"') {
                return i + 1
            }
            i++
        }
        // Unterminated string runs to end of buffer -- CMake strings CAN
        // legitimately span multiple lines (unlike nginx's), so there is no
        // safe "stop at newline" fallback here.
        return i
    }

    /** `${VAR}`, `$ENV{VAR}`, `$CACHE{VAR}`. A bare `$` not followed by one
     * of these forms is rare in real CMake and is emitted as a 1-char
     * VARIABLE token -- harmless, just colored like any other variable. */
    private fun scanVariable(from: Int): Int {
        var i = from + 1
        for (prefix in arrayOf("ENV{", "CACHE{")) {
            if (matches(i, prefix)) {
                return scanToClosingBrace(i + prefix.length)
            }
        }
        if (i < endOffset && buffer[i] == '{') {
            return scanToClosingBrace(i + 1)
        }
        return i
    }

    private fun matches(from: Int, literal: String): Boolean {
        if (from + literal.length > endOffset) return false
        for (offset in literal.indices) {
            if (buffer[from + offset] != literal[offset]) return false
        }
        return true
    }

    private fun scanToClosingBrace(from: Int): Int {
        var i = from
        while (i < endOffset && buffer[i] != '}') i++
        return if (i < endOffset) i + 1 else i
    }
}
