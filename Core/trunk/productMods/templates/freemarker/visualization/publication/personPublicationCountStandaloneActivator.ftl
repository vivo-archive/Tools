<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign googleJSAPI = 'http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>

${headScripts.add(googleJSAPI)}

<#include "/visualization/publication/personPublicationSparklineContent.ftl">