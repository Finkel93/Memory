package de.htwg.se.memory.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  // Hilfsklasse, um das Trait Observable zu testen
  class TestObservable extends Observable

  "An Observable" should {

    "notify all registered observers when notifyObservers is called" in {
      val observable = new TestObservable

      var observer1Notified = false
      var observer2Notified = false

      val observer1 = new Observer {
        def update: Unit = observer1Notified = true
      }
      val observer2 = new Observer {
        def update: Unit = observer2Notified = true
      }

      observable.add(observer1)
      observable.add(observer2)

      observable.notifyObservers

      observer1Notified shouldBe true
      observer2Notified shouldBe true
    }

    "not notify removed observers" in {
      val observable = new TestObservable

      var observer1Notified = false
      var observer2Notified = false

      val observer1 = new Observer {
        def update: Unit = observer1Notified = true
      }
      val observer2 = new Observer {
        def update: Unit = observer2Notified = true
      }

      observable.add(observer1)
      observable.add(observer2)

      observable.remove(observer2)

      observable.notifyObservers

      observer1Notified shouldBe true
      observer2Notified shouldBe false
    }

    "correctly handle multiple observers" in {
      val observable = new TestObservable

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

      observable.add(observer1)
      observable.add(observer2)
      observable.add(observer3)

      observable.notifyObservers

      observer1Notified shouldBe true
      observer2Notified shouldBe true
      observer3Notified shouldBe true
    }
  }
}
