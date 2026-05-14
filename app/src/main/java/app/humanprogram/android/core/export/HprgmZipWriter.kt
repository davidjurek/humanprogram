package app.humanprogram.android.core.export

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class HprgmZipWriter {
    fun write(
        exportPackage: HprgmExportPackage,
        outputStream: OutputStream
    ) {
        ZipOutputStream(outputStream).use { zip ->
            exportPackage.files.toSortedMap().forEach { (path, content) ->
                zip.putNextEntry(ZipEntry(path))
                zip.write(content.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        }
    }
}
