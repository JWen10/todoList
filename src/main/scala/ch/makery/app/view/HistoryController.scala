package ch.makery.app.view
import ch.makery.app.model.Task
import ch.makery.app.MainApp
import scalafx.scene.control.{Alert, Label, TableColumn, TableView}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.StringProperty
import ch.makery.app.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType

import scala.util.{Failure, Success}


@sfxml
class HistoryController(

                         private val taskTable : TableView[Task],

                         private val taskNameColumn : TableColumn[Task, String],

                         private val taskStatusColumn : TableColumn[Task, String],

                         private val taskNameLabel : Label,

                         private val taskStatusLabel : Label,

                         private val additionalInformationLabel : Label,

                         private val  dateLabel : Label

                       ) {
  // initialize Table View display contents model
  taskTable.items = MainApp.taskData
  // initialize columns's cell values
  taskNameColumn.cellValueFactory = {_.value.taskName}
  taskStatusColumn.cellValueFactory  = {_.value.taskStatus}



  private def showTaskDetails (task : Option[Task]) = {
    task match {
      case Some(task) =>
        // Fill the labels with info from the task object.
        taskNameLabel.text <== task.taskName
        taskStatusLabel.text  <== task.taskStatus
        additionalInformationLabel.text    <== task.additionalInformation
        dateLabel.text=task.date.value.asString

      case None =>
        // Task is null, remove all the text.
        taskNameLabel.text.unbind()
        taskStatusLabel.text.unbind()
        additionalInformationLabel.text.unbind()
        dateLabel.text.unbind()
        taskNameLabel.text = ""
        taskStatusLabel.text  = ""
        additionalInformationLabel.text = ""
        dateLabel.text= ""

    }
  }


  showTaskDetails(None)//Will show none when the program run

  taskTable.selectionModel.value.selectedItem.onChange((_,_,newValue)=>{
    showTaskDetails(Option(newValue))
  })
  def handleDeleteTask(action : ActionEvent) = {
    val selectedIndex = taskTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      MainApp.taskData.remove(selectedIndex).delete()match{
        case Success(e) =>

        case Failure(e) =>
          new Alert(AlertType.Warning){
            initOwner(MainApp.stage)
            title       = "Fail to remove."
            headerText  = "Fail to remove the task."
            contentText = "Error database."
          }.showAndWait()
      }

    }
    else {
      // Nothing selected.
      new Alert(AlertType.Warning){
        initOwner(MainApp.stage)
        title       = "No Selection"
        headerText  = "No Task Selected"
        contentText = "Please select a task in the table."
      }.showAndWait()
    }
  }
  def handleAddTask(action : ActionEvent) = {
    val task = new Task("","")
    val okClicked = MainApp.showTaskEditDialog(task);
    if (okClicked) {
      MainApp.taskData += task
      task.save() match{
        case Success(u) =>

        case Failure(e) =>
          new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Fail to add."
            headerText = "Fail to add the task."
            contentText = "Error database."
          }.showAndWait()
      }
    }
  }

  def handleEditTask(action : ActionEvent) = {
    val selectedTask = taskTable.selectionModel().selectedItem.value
    if (selectedTask != null) {
      val okClicked = MainApp.showTaskEditDialog(selectedTask)

      if (okClicked) {
        showTaskDetails(Some(selectedTask))
        selectedTask.save()match { //will save the updated value to the database
          case Success(u) =>

          case Failure(e) =>
            new Alert(AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Fail to update."
              headerText = "Fail to update the task."
              contentText = "Error database."
            }.showAndWait()
        }//will save the updated value to the database
      }

    } else {
      // Nothing selected.
      val alert = new Alert(Alert.AlertType.Warning){
        initOwner(MainApp.stage)
        title       = "No Selection"
        headerText  = "No Task Selected"
        contentText = "Please select a Task in the table."
      }.showAndWait()
    }
  }
  def handleDone(action: ActionEvent): Unit = {
    // Stop the application
    MainApp.stage.close()
  }



}