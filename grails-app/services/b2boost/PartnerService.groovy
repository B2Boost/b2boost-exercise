package b2boost

import b2boost.commands.PartnerCommand
import exception.ObjectNotFoundException
import grails.gorm.transactions.Transactional
import grails.validation.ValidationException

@Transactional
class PartnerService {

    List<PartnerCommand> list(ListCommand command) {
        throwExceptionIfCommandHasErrors(command)
        Partner.list(max: command.size, offset: command.from).collect { PartnerCommand.from(it) }
    }

    PartnerCommand get(Long id) {
        throwExceptionIfPartnerNotExists(id)
        PartnerCommand.from(Partner.get(id))
    }

    PartnerCommand save(PartnerCommand command, Long id = null) {
        command = validateCommand(command, id)
        throwExceptionIfCommandHasErrors(command)
        Partner partner = createPartnerFromCommand(command, id).save()
        command.id = partner.id
        command
    }

    void delete(Long id) {
        throwExceptionIfPartnerNotExists(id)
        Partner.get(id).delete()
    }

    boolean exists(Long id) {
        Partner.exists(id)
    }

    private PartnerCommand validateCommand(PartnerCommand command, Long id) {
        if (!command) {
            command = new PartnerCommand()
            command.errors.reject('empty.partner', 'No values to store')
        }
        throwExceptionIfPartnerNotExists(id)
        command
    }

    private void throwExceptionIfPartnerNotExists(Long id) {
        if (id && !exists(id)) {
            throw new ObjectNotFoundException("Partner with id $id not found")
        }
    }

    private void throwExceptionIfCommandHasErrors(command) {
        if (command.hasErrors()) {
            throw new ValidationException('', command.errors)
        }
    }

    private Partner createPartnerFromCommand(PartnerCommand command, Long id) {
        Partner partner = new Partner()
//        partner.properties = command.properties
        partner.id = id
        partner.companyName = command.name
        partner.ref = command.reference
        partner.locale = command.locale.encodeAsLocale()
        partner.expires = command.expirationTime.encodeAsDate()
        partner
    }
}
