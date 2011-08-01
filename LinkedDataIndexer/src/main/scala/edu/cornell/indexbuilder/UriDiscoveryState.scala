package edu.cornell.indexbuilder
import akka.event.EventHandler


class UriDiscoveryState(workingDirectory:String) {
  def setupInitialState( ) :Unit = {
  }

  def hasSavedState():Boolean = {
    false    
  }

  def isDiscoveryComplete():Boolean = {
    false
  } 

  def isDiscoveryCompleteForClass(classUri:String):Boolean = {
    false
  }

  def pagesDiscoveredForClass( classUri:String ){
    EventHandler.error(this,"pagesDiscoveredForClass not implemented!!!")
  }
  
  def savePageToState( pageMsg:DiscoverUrisForClassPage ) :Unit = {
    EventHandler.error(this,"savePageToState not implemented!!!")
  }

  def saveUrisToState( pageUrl:String, uriMsg:IndexUris):Unit ={
    //remove pageUrl from todo list
    urisDiscoveredForPage( pageUrl )
    //add uriMsg to todo list
    EventHandler.error(this,"saveUrisToState not implemented!!!")
  }

  def urisDiscoveredForPage( pageUri:String){
    EventHandler.error(this,"urisDiscoveredForPage not implemented!!!")
  }

}
