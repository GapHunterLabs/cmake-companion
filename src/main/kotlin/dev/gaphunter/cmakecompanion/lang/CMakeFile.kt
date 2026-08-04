package dev.gaphunter.cmakecompanion.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class CMakeFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CMakeLanguage) {
    override fun getFileType() = CMakeFileType
    override fun toString(): String = "CMake File"
}
