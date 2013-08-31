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
      SQL("INSERT INTO Contact (name, email, isFavorite) VALUES({name}, {email}, {isFavorite});")
        .on("name" -> contact.name, "email" -> contact.email, "isFavorite" -> contact.isFavorite)
        .executeInsert[Option[Long]]()
        .map(i => Contact(Some(i), contact.name, contact.email, contact.isFavorite))
    }
  }

  val contactRowMapper = {
    long("id") ~
      str("name") ~
      str("email") ~
      bool("isFavorite") map {
      case i~n~k~f => Contact(Some(i), n, k, f)
    }
  }

  def get(id: Long): Option[Contact] = {
    DB.withConnection { implicit conn =>
      SQL("SELECT id, name, email, isFavorite FROM Contact WHERE id = {id};")
        .on("id" -> id)
        .as(contactRowMapper.singleOpt)
    }
  }

  def list(): Seq[Contact] = {
    DB.withConnection { implicit conn =>
      SQL("SELECT id, name, email, isFavorite FROM Contact;")
        .list(contactRowMapper)
    }
  }

}
