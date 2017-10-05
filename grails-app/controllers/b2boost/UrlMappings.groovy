package b2boost

class UrlMappings {

    static mappings = {
        delete "/$namespace/$controller/$id"(action:"delete")
        get "/$namespace/$controller"(action:"index")
        get "/$namespace/$controller/$id"(action:"show")
        post "/$namespace/$controller"(action:"save")
        put "/$namespace/$controller/$id"(action:"update")

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
