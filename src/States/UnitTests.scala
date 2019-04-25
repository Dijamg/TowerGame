
package States

import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.Buffer
import java.io.BufferedReader
import java.io.FileReader

/* This tests test that the PlayState class reads the files correctly and stores
 * correct values into a buffer. readFile method is copied from the PlayState class
 * because I had trouble accessing it from here.
 */
class UnitTests {
  
  // This method is identical to the code in the PlayState class.
  def readFile(name: String): Unit = {
    try{
      val in = new BufferedReader(new FileReader(name));
      var str = ""
      
      str = in.readLine()
      while(str != null){
        this.probabilitiesFile += str
        str = in.readLine()
      }
      val values = (this.probabilitiesFile.map(a => a.split(","))).map(a => a.map(_.toInt))
      val isValid = values.forall(a => a.size == 3 && a.sum == 100)
      if(isValid){
        this.probabilities = this.probabilitiesFile
      }else{
        this.probabilities = this.probabilitiesDefault
      }
    }catch{
      case e: Exception => this.probabilities = this.probabilitiesDefault  // We do the same thing no matter what exception it will throw.
    }
  }
  
  
  val probabilitiesDefault = Buffer("50,50,0", "50,50,0", "40,40,20", "30,30,40")
  val probabilitiesFile = Buffer("49,49,2", "50,50,0", "40,40,20", "30,30,40")
  var probabilities = Buffer[String]()
  
  @Test def testValidFile{
    this.readFile("testData.txt")
    assertEquals(probabilities, probabilitiesFile)
  }
  
  @Test def testMissingFile{
    this.readFile("FAKEFILE")
    assertEquals(probabilities, probabilitiesDefault)
  }
  
  @Test def testInvalidFile{
    this.readFile("InvalidTestData.txt")
    assertEquals(probabilities, probabilitiesDefault)
  }
}