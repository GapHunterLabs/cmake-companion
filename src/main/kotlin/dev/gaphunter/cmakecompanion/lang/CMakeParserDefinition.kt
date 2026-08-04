package dev.gaphunter.cmakecompanion.lang

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

/**
 * Deliberately flat, same call as NginxParserDefinition: every token is a
 * direct leaf of the file root, no nested command/argument-list nodes. A
 * full CMake grammar (matching `(`/`)` pairs into an argument-list node,
 * distinguishing command name from arguments structurally) isn't needed
 * for v1's feature set (lexer-based highlighting, known-command
 * annotation, unmatched-paren inspection -- see CMakeParenChecker, which
 * does its own bracket matching over the flat token stream instead of
 * relying on the parse tree for that).
 */
class CMakeParserDefinition : ParserDefinition {

    companion object {
        val FILE = IFileElementType(CMakeLanguage)
    }

    override fun createLexer(project: Project): Lexer = CMakeLexer()

    override fun createParser(project: Project): PsiParser = PsiParser { root, builder ->
        val marker = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        marker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = TokenSet.create(CMakeTokenTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet = TokenSet.create(CMakeTokenTypes.STRING)

    override fun getWhitespaceTokens(): TokenSet =
        TokenSet.create(CMakeTokenTypes.WHITESPACE, TokenType.WHITE_SPACE)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = CMakeFile(viewProvider)

    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)
}
