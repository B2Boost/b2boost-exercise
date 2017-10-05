package exception

import org.grails.core.exceptions.GrailsException

class ObjectNotFoundException extends GrailsException {

    ObjectNotFoundException(String msg) {
        super(msg)
    }
}
