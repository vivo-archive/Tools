package edu.cornell.indexbuilder.discovery
import scala.collection.mutable.HashSet

/**
 * An in memory, non-persistant discovery state.
 */
class MemDiscoveryState extends UriDiscoveryState{
  var todo = new HashSet[DiscoveryMessage]()
  var uris =  HashSet[String]()
  var discoveryErrors = new HashSet[(DiscoveryMessage,String)]()
  var uriErrors = new HashSet[(String,String)]

  def  hasSavedState():Boolean = { false }

  def isDiscoveryComplete():Boolean = {
    this.synchronized{
      todo.isEmpty
    }
  }

  def saveTheNeedTo( msg:DiscoveryMessage){
    this.synchronized{
      todo.add( msg )
    }
  }

  def recordCompleted(msg:DiscoveryMessage){
    this.synchronized{
      todo.remove(msg)
    }
  }

  def getNeeded():Set[DiscoveryMessage] = {
    this.synchronized{
      //return a immutable version of our set
      todo.toSet[DiscoveryMessage]
    }
  }

  def saveDiscoveredUri( uri:String ){
    this.synchronized{
      uris.add( uri )
    }
  }

  def errorFor( msg: DiscoveryMessage, errMsg:String){
    this.synchronized{
      discoveryErrors.add( (msg,errMsg) )
      todo.remove(msg)
    }
  }

  def recordCompletedUri( uri:String){
    this.synchronized{
      uris.remove(uri)
    }
  }

  def errorForUri( uri:String, msg:String){
    this.synchronized{
      uriErrors.add((uri,msg))
      uris.remove(uri)
    }
  }

  def getUris():Iterator[String] = {
    this.synchronized{
      uris.toIterator
    }
  }

}
