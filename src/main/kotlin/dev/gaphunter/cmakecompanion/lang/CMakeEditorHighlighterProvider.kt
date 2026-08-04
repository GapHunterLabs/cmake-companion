package dev.gaphunter.cmakecompanion.lang

import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.fileTypes.EditorHighlighterProvider
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Explicit editor highlighter wiring, same reasoning as
 * NginxEditorHighlighterProvider: `lang.syntaxHighlighterFactory` alone is
 * documented to be enough for a `LanguageFileType`, but nginx-companion's
 * investigation found the automatic path never invoked its
 * SyntaxHighlighterFactory for files resolved through a content-based
 * FileTypeDetector/Overrider -- registering this provider explicitly
 * bypasses whatever in that automatic resolution was short-circuiting.
 * Applied here from the start rather than rediscovered.
 */
class CMakeEditorHighlighterProvider : EditorHighlighterProvider {
    override fun getEditorHighlighter(
        project: Project?,
        fileType: FileType,
        virtualFile: VirtualFile?,
        colors: EditorColorsScheme
    ): EditorHighlighter = LexerEditorHighlighter(CMakeSyntaxHighlighter(), colors)
}
