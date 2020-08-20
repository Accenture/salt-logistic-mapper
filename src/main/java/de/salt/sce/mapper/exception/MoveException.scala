package de.salt.sce.mapper.exception

class MoveException(val message: String, val edcid: String, val actorName: String) extends Exception(message){
  
}