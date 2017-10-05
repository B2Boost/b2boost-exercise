package b2boost

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Ignore
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.*

@Integration
@Rollback
class PartnersFunctionalSpec extends GebSpec {

    def setup() {
        30.times {
            new Partner(companyName: 'testCompany', ref: "xxx$it", locale: Locale.UK, expires: new Date().plus(10)).save()
        }
    }

    RestBuilder getRestBuilder() {
        new RestBuilder()
    }

    String getResourcePath() {
        "${baseUrl}/api/partners"
    }

    Closure getValidJson() {
        { ->
            name = 'Bells & Whistles'
            reference = UUID.randomUUID().toString()
            locale = "en_GB"
            expirationTime = '2017-11-03T12:18:46+00:00'
            // or to make to make it future proof: new Date().plus(10).decodeDate()
        }
    }

    Closure getInvalidJson() {
        { ->
            name = ''
            reference = ''
            locale = "very-wrong-locale"
            expirationTime = 'totally not a valid date'
        }
    }

    @Unroll(" index action returns #message of items")
    void "Test the index action returns correct list of results"() {
        when: "The index action is requested"
        def response = restBuilder.get("$resourcePath$parameters")

        then: "The response is correct"
        response.status == OK.value()
        response.json.size() == size
        response.json[0].id == ++from

        where:
        parameters         | from | size | message
        ""                 | 0    | 10   | "default start and number"
        "?size=20"         | 0    | 20   | "bigger number"
        "?from=10"         | 10   | 10   | "modified start"
        "?from=10&size=20" | 10   | 20   | "modified start and number"
        "?size=40"         | 0    | 30   | "maximum number possible"
    }

    void "Test the index action returns correct properties for the objects"() {
        when: "The index action is requested"
        def response = restBuilder.get("$resourcePath")

        then: "The response is correct"
        response.status == OK.value()
        response.json[0].id
        response.json[0].name
        response.json[0].reference
        response.json[0].locale
        response.json[0].expirationTime
    }

    void "Test the index action returns a correct object in case of error"() {
        when: "The index action is requested"
        def response = restBuilder.get("$resourcePath?from=something")

        then: "The response is correct"
        response.status == INTERNAL_SERVER_ERROR.value()
        response.json.code == INTERNAL_SERVER_ERROR.value()
        response.json.message
    }

    void "Test the save action correctly persists an instance"() {
        when: "The save action is executed with no content"
        def response = restBuilder.post(resourcePath)

        then: "The response is correct"
        response.status == BAD_REQUEST.value()
        response.json.code == BAD_REQUEST.value()
        response.json.message

        when: "The save action is executed with invalid data"
        response = restBuilder.post(resourcePath) {
            json invalidJson
        }
        then: "The response is correct"
        response.status == BAD_REQUEST.value()
        response.json.code == BAD_REQUEST.value()
        response.json.message

        when: "The save action is executed with valid data"
        response = restBuilder.post(resourcePath) {
            json validJson
        }

        then: "The response is correct"
        response.status == CREATED.value()
        response.json.id
    }

    @Unroll
    void "Test a save fails for each specific validation error"() {
        when: "The save action is executed with invalid data"
        def response = restBuilder.post(resourcePath) {
            json {
                name = nameTest
                reference = referenceTest
                locale = localeTest
                expirationTime = expirationTimeTest
            }
        }

        then: "The response is correct"
        response.status == BAD_REQUEST.value()
        response.json.code == BAD_REQUEST.value()
        response.json.message

        where:
        nameTest           | referenceTest                | localeTest          | expirationTimeTest
        ''               | UUID.randomUUID().toString() | "en_GB"             | '2017-11-03T12:18:46+00:00'
        'Bells & Whistles' | 'xxx1'                       | "en_GB"             | '2017-11-03T12:18:46+00:00'
        'Bells & Whistles' | UUID.randomUUID().toString() | "some weird locale" | '2017-11-03T12:18:46+00:00'
        'Bells & Whistles' | UUID.randomUUID().toString() | "en_GB"             | 'totally not a date'
    }

    @Ignore("TODO: allow a condition to trigger an internal server error")
    void "Test the save action returns the correct response in case of an internal error"() {
        when: "The save action is executed with a problem"
        def response = restBuilder.post(resourcePath)

        then: "The response is correct"
        response.status == INTERNAL_SERVER_ERROR.value()
        response.json.code == INTERNAL_SERVER_ERROR.value()
        response.json.message
    }

    void "Test the update action correctly updates an instance"() {
        when: "The save action is executed with valid data"
        def response = restBuilder.post(resourcePath) {
            json validJson
        }

        then: "The response is correct"
        response.status == CREATED.value()
        response.json.id

        when: "The update action is called with invalid data"
        def id = response.json.id
        response = restBuilder.put("$resourcePath/$id") {
            json invalidJson
        }

        then: "The response is correct"
        response.status == BAD_REQUEST.value()
        response.json.code == BAD_REQUEST.value()
        response.json.message

        when: "The update action is called with valid data"
        response = restBuilder.put("$resourcePath/$id") {
            json validJson
        }

        then: "The response is correct"
        response.status == OK.value()
        response.json
    }

    @Ignore("TODO: allow a condition to trigger an internal server error")
    void "Test the update action returns the correct response in case of an internal error"() {
        when: "The update action is executed with a problem"
        def response = restBuilder.put(resourcePath) {
            json {
                id = 'something'
                name = 'Bells & Whistles'
                reference = UUID.randomUUID().toString()
                locale = "en_GB"
                expirationTime = '2017-11-03T12:18:46+00:00'
            }
        }

        then: "The response is correct"
        response.status == INTERNAL_SERVER_ERROR.value()
        response.json.code == INTERNAL_SERVER_ERROR.value()
        response.json.message
    }

    void "Test the update action returns the correct response in case of not finding the object"() {
        when: "The update action is executed with a problem"
        def response = restBuilder.put(resourcePath) {
            json {
                id = 999999
                name = 'Bells & Whistles'
                reference = UUID.randomUUID().toString()
                locale = "en_GB"
                expirationTime = new Date().plus(10).decodeDate()
            }
        }

        then: "The response is correct"
        response.status == NOT_FOUND.value()
        response.json.code == NOT_FOUND.value()
        response.json.message
    }

    void "Test the show action correctly renders an instance"() {
        when: "The show action is called to retrieve a non existent resource"
        def response = restBuilder.get("$resourcePath/99999")

        then: "The response is correct"
        response.status == NOT_FOUND.value()
        response.json.code == NOT_FOUND.value()
        response.json.message

        when: "The save action is executed with valid data"
        response = restBuilder.post(resourcePath) {
            json validJson
        }

        then: "The response is correct"
        response.status == CREATED.value()
        response.json.id

        when: "When the show action is called to retrieve a resource"
        def id = response.json.id
        response = restBuilder.get("$resourcePath/$id")

        then: "The response is correct"
        response.status == OK.value()
        response.json.id == id
    }


    void "Test the delete action correctly deletes an instance"() {
        when: "The save action is executed with valid data"
        def response = restBuilder.post(resourcePath) {
            json validJson
        }

        then: "The response is correct"
        response.status == CREATED.value()
        response.json.id

        when: "When the delete action is executed on an unknown instance"
        def id = response.json.id
        response = restBuilder.delete("$resourcePath/99999")

        then: "The response is correct"
        response.status == NOT_FOUND.value()

        when: "When the delete action is executed on an existing instance"
        response = restBuilder.delete("$resourcePath/$id")

        then: "The response is correct"
        response.status == OK.value()
        !Partner.exists(id)
    }

}