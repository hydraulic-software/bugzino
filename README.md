# Bugzino, a sample app

This app demonstrates how to write and deploy a CRUD GUI app without using web tech. It implements a simple bug tracking app. The stack is:

- [Kotlin/JVM](https://www.kotlinlang.org)
- [PostgreSQL](https://www.postgresql.org)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-mpp/)
- [Conveyor](https://www.hydraulic.software) for one-step deployment to the desktop
- [jOOQ](https://www.jooq.org) for doing queries and invoking stored procedures (it reflects the database and generates type safe wrapper code)
- [Flyway](https://www.flywaydb.org) for database migrations and setup

## Rationale

Web browsers lack many features forcing us to deal with a lot of incidental complexity that makes writing CRUD apps harder than necessary. By systematically solving the problems that push devs us into browsers, we can get a better developer and end user experience. This repo contains a demo app that can be iterated on to prove out the ideas.

## Get the client

[Download here](https://downloads.hydraulic.dev/bugzino/download.html)

**Please note that the server is currently offline pending a move to a dedicated VPS, it will be brought back online sooner or later**.

## Design

Bugzino implements two-tier design, in which a desktop app connects directly to a backend database using its native protocol (you could do the same thing from mobile). This has a variety of advantages:

1. The application is written exclusively in one language with no HTML, CSS, JSON, REST or JavaScript involved (in this project, it's Kotlin).
2. The underlying network protocol is currently Postgres, which provides built-in transactionality, remote procedure call, type safety, batching, authentication, and a variety of other features that you would normally have to layer on top of HTTP.
3. To deploy we use Conveyor in aggressive updates mode in which the application will update on startup every single time it starts just like a web app. Deployment can be done from a laptop; no CI is needed. Conveyor handles JVM bundling, packaging for each platform, signing, integration of auto update etc.

## Setup/deployment

See the dedicated [setup guide](SETUP.md).

## Database schema approach

* Each app user has their own database user, 1:1, as well as an entry in the t_users table. 
* Users are locked down and don't have direct access to any tables. They use views+RLS for reading data.
* Writes are all mediated by `security definer` stored procedures. Each stored procedure is equivalent to a REST POST endpoint. This is a conservative choice - in some cases direct updates could be allowed without compromising security but for consistency we always use procedures.
* Stored procs are defined in a mix of SQL and Kotlin, using the PL/Java extension. See the code in the `backend` module.

## User signup

Bugzino shows how to do a form of relatively open signup. Users can create a new account and a form of email validation
is implemented. Users have to enter a code that they received on their email account in order to log in and see the data.

To implement this there's a `guest` user which has a well known/public password, but it's locked down so it can
only execute a couple of functions implemented in Kotlin (see the source code in the `backend` module). These are used to start
and finish the registration process, at the end of which a new db role is created with which the user can then log in.

## AI specs

There are `.prompt.md` files scattered throughout this repository. These are specs that were fed to GPT-3/4 during development using a separate tool (not a part of this repo) which then "rendered" them to code. This repo is thus also an exploration of how best to integrate and work alongside our new AI companions.

## Database glue

We use jOOQ with Kotlin extensions+coroutines support. It reflects db schemas and generates code that can be used to work with the database in a type-safe way. 

jOOQ can generate data holder classes from the database but we don't use them because it often generates nullable fields, and data classes are easy to define so we don't lose much by doing our own.

# To do

- Add user info like name, photo.
- Query the server on every change to get the updated list of ticket summaries.
- Fix the vscrollbar on the description field.
