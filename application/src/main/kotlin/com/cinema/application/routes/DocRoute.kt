package com.cinema.application.routes

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.docRoute() {
    route("docs/") {
        get("/openapi.json") {
            val jsonData = this.javaClass.getResource("/doc/openapi.json")
                .readText()
                .replace("\n", "")
            call.respond(jsonData)
        }
        get("static") {
            val icon = this.javaClass.getResource("/doc/cinema-icon.jpeg").readBytes()
            call.respondBytes(icon, ContentType.Image.JPEG)
        }
        get("/swagger") {
            call.respondRedirect("/swagger-ui/index.html?url=/docs/openapi.json", true)
        }
        get("/redoc") {
            call.respondText(ContentType.Text.Html) {
                """
    <!DOCTYPE html>
    <html>
      <head>
        <title>Cinema Service API Reference</title>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://fonts.googleapis.com/css?family=Montserrat:300,400,700|Roboto:300,400,700" rel="stylesheet">
     </head>
      <body>
        <redoc spec-url='/docs/openapi.json' suppress-warnings="true" lazy-rendering></redoc>
        <script src="https://cdn.jsdelivr.net/npm/redoc@next/bundles/redoc.standalone.js"> </script>
      </body>
    </html>
    """
            }
        }
    }
}
