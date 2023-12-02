package `me.benetis`
package testix.sbt

import sbt.testing._

object TestixFramework extends Framework {
  def name(): String = "Testix"
  
  def fingerprints(): Array[Fingerprint] = Array(TestixFingerprint)
  
  def runner(args: Array[String], remoteArgs: Array[String], testClassLoader: ClassLoader): Runner = 
    new TestixRunner(args, remoteArgs, testClassLoader)
  
}
