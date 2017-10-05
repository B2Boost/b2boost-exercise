package b2boost

import b2boost.commands.PartnerCommand
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class PartnersOldController {

    PartnerService partnerService

    static namespace = "api"
    static responseFormats = ['json']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(ListCommand command) {
        respond partnerService.list(command)
    }

    def show(Long id) {
        respond partnerService.get(id)
    }

    def save(PartnerCommand command) {
        if (command == null) {
            render status: NOT_FOUND
            return
        }

        try {
            partnerService.save(command)
        } catch (ValidationException e) {
            respond command.errors, view: 'create'
            return
        }

        respond command, [status: CREATED, view: "show"]
    }

    def update(PartnerCommand command, Long id) {
        if (command == null) {
            render status: NOT_FOUND
            return
        }

        try {
            partnerService.save(command, id)
        } catch (ValidationException e) {
            respond command.errors, view: 'edit'
            return
        }

        respond command, [status: OK, view: "show"]
    }

    def delete(Long id) {
        if (id == null) {
            render status: NOT_FOUND
            return
        }

        partnerService.delete(id)

        render status: NO_CONTENT
    }
}
