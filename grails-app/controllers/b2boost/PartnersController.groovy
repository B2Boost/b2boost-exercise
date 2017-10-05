package b2boost

import b2boost.commands.PartnerCommand
import exception.ObjectNotFoundException
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class PartnersController {

    PartnerService partnerService

    static namespace = "api"
    static responseFormats = ['json']
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def index(ListCommand command) {
        try {
            respond partnerService.list(command)
        } catch (Exception e) {
            respond([code: INTERNAL_SERVER_ERROR.value(), message: e.message], status: INTERNAL_SERVER_ERROR)
        }
    }

    def show(Long id) {
        try {
            respond partnerService.get(id)
        } catch (ObjectNotFoundException e) {
            respond([code: NOT_FOUND.value(), message: e.message], status: NOT_FOUND)
        }
    }

    def save(PartnerCommand partner) {
        try {
            partnerService.save(partner)
        } catch (ValidationException e) {
            respond([code: BAD_REQUEST.value(), message: e.message], status: BAD_REQUEST)
        } catch (Exception e) {
            respond([code: INTERNAL_SERVER_ERROR.value(), message: e.message], status: INTERNAL_SERVER_ERROR)
        }

        respond partner, [status: CREATED]
    }

    def update(PartnerCommand partner, Long id) {
        try {
            partnerService.save(partner, id)
        } catch (ValidationException e) {
            respond([code: BAD_REQUEST.value(), message: e.message], status: BAD_REQUEST)
        } catch (ObjectNotFoundException e) {
            respond([code: NOT_FOUND.value(), message: e.message], status: NOT_FOUND)
        } catch (Exception e) {
            respond([code: INTERNAL_SERVER_ERROR.value(), message: e.message], status: INTERNAL_SERVER_ERROR)
        }

        respond partner, [status: OK]
    }

    def delete(Long id) {
        try {
            partnerService.delete(id)
        } catch (ObjectNotFoundException e) {
            respond([code: NOT_FOUND.value(), message: e.message], status: NOT_FOUND)
        } catch (Exception e) {
            respond([code: INTERNAL_SERVER_ERROR.value(), message: e.message], status: INTERNAL_SERVER_ERROR)
        }

        render status: OK
    }

//    def handleValidationErrors(ValidationException e) {
//        respond([code: BAD_REQUEST.value(), message: e.message], status: BAD_REQUEST)
//    }
//
//    def handleNotFoundErrors(ObjectNotFoundException e) {
//        respond([code: NOT_FOUND.value(), message: e.message], status: NOT_FOUND)
//    }
//
//    def handleInternalErrors(Exception e) {
//        if (e.class != ValidationException.class && e.class != ObjectNotFoundException.class) {
//            respond([code: INTERNAL_SERVER_ERROR.value(), message: e.message], status: INTERNAL_SERVER_ERROR)
//        }
//    }
}

class ListCommand {
    Integer from = 0
    Integer size = 10
}
