package ch.makery.app.view
import ch.makery.app.MainApp
import scalafxml.core.macros.sfxml

@sfxml
class UserGuidePageController{

  def getBack(): Unit = {
    MainApp.showWelcomePage()


  }
}