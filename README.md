# FP can do CRUD too

## Functional Programming

Sometimes we can be an academic crowd in the FP community. And that's awesome and it's totally fine. I mean Monads are just monoids in the category of endo-functors after all right? Or they're burritos, I forget. Plus we've got combinators and they're awesome too. And Debating the relative values of type systems is interesting. All of that makes us better programmers whether we're doing FP or imperative programming.

## CRUD

But can I just build a web app that does some basic CRUD? What I want to do is try and bridge the gap in this talk. Using Scala and the Play Framework, we can explore building a web application from the front-end to back-end using Scala and functional techniques.

Now Scala is a hybrid functional / imperative / OO language. So we could do everything in a more traditional imperative / OO style if we wanted to. But I'm deliberately trying to go the other way and show how we use some of those interesting FP ideas to do everyday things that many of us probably do in our day jobs.

## What's Full Stack?
### Fundamentally made up of 3 things

These days we often use "Full Stack" frameworks for building web apps and I thought it would be important to talk about what that means.
My proposition is that these are the 3 areas of any full-stack web framework that you care about. When we're going to talk about building a web app, we're going to end up talking about all of these layers in some way.

### Data Presentation

How do we show things to a user. This is where templates come into play. Or your new-fangled javascript frameworks like Ember or Backbone.

### Application Logic

How do we add logic, transform data, and the like? We need a way to apply business rules and implementation details in a reasonable way. All of that stuff lives in this category.

### Data Storage

And then there's data storage. If someone puts something in the system, there has to be a way to save it and retrieve it later. Generally we deal with databases whether that's SQL or NoSQL, but it could be implemented as a REST API as well, for example. I guess we could store things in flat files too if we wanted. You get the point. Most apps, especially boring ones like what we want to do, will use a SQL database right?


### Glue (or 4th part?)

So maybe I fibbed a bit, sometimes we also have a bit of a 4th part. We need a way to translate between the language of the Web, URLs, and something that works for users. So this is how we handle requests, and mediate between our domain logic, data access, and the display of data to the user. Most modern web frameworks have the idea of a Controller to sit in that area between the other layers and glue them all together. That's kind of the Application Logic layer and kind of its own thing.


## Play Framework

Today we'll specifically talk about those layers and how they're implemented in the Play Framework.
Play is a Scala based, full-stack Web framework built on top of some pretty powerful technologies that come from the JVM ecosystem. Things like Netty (which is a Java asynchronous IO framework) and Akka and some things like that.


## What I'm not going to cover, but yeah it has that too
  * You can do stuff with XML
  * Akka
  * Really good Testing support 
  * Yes it's got a Cache service
  * Yes it's got Internationalization support
  * Yes it's got Asset Compilation (Coffeescript, LESS)
  * Yes it's got a build tool and dependency management (Sbt)

Just a quick aside. There's a lot of stuff that many web frameworks help with that fall into what might be called non-functional requirements support. Caching, Internationaliztion, Asset compilation, and things like that.

Scala has support for XML built in. You can just write XML in your scala and the compiler handles it.

Akka is used under the covers, but you can also use it yourself to create Erlang like Actors for loosley coupled, concurrent designs. Akka is a big enough subject that it would deserve it's own presentation. So I'm not going to do anythign with it directly today, but know that when you use Futures and Async in Play you're using Akka under the covers.

Play supports testing out of the box using Spec2 which looks a lot like Spock in Groovy or Rspec in Ruby. It's got built in Fake Requests for fast functional testing of  controllers and all kinds of goodness like that.

That's all I'm going to say about that stuff. If you need it, yeah, it's there.

## But we still can keep it Real

But just because we're going to build a totally boring application doesn't mean we can't use some interesting techniques to do it.
So let's see about bringing together the academic stuff and the everyday stuff that many of us do...


--------------

So rather than try and live code this thing, I'm going to do this like a cooking show and cut up a few things and pull some TV magic and take things out of the oven that are already kind of baked up.


