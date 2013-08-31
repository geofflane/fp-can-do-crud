package data

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models.Contact


object DbContactRepository {
  def save(contact: Contact): Option[Contact] = ???

  def get(id: Long): Option[Contact] = ???

  def list(): Seq[Contact] = ???

}
