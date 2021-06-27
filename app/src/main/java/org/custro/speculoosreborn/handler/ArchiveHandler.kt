package org.custro.speculoosreborn.handler

import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import org.custro.speculoosreborn.parser.Parser

class ArchiveHandler(parser: Parser): RequestHandler() {
    private var mParser: Parser = parser
    override fun canHandleRequest(data: Request?): Boolean {
        return data?.uri?.scheme?.equals("localcomic") ?: false
    }

    override fun load(request: Request?, networkPolicy: Int): Result? {
        return null
    }
}