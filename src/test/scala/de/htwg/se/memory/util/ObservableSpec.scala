package de.htwg.se.memory.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "notify all registered observers when notifyObservers is called" in {
      val observable = new Observable

      // Erstellen von zwei Test-Observers
      var observer1Notified = false
      var observer2Notified = false

      val observer1 = new Observer {
        def update: Unit = observer1Notified = true
      }
      val observer2 = new Observer {
        def update: Unit = observer2Notified = true
      }

      // Hinzufügen der Observer zum Observable
      observable.add(observer1)
      observable.add(observer2)

      // Benachrichtigen der Observer
      observable.notifyObservers

      // Überprüfen, dass beide Observer benachrichtigt wurden
      observer1Notified shouldBe true
      observer2Notified shouldBe true
    }

    "not notify removed observers" in {
      val observable = new Observable

      // Erstellen von zwei Test-Observers
      var observer1Notified = false
      var observer2Notified = false

      val observer1 = new Observer {
        def update: Unit = observer1Notified = true
      }
      val observer2 = new Observer {
        def update: Unit = observer2Notified = true
      }

      // Hinzufügen der Observer zum Observable
      observable.add(observer1)
      observable.add(observer2)

      // Entfernen von observer2
      observable.remove(observer2)

      // Benachrichtigen der Observer
      observable.notifyObservers

      // Überprüfen, dass nur observer1 benachrichtigt wurde
      observer1Notified shouldBe true
      observer2Notified shouldBe false
    }

    "correctly handle multiple observers" in {
      val observable = new Observable

      // Erstellen von drei Test-Observers
      var observer1Notified = false
      var observer2Notified = false
      var observer3Notified = false

      val observer1 = new Observer {
        def update: Unit = observer1Notified = true
      }
      val observer2 = new Observer {
        def update: Unit = observer2Notified = true
      }
      val observer3 = new Observer {
        def update: Unit = observer3Notified = true
      }

      // Hinzufügen der Observer zum Observable
      observable.add(observer1)
      observable.add(observer2)
      observable.add(observer3)

      // Benachrichtigen der Observer
      observable.notifyObservers

      // Überprüfen, dass alle Observer benachrichtigt wurden
      observer1Notified shouldBe true
      observer2Notified shouldBe true
      observer3Notified shouldBe true
    }
  }
}
