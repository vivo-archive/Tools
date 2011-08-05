package edu.cornell.indexbuilder.discovery


/** These are the messages used by the discovery system. */
trait DiscoveryMessage

/** Mesages that encodes a request */
trait DiscoveryRequestMessage extends DiscoveryMessage

/** Message that encodes a response */
trait DiscoveryReplyMessage extends DiscoveryMessage

/*
 * Ask for a site to be indexed.
 * Asynchronous. Worker will send out DiscoverUrisForClass messages.
 * siteBaseUrl should not end with a /
 */
case class DiscoverUrisForSite ( siteBaseUrl : String) extends DiscoveryRequestMessage

/*
 * Ask for a site to have its individuals of vclass indexed.
 * Asynchronous. Worker will send out DiscoverUrisForClassPage to self.
 */
case class DiscoverUrisForClass( siteBaseUrl:String, classUri:String) extends DiscoveryRequestMessage

/*
 * Ask for a page from the vclass search to be retreived and have its URIs indexed.
 * Asynchronous.  Worker will send out IndexUris messages to Master.
 */
case class DiscoverUrisForClassPage( siteBaseUrl:String, classUri:String, pageUrl:String) extends DiscoveryRequestMessage

/*
 * Ask for a page to be retrieved and have its URIs indexed.
 * Asynchronous.
 */
case class DiscoverUrisForPage( siteBaseUrl:String, pageUrl:String) extends DiscoveryRequestMessage

/**
 * A reply that indicates that
 * the list of uris has been discovered for a site */
case class URIsDiscovered( siteBaseUrl:String, uris: List[String] ) extends DiscoveryReplyMessage

/**
 * A reply that indicates that the URI discovery phase is complete for the site.
 */
case class DiscoveryComplete( siteBaseUrl:String ) extends DiscoveryReplyMessage

