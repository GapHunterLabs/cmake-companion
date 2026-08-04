package dev.gaphunter.cmakecompanion.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.gaphunter.cmakecompanion.inspection.CMakeParenChecker

/**
 * Runs [CMakeParenChecker] once per file (guarded on `element is PsiFile`,
 * since the platform calls every registered Annotator once per PSI
 * element and this check is whole-file, not per-token) and turns each
 * unmatched paren into a real error annotation.
 */
class CMakeParenAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiFile) return
        for (issue in CMakeParenChecker.check(element.text)) {
            holder.newAnnotation(HighlightSeverity.ERROR, issue.message)
                .range(TextRange(issue.offset, issue.offset + issue.length))
                .create()
        }
    }
}
