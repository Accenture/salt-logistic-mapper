SCE Mapper
==========

Mapper Service

A service to mapping edifact or csv files to Transport and PacketCSV objects using Smooks library.
It uses akka builtin server.

# API Docs

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


