package b2boost

class Partner {

    String companyName
    String ref
    Locale locale
//    LocalDateTime expires
    Date expires

    static constraints = {
        ref unique: true
    }
}
