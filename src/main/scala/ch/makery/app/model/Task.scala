package ch.makery.app.model
import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate
import ch.makery.app.util.Database
import ch.makery.app.util.DateUtil._
import scalikejdbc._
import scala.util.{ Try, Success, Failure }

class Task (val taskNameS : String, val taskStatusS : String) extends Database {
  def this()     = this(null, null)
  var taskName  = new StringProperty(taskNameS)
  var taskStatus   = new StringProperty(taskStatusS)
  var additionalInformation     = new StringProperty("-")
  var date      = ObjectProperty[LocalDate](LocalDate.of(2024, 7, 10))

  // Method to update the date to a new value
  def updateDate(newDate: LocalDate): Unit = {
    date.set(newDate)
  }



  def save() : Try[Int] = {
    if (!(isExist)) {
      Try(DB autoCommit { implicit session =>
        sql"""
					insert into task (taskName, taskStatus,
						additionalInformation,date) values
						(${taskName.value}, ${taskStatus.value}, ${additionalInformation.value},
						 ${date.value.asString})
				""".update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
				update task
				set
				taskName  = ${taskName.value} ,
				taskStatus   = ${taskStatus.value},
				additionalInformation     = ${additionalInformation.value},
				date       = ${date.value.asString}
				 where taskName = ${taskName.value} and
				 taskStatus = ${taskStatus.value}
				""".update.apply()
      })
    }

  }
  def delete() : Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
				delete from task where
					taskName = ${taskName.value} and taskStatus = ${taskStatus.value}
				""".update.apply()
      })
    } else
      throw new Exception("Task not Exists in Database")
  }
  def isExist : Boolean =  {
    DB readOnly { implicit session =>
      sql"""
				select * from task where
				taskName = ${taskName.value} and taskStatus = ${taskStatus.value}
			""".map(rs => rs.string("taskName")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }

  }
}
object Task extends Database{
  def apply (
              taskNameS : String,
              taskStatusS : String,
              additionalInformationS : String,
              dateS : String
            ) : Task = {

    new Task(taskNameS, taskStatusS) {
      additionalInformation.value     = additionalInformationS
      date.value       = dateS.parseLocalDate
    }

  }
  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
			create table task (
			  id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
			  taskName varchar(64),
			  taskStatus varchar(64),
			  additionalInformation varchar(200),
			  date varchar(64)
			)
			""".execute.apply()
    }
  }

  def getAllTasks : List[Task] = {
    DB readOnly { implicit session =>
      sql"select * from task".map(rs => Task(rs.string("taskName"),
        rs.string("taskStatus"),rs.string("AdditionalInformation"),
        rs.string("date") )).list.apply()
    }
  }
}
