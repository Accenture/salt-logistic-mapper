SCE Mapper
==========

Mapper Service

A service to mapping edifact or csv files to transport and PaketCSV objects using smooks library.
It uses akka builtin server.

# API Docs
* Floating Version: [https://swagger-ui.apps.d4s.salt-solutions.de/?url=https://cors-anywhere.apps.d4s.salt-solutions.de/https://swugit1.salt-solutions.de/sce-public/api-doc/-/raw/master/sce/mapper/x.y.z.yml](https://swagger-ui.apps.d4s.salt-solutions.de/?url=https://cors-anywhere.apps.d4s.salt-solutions.de/https://swugit1.salt-solutions.de/sce-public/api-doc/-/raw/master/sce/mapper/x.y.z.yml) - x.y.z needs to be substituted with the desired version
* Latest Version: [https://swagger-ui.apps.d4s.salt-solutions.de/?url=https://cors-anywhere.apps.d4s.salt-solutions.de/https://swugit1.salt-solutions.de/sce-public/api-doc/-/raw/master/sce/mapper/latest.yml](https://swagger-ui.apps.d4s.salt-solutions.de/?url=https://cors-anywhere.apps.d4s.salt-solutions.de/https://swugit1.salt-solutions.de/sce-public/api-doc/-/raw/master/sce/mapper/latest.yml)

Needs Basic Authentication.

### Request(JSon)

        {
            id: String, // UUID
            serviceName: String, // The name of Microservice: ups, dpd...
            configFile: String, // The name of first smooks configuration file.
            messageType: String, // edifact or csv
            encoding: String, // utf or windows
            files: [
                "filename":"filecontent"
            ]
        }

#### Response(JSon)

        {
            id: String,  // id from request
            csvResponse: { // if messageType in request is csv, otherwise is empty
                success: ["filename":"filecontent"],
                error: ["filename":"error message"]
            },
            edifactResponse: { // if messageType in request is edifact, otherwise is empty
                success: ["filename":"filecontent"],
                error: ["filename":"error message"]
            },
            statusCode: Integer // 2 HTTP error codes: BAD_REQUEST or OK
        )


