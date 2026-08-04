package dev.gaphunter.cmakecompanion.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import dev.gaphunter.cmakecompanion.commands.CMakeCommandIndex
import dev.gaphunter.cmakecompanion.lang.CMakeHighlighterColors
import dev.gaphunter.cmakecompanion.lang.CMakeTokenTypes

/**
 * Colors a WORD token as a known command only when its text is a real
 * CMake command name from the bundled catalog AND it's immediately
 * followed (skipping whitespace) by "(" -- command names and ordinary
 * arguments are lexically identical WORD tokens (see CMakeLexer), and a
 * bare WORD matching a command name that ISN'T being invoked (e.g. it's
 * just an argument value, or a CMake variable happens to be named after a
 * command) shouldn't be highlighted as a command. Same pattern as
 * NginxDirectiveAnnotator.
 */
class CMakeCommandAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.node?.elementType != CMakeTokenTypes.WORD) return
        if (!CMakeCommandIndex.isKnownCommand(element.text)) return
        if (!isFollowedByOpenParen(element)) return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(CMakeHighlighterColors.KNOWN_COMMAND)
            .range(element.textRange)
            .create()
    }

    private fun isFollowedByOpenParen(element: PsiElement): Boolean {
        var sibling = element.nextSibling
        while (sibling is PsiWhiteSpace) sibling = sibling.nextSibling
        return sibling?.node?.elementType == CMakeTokenTypes.LPAREN
    }
}
