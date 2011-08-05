package edu.cornell.indexbuilder.discovery

trait UriDiscoveryState {

  //Indicate if this has persistant state to laod
  def hasSavedState():Boolean

  //Return true if there is discovery work left to do
  def isDiscoveryComplete():Boolean

  /* ********** DiscoveryMessage ************* */

  //Save that a request discovery message is need but not yet done
  def saveTheNeedTo( msg: DiscoveryMessage ):Unit

  //Record that a request discovery message has been completed
  def recordCompleted( msg: DiscoveryMessage ):Unit

  //get the list of not yet done request discovery messages
  def getNeeded():Set[DiscoveryMessage]

  //Save that a uri has been discovered
  def saveDiscoveredUri( uri:String ):Unit

  // Save that an error has happend while trying to
  // process a request discovery message.
  // This should probably remove the msg from the list of
  // needed DiscoveryMessages.
  def errorFor( msg: DiscoveryMessage,  errMsg:String):Unit
  
  /* *********** URIs ************ */

  //record that a uri has had all required work done for it.
  def recordCompletedUri( uri:String ):Unit

  // Save that an error has happend while trying to
  // process a URI.
  // This should probably remove the URI from the list of
  // URIs to index.
  def errorForUri( uri:String, msg:String):Unit

  //get the list of not yet completed uris
  def getUris():Iterator[String]
}
