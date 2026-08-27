package dev.gaphunter.cmakecompanion.detection

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.util.io.ByteSequence
import com.intellij.openapi.vfs.VirtualFile
import dev.gaphunter.cmakecompanion.lang.CMakeFileType
import java.nio.charset.StandardCharsets

/**
 * Content-based fallback for CMake files whose filename doesn't match
 * "CMakeLists.txt"/`*.cmake` (see [CMakeFileType.isMyFileType] for the
 * primary, higher-priority filename+content check). Registered as a
 * [FileTypeRegistry.FileTypeDetector] (lowest-priority tier) purely for
 * defense in depth -- reads bytes via [ByteSequence], never
 * `VirtualFile.contentsToByteArray()` (that specific method causes real
 * infinite recursion in a FileTypeOverrider; this class is a
 * FileTypeDetector, not a FileTypeOverrider, but the same
 * "never re-ask the platform what a file's FileType is" discipline
 * applies).
 */
class CMakeFileTypeOverrider : FileTypeRegistry.FileTypeDetector {

    override fun detect(file: VirtualFile, firstBytes: ByteSequence, firstCharsIfText: CharSequence?): FileType? {
        val sample = firstCharsIfText?.toString() ?: String(firstBytes.toBytes(), StandardCharsets.UTF_8)
        return if (CMakeFileDetector.isCMakeFile(file.name, sample)) CMakeFileType else null
    }

    @Deprecated("Overrides a deprecated platform member; still the correct hook for cache invalidation.")
    override fun getVersion(): Int = 1
}
