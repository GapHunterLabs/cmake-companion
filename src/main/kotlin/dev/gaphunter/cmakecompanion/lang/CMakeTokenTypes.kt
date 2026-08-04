package dev.gaphunter.cmakecompanion.lang

import com.intellij.psi.tree.IElementType

class CMakeTokenType(debugName: String) : IElementType(debugName, CMakeLanguage)

object CMakeTokenTypes {
    val WHITESPACE = CMakeTokenType("CMAKE_WHITESPACE")
    val COMMENT = CMakeTokenType("CMAKE_COMMENT")
    val STRING = CMakeTokenType("CMAKE_STRING")
    val VARIABLE = CMakeTokenType("CMAKE_VARIABLE")
    val LPAREN = CMakeTokenType("CMAKE_LPAREN")
    val RPAREN = CMakeTokenType("CMAKE_RPAREN")
    val WORD = CMakeTokenType("CMAKE_WORD")
    val BAD_CHARACTER = CMakeTokenType("CMAKE_BAD_CHARACTER")
}
