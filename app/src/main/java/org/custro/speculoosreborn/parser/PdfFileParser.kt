package org.custro.speculoosreborn.parser

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.custro.speculoosreborn.utils.bitmapToByteArray

class PdfFileParser(override val uri: Uri) : Parser {
    private val pdfFile = PDDocument.load(uri.toFile())
    private val headers: MutableList<COSName> = mutableListOf()
    override val size: Int
        get() = headers.size

    init {
        Log.d("PdfFileParser", "scheme: ${uri.scheme}")
        for (page in pdfFile.pages) {
            val res = page.resources
            for (name in res.xObjectNames) {
                if (res.isImageXObject(name)) {
                    headers.add(name)
                }
            }
        }
    }

    @Synchronized
    override fun at(index: Int): ByteArray {
        val imgObj = pdfFile.pages[index].resources.getXObject(headers[index]) as PDImageXObject
        return bitmapToByteArray(imgObj.image)
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        TODO("Not yet implemented")
    }

    override fun close() {
        pdfFile.close()
    }

    companion object {
        fun isSupported(uri: Uri) = uri.scheme == "file"
                && uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(pdf)$")) ?: false
    }
}

