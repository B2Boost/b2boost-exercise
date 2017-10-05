package b2boost

class BootStrap {

    def init = { servletContext ->
        environments {
            development {
                20.times {
                    Partner.findByRef("xxx$it") ?: new Partner(companyName: 'testCompany', ref: "xxx$it", locale: Locale.UK, expires: new Date().plus(10)).save(flush: true)
                }
            }
        }
    }
    def destroy = {
    }
}
