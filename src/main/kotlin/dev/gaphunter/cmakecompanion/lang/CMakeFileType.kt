package dev.gaphunter.cmakecompanion.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.vfs.VirtualFile
import dev.gaphunter.cmakecompanion.detection.CMakeFileDetector
import java.nio.charset.StandardCharsets
import javax.swing.Icon

/**
 * Implements [FileTypeIdentifiableByVirtualFile] in addition to being a
 * plain [LanguageFileType] -- proactively applying the fix
 * nginx-companion needed after a real 5-round investigation: a bundled
 * `FileTypeIdentifiableByVirtualFile`
 * (e.g. TextMate, if it ships a CMake grammar) is consulted first and
 * wins any tie by registration order alone, regardless of specificity.
 * Implementing this interface AND `order="first"` on the `<fileType>`
 * registration in plugin.xml (both required together, neither alone is
 * enough -- confirmed the hard way for nginx-companion) settles the race
 * in this plugin's favor from the first version, instead of rediscovering
 * the same silent "highlighting never appears" bug from scratch.
 */
object CMakeFileType : LanguageFileType(CMakeLanguage), FileTypeIdentifiableByVirtualFile {
    override fun getName(): String = "CMake"
    override fun getDescription(): String = "CMake script"
    override fun getDefaultExtension(): String = "cmake"
    override fun getIcon(): Icon? = null

    /**
     * Cheap on purpose: only reads a bounded prefix of the file, same
     * content-sampling approach [dev.gaphunter.cmakecompanion.detection.CMakeFileTypeOverrider]
     * uses -- this method is called frequently by the platform (per the
     * interface's own JavaDoc), so no full-file read, no PSI access, no
     * indices.
     */
    override fun isMyFileType(file: VirtualFile): Boolean {
        val sampleBytes = try {
            file.inputStream.use { it.readNBytes(SAMPLE_BYTE_LIMIT) }
        } catch (e: Exception) {
            return false
        }
        val sample = String(sampleBytes, StandardCharsets.UTF_8)
        return CMakeFileDetector.isCMakeFile(file.name, sample)
    }

    private const val SAMPLE_BYTE_LIMIT = 8192
}
