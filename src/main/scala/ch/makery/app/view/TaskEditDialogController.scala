package ch.makery.app.view
import ch.makery.app.model.Task
import ch.makery.app.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import ch.makery.app.util.DateUtil._
import scalafx.event.ActionEvent

@sfxml
class TaskEditDialogController(
                                private val taskNameField: TextField,
                                private val taskStatusField: TextField,
                                private val deadlineField: TextField,
                                private val additionalInformationField: TextField
                              ) {

  var dialogStage: Stage = null
  private var _task: Task = null
  var okClicked = false

  def task: Task = _task
  def task_=(x: Task) {
    _task = x

    taskNameField.text = _task.taskName.value
    taskStatusField.text = _task.taskStatus.value
    deadlineField.text = _task.date.value.asString
    deadlineField.setPromptText("dd.mm.yyyy")
    additionalInformationField.text = _task.additionalInformation.value;

  }

  def handleOk(action: ActionEvent) {
    if (isInputValid()) {
      _task.taskName <== taskNameField.text
      _task.taskStatus <== taskStatusField.text
      _task.date.value = deadlineField.text.value.parseLocalDate
      _task.additionalInformation <== additionalInformationField.text;

      okClicked = true;
      dialogStage.close()
    }
  }

  def handleCancel(action: ActionEvent){
    dialogStage.close();
  }

  def nullChecking (x : String) = x == null || x.length == 0

  def isInputValid(): Boolean = {
    var errorMessage = ""

    if (nullChecking(taskNameField.text.value))
      errorMessage += "No valid task name!\n"
    if (nullChecking(taskStatusField.text.value))
      errorMessage += "No valid task status!\n"
    if (nullChecking(deadlineField.text.value))
      errorMessage += "No valid deadline!\n"
    else {
      if (!deadlineField.text.value.isValid) {
        errorMessage += "No valid deadline. Use the format dd.mm.yyyy!\n"
      }
    }
    if (nullChecking(additionalInformationField.text.value))
      errorMessage += "No valid additional information!\n"

    if (errorMessage.length() == 0) {
      return true;
    } else {
      val alert = new Alert(Alert.AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()
      return false;
    }
  }
}