import org.apache.commons.lang.LocaleUtils

class LocaleCodec {
    static encode = { theTarget ->
        LocaleUtils.toLocale(theTarget.toString())
    }

    static decode = { theTarget ->
        theTarget.toString()
    }
}
