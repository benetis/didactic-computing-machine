package `me.benetis`.testix.sbt

import sbt.testing._

object TestixFingerprint extends SubclassFingerprint {
  def isModule(): Boolean = true
  
  def superclassName(): String = ???
  def requireNoArgConstructor(): Boolean = ???  
  
}