## Create an application

Like many modern web frameworks, Play comes with a command line tool to make new applications. It can create files to integrate with yoru IDE and pulls down all the dependencies and gets everything setup. Unlike with Rails or Grails, the play command is not used for creating controllers or models or things like that. That's all I'm going to say about it because it's really not super interesting.

## Database
  * uncomment db configuration
  * create an evolution directory
  * create an evolution

Evolutions are the equivlent of Rails migrations. They have some interesting differences though. The up and down are stored in the database and changes to them are automatically detected and can be reapplied if they are changed. I'm also not going to talk much about this, since it doesn't have a lot to do with FP.

## Create data access class
  * New Package 'data'
  * New Scala Class Contact
  * Make a type to hold the Contact data
  * Make a Contact repository trait and a DbContactRepository object

We're using Anorm because it's installed out-of-the box and integrates with the evolutions support. There are other libraries such as Slick. Slick is more ORM and seems like it might become "The One True Way" at some point, but Anorm is interesting because it's just SQL and FP. For now we're just going to stub it out since it's kind of weird to implement it from the back forward.


## Add a route
  * Create a controller
  * Add some routes
  * Add some views

Controllers, as in most MVC framworks, are the glue between a domain, or domain logic, and the presentation of data. Also we need a way to map URLs into our controllers. One of the features of Play is that almost everything is strongly-typed. So even the Routes are compiled and type-checked at compile time. This allows for the generation of routes and reverse routes that are themselves available for use in the application and can be type-checked at compile time.

We make use of the Cake Pattern which gives us the ability to isolate dependencies for testing without having to use crazy frameworks for Dependency Injection.

We could create a trait or an abstract base class and inherit from that. But "Geoff" you might say "That's just plain old Object Oriented Programming and I can do that in Java or C#. You said this would be about Functional Programming." OK, the let's do something different.:w

Scala allows us to do Types differently. We don't just have to do inheritance or explicitly implement interfaces. We can define structural types and do compiler type-checked Duck Typing too.

You have access to the Request and the body of the request, so you could grab data that way, but we can also create Forms that map between the POSTs and some structs.

GET requests are maybe more interesting, because we can define the parameters as arguments to the Controller function that handles the request. Remember, our routes are strongly typed, so they'll ensure that we've defined them properly.


## Implement DB Access
  * Show some easy row selectors
  * Show DataMapper

So this is just SQL, with some nice ways of processing the results. It handles all the things you need like parameter substitution.
You can do very simple pattern matching with the Rows returned. Of course if you have a lot of queries for the same objects, then those will get repetitive really quickly. This is where RowMappers come into play.

Anorm does require writing SQL manually. That's both it's strength and weakness. You have complete control, but you do seem to be writing the names of parameters over and over. One of the interesting features in Slick is that they plan to use the meta-data about the mapping and do type-checking at compile time to help catch typos and whatnot.


## JSON API
  * Content Negotiation, we can do that too
  * Create Writes as an OO style
  * Create Writes with a functional Combinator style

One of Play's strengths is APIs. It's got content negotiation, JSON, and XML support. That combined with the fact that it's super scalable and can handle asynchronous operations make it a really nice platform for those kinds of applications. Let's say you want to write your front-end in Angular or Backbone (or whatever the New Hotness is). We can easily add JSON responses to our controller.

We just need to do some Content negotiation checks and then serialize our data to the appropriate format.

Formal definition:
A combinator is a higher-order function that uses only function application and earlier defined combinators to define a result from its arguments.

Think of this as a functional Builder where the combinator returns the builder that makes the Writes

curl -H "Accept: application/json" http://localhost:9000/contacts/1

## Consuming APIs
  * WS support

Consuming APIs is just as easy with the built in WS package. What we're doing here is crazy, but my example broke down and I still wanted to show of Async and consuming a RESTful service.



