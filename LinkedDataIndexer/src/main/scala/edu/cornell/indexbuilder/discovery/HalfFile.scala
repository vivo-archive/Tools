
import java.io.FileOutputStream

import scala.io.Source
import scala.tools.nsc.io.Path



/**
 * strip odd characters from Harvard's file
 */

object HalfFile{

  def main(args : Array[String]) : Unit = {
    HalfFile.halfFile( args(0) ) 
  }
  
  /* Take file, open new file called half.fileName, write every non NULL byte to that file */
  def halfFile( fileName:String )={
    val buffer = new Array[Byte](1024)
    val out =  new FileOutputStream( "half."+fileName )
    Source.fromFile(fileName).filter( c => c > 0 ).map( out.write(buffer,0,_))
    out.close()
  }
}
