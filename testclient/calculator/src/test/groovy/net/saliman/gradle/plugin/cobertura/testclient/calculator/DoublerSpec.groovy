package net.saliman.gradle.plugin.cobertura.testclient.calculator;
import spock.lang.Specification

class DoublerSpec extends Specification {
  def "doubles?"() {
    when:
	  def i = Doubler.doubleIt(3)
	then:
	  i == 6
  }
}
