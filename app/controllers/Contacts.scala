package controllers

import data.DbContactRepository
import models.Contact
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import scala.language.reflectiveCalls
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._

/**
 * @author geoff
 * @since 8/30/13
 */
object Contacts extends Controller with ContactsController {
  val contactRepository = DbContactRepository

}

trait ContactsController {
  this: Controller =>

  type ContactRepository = {
    def save(contact: Contact): Option[Contact]
    def get(id: Long): Option[Contact]
    def list(): Seq[Contact]
  }

  val contactRepository: ContactRepository

  def list() = Action {
    val contacts = contactRepository.list()
    Ok(views.html.Contacts.list(contacts))
  }

  implicit val contactWrites = (
      (__ \ "id").write[Option[Long]] ~
      (__ \ "name").write[String] ~
      (__ \ "email").write[String] ~
      (__ \ "isFavorite").write[Boolean]
    )(unlift(Contact.unapply))

  implicit val contactReads = (
      (__ \ "id").read[Option[Long]] ~
      (__ \ "name").read[String] ~
      (__ \ "email").read[String] ~
      (__ \ "isFavorite").read[Boolean]
    )(Contact.apply _)

  def get(id: Long) = Action { implicit request =>
    render {
      case Accepts.Html() => {
        Async {
          WS.url("http://localhost:9000/contacts/%d".format(id))
            .withHeaders("Accept" -> "application/json")
            .get().map { response =>
            Json.fromJson(response.json).asOpt match {
              case Some(c:Contact) => Ok(views.html.Contacts.show(c))
              case None => NotFound(views.html.index.render("Not found"))
            }
          }
        }
      }
      case Accepts.Json() => contactRepository.get(id) match {
        case Some(c) => Ok(Json.toJson(c))
        case None => NotFound(Json.toJson("No such contact"))
      }
    }
  }


  val contactForm: Form[Contact] = Form[Contact](
    mapping(
      "name" -> text.verifying(nonEmpty),
      "email" -> text.verifying(nonEmpty)
    )
      ((name, email) => Contact(None, name, email, false))
      ((c: Contact) => Some(c.name, c.email))
  )

  def create = Action {
    Ok(views.html.Contacts.create(contactForm))
  }

  def save = Action { implicit request =>
    contactForm.bindFromRequest.fold(
      invalid => BadRequest(views.html.Contacts.create(invalid)),
      valid => {
        contactRepository.save(valid) match {
          case Some(c) => Redirect(routes.Contacts.get(c.id.get))
          case None => InternalServerError(views.html.Contacts.create(contactForm))
        }
      }
    )
  }
}
