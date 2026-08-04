package dev.gaphunter.cmakecompanion.lang

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

object CMakeHighlighterColors {
    val COMMENT: TextAttributesKey =
        createTextAttributesKey("CMAKE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val STRING: TextAttributesKey =
        createTextAttributesKey("CMAKE_STRING", DefaultLanguageHighlighterColors.STRING)
    val VARIABLE: TextAttributesKey =
        createTextAttributesKey("CMAKE_VARIABLE", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
    val PARENS: TextAttributesKey =
        createTextAttributesKey("CMAKE_PARENS", DefaultLanguageHighlighterColors.PARENTHESES)
    val KNOWN_COMMAND: TextAttributesKey =
        createTextAttributesKey("CMAKE_KNOWN_COMMAND", DefaultLanguageHighlighterColors.KEYWORD)
    val BAD_CHARACTER: TextAttributesKey =
        createTextAttributesKey("CMAKE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
}

class CMakeSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = CMakeLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        val key = when (tokenType) {
            CMakeTokenTypes.COMMENT -> CMakeHighlighterColors.COMMENT
            CMakeTokenTypes.STRING -> CMakeHighlighterColors.STRING
            CMakeTokenTypes.VARIABLE -> CMakeHighlighterColors.VARIABLE
            CMakeTokenTypes.LPAREN, CMakeTokenTypes.RPAREN -> CMakeHighlighterColors.PARENS
            CMakeTokenTypes.BAD_CHARACTER -> CMakeHighlighterColors.BAD_CHARACTER
            else -> return emptyArray()
        }
        return arrayOf(key)
    }
}

class CMakeSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = CMakeSyntaxHighlighter()
}
