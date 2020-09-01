package de.salt.sce.mapper.server.communication.model


/**
 * Generic Mapper Request
 */
case class MapperRequest(id: String, // UUID
                         serviceName: String, // The name of Microservice: ups, dpd...
                         configFile: String, // The name of first smooks configuration file.
                         messageType: String, // edifact or cvs
                         encoding: String, // utf or windows
                         files: Map[String, String] // [file name, file content]
                        )

