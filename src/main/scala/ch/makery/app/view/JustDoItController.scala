package ch.makery.app.view
import ch.makery.app.MainApp
import ch.makery.app.model.Task
import scalafx.scene.control.{Alert, TextField}
import scalafx.scene.control.Alert.AlertType
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import scala.util.{Failure, Success}

@sfxml
class JustDoItController(private val text1: TextField) {

  def getDone(): Unit = {
    // Get the text from the text field
    val taskDescription = text1.getText

    // Create a new Task object
    val newTask = new Task(taskDescription, "") // Assuming Task takes two strings

    // Show the TaskEditDialog for potential edits (if needed)
    val okClicked = MainApp.showTaskEditDialog(newTask)

    if (okClicked) {
      // Add the task to the taskData list
      MainApp.taskData += newTask

      // Save the task to the database with error handling
      newTask.save() match {
        case Success(u) =>
          // Successfully saved, now navigate to the History page
          MainApp.showHistory()

        case Failure(e) =>
          // Handle the failure case
          new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failed to add task"
            headerText = "The task could not be added."
            contentText = "Error "
          }.showAndWait()
      }
    }
  }

  def getBack(): Unit = {
    MainApp.showWelcomePage()
  }
}
