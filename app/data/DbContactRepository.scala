package data

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models.Contact
import scala.Option

object DbContactRepository {

  def save(contact: Contact): Option[Contact] = {
    DB.withConnection { implicit conn =>
      val id:Option[Long] = SQL("INSERT INTO Contact (name, email, isFavorite) VALUES({name}, {email}, {isFavorite});")
        .on("name" -> contact.name, "email" -> contact.email, "isFavorite" -> contact.isFavorite)
        .executeInsert()

      id.map(i => Contact(Some(i), contact.name, contact.email, contact.isFavorite))
    }
  }

  def get(id: Long): Option[Contact] = {
    DB.withConnection { implicit conn =>
      SQL("SELECT id, name, email, isFavorite FROM Contact WHERE id = {id};")
        .on("id" -> id)
        .single {
          case Row(i:Long, n:String, e:String, f:Boolean) => Some(Contact(Some(i), n, e, f))
          case _ => None
        }
    }
  }

  def list(): Seq[Contact] = ???

}
