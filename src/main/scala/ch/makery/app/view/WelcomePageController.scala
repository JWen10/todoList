package ch.makery.app.view

import ch.makery.app.MainApp
import scalafxml.core.macros.sfxml

@sfxml
class WelcomePageController{
  def getLogIn(): Unit = {
    MainApp.showJustDoIt()

  }
  def getHistory(): Unit = {
    MainApp.showHistory()
  }

  def getUserGuide(): Unit = {
    MainApp.showUserGuide()

  }
}