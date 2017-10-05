package b2boost.command

import b2boost.Partner
import grails.validation.Validateable
import org.springframework.validation.Errors

class PartnerCommand implements Validateable {
    Long id
    String name
    String reference
    String locale
    String expirationTime

    static constraints = {
        id nullable: true
        reference validator: { val, obj, Errors errors ->
            if (Partner.findByRef(val)) {
                errors.rejectValue('reference', 'reference.unique')
            }
        }
        locale validator: { val, obj, Errors errors ->
            try {
                val.encodeAsLocale()
            } catch (Exception e) {
                errors.rejectValue("locale", "locale.invalid", "value is not a valid Locale")
            }
        }
        expirationTime validator: { val, obj, Errors errors ->
            try {
                val.encodeAsDate()
            } catch (Exception e) {
                errors.rejectValue("expirationTime", "invalid.expirationTime", "value is not a valid expirationTime")
            }
        }
    }

    static PartnerCommand from(Partner partner) {
        if (!partner) return null

        PartnerCommand command = new PartnerCommand()
        command.id = partner.id
        command.name = partner.companyName
        command.reference = partner.ref
        command.locale = partner.locale.decodeLocale()
        command.expirationTime = partner.expires.decodeDate()
        command
    }
}
